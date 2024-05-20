package pl.mimuw.allezon.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pl.mimuw.allezon.AbstractIT;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Aggregate;
import pl.mimuw.allezon.domain.UserTagMessage;
import pl.mimuw.allezon.dto.response.AggregatesQueryResponse;

import java.time.Instant;
import java.util.List;

@Slf4j
public class AggregateControllerIT extends AbstractIT {

    @Test
    @SneakyThrows
    void testAggregatesEndpoint() {
        // given
        final Instant timestamp = Instant.parse("2022-03-22T12:25:00.000Z");
        final UserTagMessage userTagMessage = UserTagMessage.builder()
                .timestampSecs(timestamp.getEpochSecond())
                .action(Action.VIEW)
                .origin("NIKE_WOMEN_SHOES_CAMPAIGN")
                .brandId("NIKE")
                .categoryId("WOMAN_SHOES")
                .price(100)
                .build();
        kafkaTemplate.send(Constants.USER_TAG_TOPIC, userTagMessage);
        Thread.sleep(6000);

        // when
        final AggregatesQueryResponse response = callGetAggregates(
                "2022-03-22T12:25:00_2022-03-22T12:28:00",
                Action.VIEW,
                List.of(Aggregate.SUM_PRICE, Aggregate.COUNT),
                "NIKE_WOMEN_SHOES_CAMPAIGN",
                "NIKE",
                "WOMAN_SHOES"
        );
        log.info("Response: {}", response);
    }
}
