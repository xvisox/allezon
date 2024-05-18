package pl.mimuw.allezon.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class UserTag {
    String time;
    String country;
    Device device;
    Action action;
    String origin;
    Product productInfo;
}
