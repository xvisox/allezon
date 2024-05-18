package pl.mimuw.allezon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.mimuw.allezon.Constants;
import pl.mimuw.allezon.dto.request.UserTagEvent;
import pl.mimuw.allezon.dto.response.UserProfileResult;
import pl.mimuw.allezon.service.ProfileService;

@Slf4j
@RestController
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "Add user tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.HTTP_NO_CONTENT, description = "User tag added"),
            @ApiResponse(responseCode = Constants.HTTP_INTERNAL_SERVER_ERROR, description = "Internal server error")
    })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PostMapping(value = "/user_tags", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addUserTag(
            @RequestBody(required = false) UserTagEvent userTag
    ) {
        profileService.addUserTag(userTag);
    }

    @Operation(summary = "Get user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.HTTP_OK, description = "User profile returned",
                    content = @Content(schema = @Schema(implementation = UserProfileResult.class))),
            @ApiResponse(responseCode = Constants.HTTP_INTERNAL_SERVER_ERROR, description = "Internal server error",
                    content = @Content())
    })
    @PostMapping(value = "/user_profiles/{cookie}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileResult getUserProfile(
            @PathVariable(Constants.COOKIE_PARAM) String cookie,
            @RequestParam(Constants.TIME_RANGE_PARAM) String timeRangeStr,
            @RequestParam(defaultValue = Constants.MAX_PROFILE_SIZE_STR) int limit,
            @RequestBody(required = false) UserProfileResult expectedResult
    ) {
        log.debug("Expected result: {}", expectedResult);
        return profileService.getUserProfile(cookie, timeRangeStr, limit);
    }
}
