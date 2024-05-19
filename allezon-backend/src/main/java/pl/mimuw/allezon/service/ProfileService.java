package pl.mimuw.allezon.service;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.ResultCode;
import com.aerospike.client.policy.CommitLevel;
import com.aerospike.client.policy.GenerationPolicy;
import com.aerospike.client.policy.WritePolicy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.UserTag;
import pl.mimuw.allezon.dto.request.UserTagEvent;
import pl.mimuw.allezon.dto.response.UserProfileResponse;
import pl.mimuw.allezon.jpa.entity.ProfileEntity;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class ProfileService {

    private final AerospikeTemplate aerospikeTemplate;
    private final KafkaTemplate<String, UserTagEvent> kafkaTemplate;

    public void addUserTag(final UserTagEvent userTag) {
        kafkaTemplate.send(Constants.USER_TAG_TOPIC, userTag);

        while (true) {
            final ProfileEntity profile = getOrCreateProfile(userTag);
            final ProfileEntity updatedProfile = getUpdatedProfile(profile, userTag);
            final WritePolicy writePolicy = createWritePolicy(profile);

            try {
                aerospikeTemplate.persist(updatedProfile, writePolicy);
                break;
            } catch (final RecoverableDataAccessException up) {
                if (up.getCause() instanceof AerospikeException cause) {
                    if (cause.getResultCode() != ResultCode.GENERATION_ERROR) {
                        throw up;
                    }
                }
                log.warn("Generation error for cookie={}", userTag.getCookie());
            }
        }
    }

    public UserProfileResponse getUserProfile(final String cookie, final String timeRange, final int limit) {
        final String[] timeRanges = timeRange.split("_");
        final long timeBegin = parseTimestamp(timeRanges[0]);
        final long timeEnd = parseTimestamp(timeRanges[1]);

        final ProfileEntity profile = getUserProfileByCookie(cookie);
        final List<UserTagEvent> views = filterUserTags(profile.getViews(), cookie, timeBegin, timeEnd, limit);
        final List<UserTagEvent> buys = filterUserTags(profile.getBuys(), cookie, timeBegin, timeEnd, limit);

        return UserProfileResponse.builder()
                .cookie(cookie)
                .views(views)
                .buys(buys)
                .build();
    }

    private List<UserTagEvent> filterUserTags(final List<UserTag> views, final String cookie,
                                              final long timeBegin, final long timeEnd, final int limit) {
        return views.stream()
                .filter(userTag -> timeBegin <= userTag.getTimestamp() && userTag.getTimestamp() < timeEnd)
                .limit(limit)
                .map(userTag -> userTag.toUserTagEvent(cookie))
                .toList();
    }

    private ProfileEntity getUserProfileByCookie(final String cookie) {
        return Optional.ofNullable(aerospikeTemplate.findById(cookie, ProfileEntity.class))
                .orElseThrow(RuntimeException::new);
    }

    private ProfileEntity getOrCreateProfile(final UserTagEvent userTag) {
        return Objects.requireNonNullElse(
                aerospikeTemplate.findById(userTag.getCookie(), ProfileEntity.class),
                new ProfileEntity(userTag.getCookie(), 0, List.of(), List.of())
        );
    }

    private ProfileEntity getUpdatedProfile(final ProfileEntity profile, final UserTagEvent userTag) {
        final List<UserTag> updatedViews = updateTagsIfNeeded(profile.getViews(), userTag, Action.VIEW);
        final List<UserTag> updatedBuys = updateTagsIfNeeded(profile.getBuys(), userTag, Action.BUY);

        return new ProfileEntity(userTag.getCookie(), profile.getGeneration(), updatedViews, updatedBuys);
    }

    private List<UserTag> updateTagsIfNeeded(final List<UserTag> userTags, final UserTagEvent userTag, final Action action) {
        if (userTag.getAction().equals(action)) {
            return addNewTagToProfile(userTags, userTag);
        }
        return userTags;
    }

    private List<UserTag> addNewTagToProfile(final List<UserTag> userTags, final UserTagEvent userTagEvent) {
        return Stream.concat(userTags.stream(), Stream.of(userTagEvent.toUserTag()))
                .sorted(Comparator.comparing(UserTag::getTimestamp).reversed())
                .limit(Constants.MAX_PROFILE_SIZE)
                .toList();
    }

    private WritePolicy createWritePolicy(final ProfileEntity profile) {
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.socketTimeout = 15000;
        writePolicy.totalTimeout = 35000;
        writePolicy.commitLevel = CommitLevel.COMMIT_MASTER;
        writePolicy.generation = profile.getGeneration();
        writePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
        writePolicy.maxRetries = 100;
        return writePolicy;
    }

    private long parseTimestamp(final String timestamp) {
        return Instant.parse(timestamp + "Z").toEpochMilli();
    }
}
