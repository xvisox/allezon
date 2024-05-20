package pl.mimuw.allezon.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class UserTagMessage {
    long timestampSecs;
    Action action;
    String origin;
    String brandId;
    String categoryId;
    int price;
}
