package pl.mimuw.allezon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import pl.mimuw.allezon.dto.request.UserTagEvent;

import java.util.List;

@Schema(description = "Details about the user profile")
@Value
@Jacksonized
@Builder
public class UserProfileResponse {

    @Schema(description = "The user's cookie identifier")
    String cookie;

    @Schema(description = "List of user tag events for views")
    List<UserTagEvent> views;

    @Schema(description = "List of user tag events for buys")
    List<UserTagEvent> buys;
}