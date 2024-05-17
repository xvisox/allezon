package pl.mimuw.allezon.dto.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import pl.mimuw.allezon.domain.UserTagEvent;

import java.util.List;

@Value
@Jacksonized
@Builder
public class UserProfileResult {
    String cookie;
    List<UserTagEvent> views;
    List<UserTagEvent> buys;
}