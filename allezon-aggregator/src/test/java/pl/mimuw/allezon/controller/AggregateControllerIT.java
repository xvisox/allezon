package pl.mimuw.allezon.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.mimuw.allezon.AbstractIT;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Aggregate;
import pl.mimuw.allezon.domain.UserTagMessage;
import pl.mimuw.allezon.dto.response.AggregatesQueryResponse;
import pl.mimuw.allezon.jpa.entity.AggregateEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class AggregateControllerIT extends AbstractIT {

    @BeforeEach
    void clearAggregates() {
        aerospikeTemplate.deleteAll(AggregateEntity.class);
    }

    @Test
    void testColumns() {
        // when
        String[] origins = {"NIKE_WOMEN_SHOES_CAMPAIGN", null};
        String[] brandIds = {"NIKE", null};
        String[] categoryIds = {"WOMAN_SHOES", null};
        String timeRange = "2022-03-22T12:25:00_2022-03-22T12:28:00";
        Action action = Action.VIEW;
        List<Aggregate> aggregates = List.of(Aggregate.SUM_PRICE, Aggregate.COUNT);

        for (var origin : origins) {
            for (var brandId : brandIds) {
                for (var categoryId : categoryIds) {
                    final AggregatesQueryResponse response = callGetAggregates(
                            timeRange, action, aggregates, origin, brandId, categoryId
                    );
                    List<String> expectedColumns = createColumns(aggregates, origin, brandId, categoryId);
                    Assertions.assertArrayEquals(expectedColumns.toArray(), response.getColumns().toArray());
                }
            }
        }
    }

    @Test
    void testBucketTimeFormat() {
        String timeRange = "2022-03-22T12:25:00_2022-03-22T12:26:00";
        Action action = Action.BUY;
        List<Aggregate> aggregates = List.of(Aggregate.COUNT);

        final AggregatesQueryResponse response = callGetAggregates(
                timeRange, action, aggregates, null, null, null
        );
        Assertions.assertNotNull(response.getRows());
        Assertions.assertEquals(1, response.getRows().size());
        Assertions.assertEquals(3, response.getRows().get(0).size());
        Assertions.assertEquals(response.getRows().get(0).get(0), "2022-03-22T12:25:00");
    }

    @Test
    void testNumberOfBuckets() {
        Map<String, Integer> timeRangesToBucketsNumber = Map.of(
                "2022-03-22T12:25:00_2022-03-22T12:26:00", 1,
                "2022-03-22T12:25:00_2022-03-22T12:27:00", 2,
                "2022-03-22T12:25:00_2022-03-22T12:28:00", 3
        );
        Action action = Action.BUY;
        List<Aggregate> aggregates = List.of(Aggregate.COUNT);
        for (var entry : timeRangesToBucketsNumber.entrySet()) {
            final AggregatesQueryResponse response = callGetAggregates(
                    entry.getKey(), action, aggregates, null, null, null
            );
            Assertions.assertNotNull(response.getRows());
            Assertions.assertEquals(entry.getValue(), response.getRows().size());
        }
    }

    @Test
    @SneakyThrows
    void testAdditionOfAggregatesFromDifferentBuckets() {
        final Instant timestamp = Instant.parse("2022-03-22T12:25:00.000Z");
        Action action = Action.VIEW;
        String origin = "NIKE_WOMEN_SHOES_CAMPAIGN";
        String brandId = "NIKE";
        String categoryId = "WOMAN_SHOES";

        for (int i = 0; i < 2; i++) {
            final UserTagMessage userTagMessage = UserTagMessage.builder()
                    .timestampSecs(timestamp.getEpochSecond())
                    .action(action)
                    .origin(origin)
                    .brandId(brandId)
                    .categoryId(categoryId)
                    .price(100)
                    .build();
            kafkaTemplate.send(Constants.USER_TAG_TOPIC, userTagMessage);
            Thread.sleep(6000);
        }

        String timeRange = "2022-03-22T12:25:00_2022-03-22T12:26:00";
        List<Aggregate> aggregates = List.of(Aggregate.SUM_PRICE, Aggregate.COUNT);
        final AggregatesQueryResponse response = callGetAggregates(
                timeRange, action, aggregates, origin, brandId, categoryId
        );
        Assertions.assertNotNull(response.getRows());
        Assertions.assertEquals(1, response.getRows().size());
        Assertions.assertEquals(7, response.getRows().get(0).size());
        Assertions.assertEquals("200", response.getRows().get(0).get(5));
        Assertions.assertEquals("2", response.getRows().get(0).get(6));
    }

    private List<String> createColumns(final List<Aggregate> aggregates, final String origin,
                                       final String brandId, final String categoryId) {
        final List<String> columns = new ArrayList<>(Arrays.asList("1m_bucket", "action"));
        if (origin != null) columns.add("origin");
        if (brandId != null) columns.add("brand_id");
        if (categoryId != null) columns.add("category_id");
        for (final Aggregate aggregate : aggregates) {
            columns.add(aggregate.name().toLowerCase());
        }
        return columns;
    }
}
