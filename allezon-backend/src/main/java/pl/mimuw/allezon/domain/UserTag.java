package pl.mimuw.allezon.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import pl.mimuw.allezon.dto.request.UserTagEvent;

import java.time.Instant;

@Value
@Jacksonized
@Builder
public class UserTag {
    long timestampMillis;
    String country;
    Device device;
    Action action;
    String origin;
    Product productInfo;

    public UserTagEvent toUserTagEvent(String cookie) {
        return UserTagEvent.builder()
                .time(Instant.ofEpochMilli(timestampMillis).toString())
                .country(country)
                .device(device)
                .action(action)
                .origin(origin)
                .productInfo(productInfo)
                .cookie(cookie)
                .build();
    }
}
