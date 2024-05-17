package pl.mimuw.allezon.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@Jacksonized
@Builder
public class UserTag {
    Instant time;
    String country;
    Device device;
    Action action;
    String origin;
    Product productInfo;
}
