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
import java.util.stream.Stream;

@Slf4j
public class ProfileControllerIT extends AbstractIT {

    @Test
    void testUserProfile() {
        // given
        final String userId = UUID.randomUUID().toString();
        final Instant eventTime = Instant.parse("2022-03-22T12:23:00.000Z");
        final UserTagEvent userTagEvent = createUserTagEvent(userId, eventTime, 2115, Action.BUY);
        final String timeRange = "2022-03-22T12:15:00.000_2022-03-22T12:30:00.000";

        // when
        callPostUserTags(userTagEvent);
        final var userProfilesResponse = callPostUserProfiles(userId,
                Map.of(Constants.TIME_RANGE_PARAM, timeRange)
        );
        log.info("User profiles: {}", userProfilesResponse);

        // then
        Assertions.assertEquals(userId, userProfilesResponse.getCookie());
        Assertions.assertEquals(1, userProfilesResponse.getBuys().size());
        Assertions.assertEquals(0, userProfilesResponse.getViews().size());
        Assertions.assertEquals(eventTime.toString(), userProfilesResponse.getBuys().get(0).getTime());

        final UserTagEvent buyEvent = kafkaTestListener.pollUserTagEvent();
        Assertions.assertNotNull(buyEvent);
        Assertions.assertEquals(userTagEvent, buyEvent);
    }

    @Test
    void testUserProfileMaxSize() {
        // given
        final String userId = UUID.randomUUID().toString();
        final Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        final int maxEvents = Constants.MAX_PROFILE_SIZE + 50;
        final List<UserTagEvent> userTagViewEvents = IntStream.range(0, maxEvents)
                .mapToObj(i -> createUserTagEvent(userId, getTimestamp(now, i), i, Action.VIEW))
                .toList();
        final List<UserTagEvent> userTagBuyEvents = IntStream.range(0, maxEvents)
                .mapToObj(i -> createUserTagEvent(userId, getTimestamp(now, i), i, Action.BUY))
                .toList();
        Stream.concat(userTagBuyEvents.stream(), userTagViewEvents.stream())
                // .parallel()
                .forEach(this::callPostUserTags);

        final String timeBegin = getTimestamp(now, 0)
                .toString().replace("Z", "");
        final String timeEnd = getTimestamp(now, maxEvents)
                .toString().replace("Z", "");
        final var userProfilesResponse = callPostUserProfiles(userId, Map.of(
                Constants.TIME_RANGE_PARAM, timeBegin + "_" + timeEnd,
                Constants.LIMIT_PARAM, 100
        ));

        // then
        final ProfileEntity profile = getProfileFromDatabase(userId);
        Assertions.assertEquals(maxEvents * 2, profile.getGeneration());
        Assertions.assertEquals(Constants.MAX_PROFILE_SIZE, profile.getBuys().size());

        final AtomicInteger productId = new AtomicInteger(maxEvents);
        userProfilesResponse.getBuys().forEach(userTagEvent -> {
            Assertions.assertEquals(productId.decrementAndGet(), userTagEvent.getProductInfo().getProductId());
            Assertions.assertEquals(getTimestamp(now, productId.get()).toString(), userTagEvent.getTime());
        });
    }

    private ProfileEntity getProfileFromDatabase(final String userId) {
        final ProfileEntity profile = aerospikeTemplate.findById(userId, ProfileEntity.class);
        Assertions.assertNotNull(profile);
        log.info("Profile entity: {}", profile);
        return profile;
    }

    private static UserTagEvent createUserTagEvent(final String userId, final Instant timestamp,
                                                   final int productId, final Action action) {
        return UserTagEvent.builder()
                .cookie(userId)
                .time(timestamp.toString())
                .country("Poland")
                .device(Device.MOBILE)
                .action(action)
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
