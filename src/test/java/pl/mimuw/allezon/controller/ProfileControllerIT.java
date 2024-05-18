package pl.mimuw.allezon.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.mimuw.allezon.AbstractIT;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Device;
import pl.mimuw.allezon.domain.Product;
import pl.mimuw.allezon.dto.request.UserTagEvent;
import pl.mimuw.allezon.jpa.entity.ProfileEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
public class ProfileControllerIT extends AbstractIT {


    @Test
    void testUserProfile() {
        // given
        final String userId = UUID.randomUUID().toString();
        final UserTagEvent userTagEvent = createUserTagEvent(userId, Instant.parse("2022-03-22T12:23:00.000Z"), 2115);

        // when
        callPostUserTags(userTagEvent);
        final var userProfilesResponse = callPostUserProfiles(userId,
                Map.of("time_range", "2022-03-22T12:15:00.000_2022-03-22T12:30:00.000")
        );
        log.info("User profiles: {}", userProfilesResponse);

        // then
        Assertions.assertEquals(userId, userProfilesResponse.getCookie());
        Assertions.assertEquals(1, userProfilesResponse.getBuys().size());
        Assertions.assertEquals(0, userProfilesResponse.getViews().size());
    }

    @Test
    void testUserProfileMaxSize() {
        // given
        final String userId = UUID.randomUUID().toString();
        final Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        final List<UserTagEvent> userTagEvents = IntStream.range(0, Constants.MAX_PROFILE_SIZE + 50)
                .mapToObj(i -> createUserTagEvent(userId, getTimestamp(now, i), i))
                .toList();
        userTagEvents.forEach(this::callPostUserTags);

        final String timeBegin = getTimestamp(now, 0)
                .toString().replace("Z", "");
        final String timeEnd = getTimestamp(now, Constants.MAX_PROFILE_SIZE + 50)
                .toString().replace("Z", "");
        final var userProfilesResponse = callPostUserProfiles(userId, Map.of(
                "time_range", timeBegin + "_" + timeEnd,
                "limit", 100
        ));

        // then
        final ProfileEntity profile = getProfileFromDatabase(userId);
        Assertions.assertEquals(Constants.MAX_PROFILE_SIZE, profile.getBuys().size());

        final AtomicInteger productId = new AtomicInteger(250);
        userProfilesResponse.getBuys().forEach(userTagEvent ->
                Assertions.assertEquals(productId.decrementAndGet(), userTagEvent.getProductInfo().getProductId())
        );
    }

    private ProfileEntity getProfileFromDatabase(final String userId) {
        final ProfileEntity profile = aerospikeTemplate.findById(userId, ProfileEntity.class);
        Assertions.assertNotNull(profile);
        log.info("Profile entity: {}", profile);
        return profile;
    }

    private static UserTagEvent createUserTagEvent(final String userId, final Instant timestamp, final int productId) {
        return UserTagEvent.builder()
                .cookie(userId)
                .time(timestamp.toString())
                .country("Poland")
                .device(Device.MOBILE)
                .action(Action.BUY)
                .origin("NIKE_WOMEN_SHOES_CAMPAIGN")
                .productInfo(Product.builder()
                        .productId(productId)
                        .brandId("NIKE")
                        .categoryId("WOMAN_SHOES")
                        .price(2115)
                        .build())
                .build();
    }

    private static Instant getTimestamp(final Instant now, final int plusSeconds) {
        return now.plus(plusSeconds, ChronoUnit.SECONDS).truncatedTo(ChronoUnit.MILLIS);
    }
}
