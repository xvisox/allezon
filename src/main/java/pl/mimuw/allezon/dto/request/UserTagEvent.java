package pl.mimuw.allezon.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Device;
import pl.mimuw.allezon.domain.Product;
import pl.mimuw.allezon.domain.UserTag;

import java.time.Instant;

@Value
@Jacksonized
@Builder
public class UserTagEvent {
    Instant time;
    String country;
    Device device;
    Action action;
    String origin;
    @JsonProperty("product_info")
    Product productInfo;
    String cookie;

    public UserTag toUserTag() {
        return UserTag.builder()
                .time(time)
                .country(country)
                .device(device)
                .action(action)
                .origin(origin)
                .productInfo(productInfo)
                .build();
    }
}
