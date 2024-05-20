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
import pl.mimuw.allezon.domain.UserTagMessage;
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
    private final KafkaTemplate<String, UserTagMessage> kafkaTemplate;

    public void addUserTag(final UserTagEvent userTagEvent) {
        kafkaTemplate.send(Constants.USER_TAG_TOPIC, userTagEvent.toUserTagMessage());

        while (true) {
            final ProfileEntity profile = getOrCreateProfile(userTagEvent);
            final ProfileEntity updatedProfile = getUpdatedProfile(profile, userTagEvent);
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
                log.warn("Generation error for cookie={}", userTagEvent.getCookie());
            }
        }
    }

    public UserProfileResponse getUserProfile(final String cookie, final String timeRange, final int limit) {
        final String[] timeRanges = timeRange.split("_");
        final long timeBeginSeconds = parseTimestamp(timeRanges[0]);
        final long timeEndSeconds = parseTimestamp(timeRanges[1]);

        final ProfileEntity profile = getUserProfileByCookie(cookie);
        final List<UserTagEvent> views = filterUserTags(profile.getViews(), cookie, timeBeginSeconds, timeEndSeconds, limit);
        final List<UserTagEvent> buys = filterUserTags(profile.getBuys(), cookie, timeBeginSeconds, timeEndSeconds, limit);

        return UserProfileResponse.builder()
                .cookie(cookie)
                .views(views)
                .buys(buys)
                .build();
    }

    private List<UserTagEvent> filterUserTags(final List<UserTag> views, final String cookie,
                                              final long timeBegin, final long timeEnd, final int limit) {
        return views.stream()
                .filter(userTag -> timeBegin <= userTag.getTimestampMillis() && userTag.getTimestampMillis() < timeEnd)
                .limit(limit)
                .map(userTag -> userTag.toUserTagEvent(cookie))
                .toList();
    }

    private ProfileEntity getUserProfileByCookie(final String cookie) {
        return Optional.ofNullable(aerospikeTemplate.findById(cookie, ProfileEntity.class))
                .orElseThrow(RuntimeException::new);
    }

    private ProfileEntity getOrCreateProfile(final UserTagEvent userTagEvent) {
        return Objects.requireNonNullElse(
                aerospikeTemplate.findById(userTagEvent.getCookie(), ProfileEntity.class),
                new ProfileEntity(userTagEvent.getCookie(), 0, List.of(), List.of())
        );
    }

    private ProfileEntity getUpdatedProfile(final ProfileEntity profile, final UserTagEvent userTagEvent) {
        final List<UserTag> updatedViews = updateTagsIfNeeded(profile.getViews(), userTagEvent, Action.VIEW);
        final List<UserTag> updatedBuys = updateTagsIfNeeded(profile.getBuys(), userTagEvent, Action.BUY);

        return new ProfileEntity(userTagEvent.getCookie(), profile.getGeneration(), updatedViews, updatedBuys);
    }

    private List<UserTag> updateTagsIfNeeded(final List<UserTag> userTags, final UserTagEvent userTagEvent, final Action action) {
        if (userTagEvent.getAction().equals(action)) {
            return addNewTagToProfile(userTags, userTagEvent);
        }
        return userTags;
    }

    private List<UserTag> addNewTagToProfile(final List<UserTag> userTags, final UserTagEvent userTagEvent) {
        return Stream.concat(userTags.stream(), Stream.of(userTagEvent.toUserTag()))
                .sorted(Comparator.comparing(UserTag::getTimestampMillis).reversed())
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
