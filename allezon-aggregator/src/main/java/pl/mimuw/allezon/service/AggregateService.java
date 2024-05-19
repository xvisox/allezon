package pl.mimuw.allezon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Aggregate;
import pl.mimuw.allezon.dto.request.UserTagEvent;
import pl.mimuw.allezon.dto.response.AggregatesQueryResponse;

import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class AggregateService {

    private final AerospikeTemplate aerospikeTemplate;

    private static final byte[] SEPARATOR_BYTES = ",".getBytes();
    private static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (final Exception e) {
            log.error("Error while creating message digest", e);
        }
    }

    @KafkaListener(topics = Constants.USER_TAG_TOPIC, groupId = "allezon")
    private void listen(final UserTagEvent userTagEvent) {
        // todo: change event to more lightweight object
    }

    public AggregatesQueryResponse getAggregates(final String timeRangeStr, final Action action,
                                                 final List<Aggregate> aggregates, final String origin,
                                                 final String brandId, final String categoryId) {
        return null;
    }

    private String getHash(final long timestampSecs, final Action action,
                           final String origin, final String brandId, final String categoryId) {
        messageDigest.update(String.valueOf(timestampSecs).getBytes());
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
}
