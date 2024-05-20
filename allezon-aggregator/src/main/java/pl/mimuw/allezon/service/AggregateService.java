package pl.mimuw.allezon.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Aggregate;
import pl.mimuw.allezon.domain.AggregateValue;
import pl.mimuw.allezon.domain.UserTagMessage;
import pl.mimuw.allezon.dto.response.AggregatesQueryResponse;
import pl.mimuw.allezon.jpa.entity.AggregateEntity;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class AggregateService {

    private static final String SHA_256 = "SHA-256";
    private static final byte[] SEPARATOR_BYTES = ",".getBytes();

    private final Semaphore mutex = new Semaphore(1);
    private Map<String, AggregateValue> currentBucket = new HashMap<>();
    private Map<String, AggregateValue> backupBucket = new HashMap<>();

    private final AerospikeTemplate aerospikeTemplate;

    @KafkaListener(topics = Constants.USER_TAG_TOPIC, groupId = Constants.DEFAULT_GROUP_ID)
    private void listen(final UserTagMessage userTagMessage) {
        final String[] origins = {userTagMessage.getOrigin(), null};
        final String[] brandIds = {userTagMessage.getBrandId(), null};
        final String[] categoryIds = {userTagMessage.getCategoryId(), null};
        for (var origin : origins) {
            for (var brandId : brandIds) {
                for (var categoryId : categoryIds) {
                    withMutex(() -> {
                        final String hashKey = getHashKey(userTagMessage.getTimestampSecs() / 60,
                                userTagMessage.getAction(), origin, brandId, categoryId);
                        final AggregateValue aggregateValue = currentBucket.get(hashKey);
                        if (aggregateValue == null) {
                            currentBucket.put(hashKey, new AggregateValue(1, userTagMessage.getPrice()));
                        } else {
                            aggregateValue.add(userTagMessage.getPrice());
                        }
                    });
                }
            }
        }
    }

    @Scheduled(cron = "${app.aggregate.cron:*/30 * * ? * *}")
    private void flushCurrentBucket() {
        withMutex(() -> {
            var temp = backupBucket;
            backupBucket = currentBucket;
            currentBucket = temp;
        });

        for (final Map.Entry<String, AggregateValue> entry : backupBucket.entrySet()) {
            final AggregateValue aggregateValue = entry.getValue();
            final AggregateEntity aggregateEntity = new AggregateEntity(entry.getKey(), Constants.EXPIRATION_SECONDS, 0, 0);
            // if aggregate exists, it will be updated (count and priceSum will be added to the existing values),
            // otherwise new aggregate will be created with count = 0 and priceSum = 0 and then updated
            aerospikeTemplate.add(aggregateEntity,
                    Map.of("count", aggregateValue.getCount(), "priceSum", aggregateValue.getPriceSum())
            );
        }
        backupBucket.clear();
    }

    public AggregatesQueryResponse getAggregates(final String timeRange, final Action action,
                                                 final List<Aggregate> aggregates, final String origin,
                                                 final String brandId, final String categoryId) {
        final String[] timeRanges = timeRange.split("_");
        final long timeBeginMins = parseTimestamp(timeRanges[0]) / 60;
        final long timeEndMins = parseTimestamp(timeRanges[1]) / 60;

        final List<String> hashKeys = new ArrayList<>();
        for (long timestampMins = timeBeginMins; timestampMins < timeEndMins; timestampMins++) {
            final String hashKey = getHashKey(timestampMins, action, origin, brandId, categoryId);
            hashKeys.add(hashKey);
        }

        final List<AggregateEntity> aggregateEntities = aerospikeTemplate.findByIds(hashKeys, AggregateEntity.class);
        final Map<String, AggregateEntity> hashKeyToEntity = aggregateEntities.stream()
                .collect(Collectors.toMap(AggregateEntity::getHashKey, Function.identity()));

        final List<String> columns = createColumns(aggregates, origin, brandId, categoryId);
        final List<List<String>> rows = new ArrayList<>();
        int i = 0;
        for (long timestampMins = timeBeginMins; timestampMins < timeEndMins; timestampMins++) {
            final String hashKey = hashKeys.get(i++);
            final AggregateEntity aggregate = Optional.ofNullable(hashKeyToEntity.get(hashKey))
                    .orElse(new AggregateEntity(hashKey, Constants.EXPIRATION_SECONDS, 0, 0));
            rows.add(createRow(timestampMins, action, origin, brandId, categoryId, aggregate, aggregates));
        }

        return AggregatesQueryResponse.builder()
                .columns(columns)
                .rows(rows)
                .build();
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

    private List<String> createRow(final long timestampMins, final Action action, final String origin, final String brandId,
                                   final String categoryId, final AggregateEntity aggregate, final List<Aggregate> aggregates) {
        final List<String> row = new ArrayList<>();
        row.add(Instant.ofEpochSecond(timestampMins * 60).toString());
        row.add(action.name());
        if (origin != null) row.add(origin);
        if (brandId != null) row.add(brandId);
        if (categoryId != null) row.add(categoryId);
        for (final Aggregate aggregateType : aggregates) {
            switch (aggregateType) {
                case COUNT -> row.add(String.valueOf(aggregate.getCount()));
                case SUM_PRICE -> row.add(String.valueOf(aggregate.getPriceSum()));
            }
        }
        return row;
    }

    @SneakyThrows
    private String getHashKey(final long timestampMins, final Action action,
                              final String origin, final String brandId, final String categoryId) {
        final MessageDigest messageDigest = MessageDigest.getInstance(SHA_256);
        messageDigest.update(String.valueOf(timestampMins).getBytes());
        messageDigest.update(SEPARATOR_BYTES);
        messageDigest.update(action.name().getBytes());
        messageDigest.update(SEPARATOR_BYTES);
        messageDigest.update(Objects.requireNonNullElse(origin, "").getBytes());
        messageDigest.update(SEPARATOR_BYTES);
        messageDigest.update(Objects.requireNonNullElse(brandId, "").getBytes());
        messageDigest.update(SEPARATOR_BYTES);
        messageDigest.update(Objects.requireNonNullElse(categoryId, "").getBytes());
        return new String(messageDigest.digest());
    }

    private long parseTimestamp(final String timestamp) {
        return Instant.parse(timestamp + "Z").getEpochSecond();
    }

    private void withMutex(final Runnable runnable) {
        try {
            mutex.acquire();
            runnable.run();
        } catch (final InterruptedException e) {
            log.error("Mutex interrupted", e);
        } finally {
            mutex.release();
        }
    }
}
