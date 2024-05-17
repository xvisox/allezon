package pl.mimuw.allezon.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Getter
@FieldDefaults(level= AccessLevel.PRIVATE)
@EqualsAndHashCode
@Jacksonized
@Builder
public class UserTag {
    Instant time;
    String country;
    Device device;
    Action action;
    String origin;
    @JsonProperty("product_info")
    Product productInfo;
}
