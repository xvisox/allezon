package pl.mimuw.allezon.domain;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class UserTagEvent {
    Instant time;
    String cookie;

    String country;
    Device device;
    Action action;
    String origin;
    @JsonProperty("product_info")
    Product productInfo;
}
