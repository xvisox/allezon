package pl.mimuw.allezon.service;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.ResultCode;
import com.aerospike.client.policy.CommitLevel;
import com.aerospike.client.policy.GenerationPolicy;
import com.aerospike.client.policy.WritePolicy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.stereotype.Service;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.UserTag;
import pl.mimuw.allezon.dto.request.UserTagEvent;
import pl.mimuw.allezon.jpa.entity.ProfileEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class ProfileService {

    private static final long MAX_PROFILE_SIZE = 200L;

    private final AerospikeTemplate aerospikeTemplate;

    public void addUserTag(final UserTagEvent userTag) {
        while (true) {
            final ProfileEntity profile = getOrCreateProfile(userTag);
            final ProfileEntity updatedProfile = getUpdatedProfile(profile, userTag);
            final WritePolicy writePolicy = createWritePolicy(profile);

            try {
                aerospikeTemplate.persist(updatedProfile, writePolicy);
                return;
            } catch (final AerospikeException up) {
                if (up.getResultCode() != ResultCode.GENERATION_ERROR) {
                    throw up;
                }
                log.warn("Generation error for cookie={}", userTag.getCookie());
            }
        }
    }

    private ProfileEntity getOrCreateProfile(final UserTagEvent userTag) {
        return Objects.requireNonNullElse(
                aerospikeTemplate.findById(userTag.getCookie(), ProfileEntity.class),
                new ProfileEntity(userTag.getCookie(), 0, List.of(), List.of())
        );
    }

    private ProfileEntity getUpdatedProfile(final ProfileEntity profile, final UserTagEvent userTag) {
        final List<UserTag> updatedViews = updateTagsIfNeeded(profile.views(), userTag, Action.VIEW);
        final List<UserTag> updatedBuys = updateTagsIfNeeded(profile.buys(), userTag, Action.BUY);

        return new ProfileEntity(userTag.getCookie(), profile.generation(), updatedViews, updatedBuys);
    }

    private List<UserTag> updateTagsIfNeeded(final List<UserTag> userTags, final UserTagEvent userTag, final Action action) {
        if (userTag.getAction().equals(action)) {
            return addNewTagToProfile(userTags, userTag);
        }
        return userTags;
    }

    private List<UserTag> addNewTagToProfile(final List<UserTag> userTags, final UserTagEvent userTagEvent) {
        return Stream.concat(userTags.stream(), Stream.of(userTagEvent.toUserTag()))
                .sorted(Comparator.comparing(UserTag::getTime).reversed())
                .limit(MAX_PROFILE_SIZE)
                .toList();
    }

    private WritePolicy createWritePolicy(final ProfileEntity profile) {
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.socketTimeout = 15000;
        writePolicy.totalTimeout = 35000;
        writePolicy.commitLevel = CommitLevel.COMMIT_MASTER;
        writePolicy.generation = profile.generation();
        writePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
        return writePolicy;
    }
}
