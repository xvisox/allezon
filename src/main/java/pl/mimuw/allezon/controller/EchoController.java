package pl.mimuw.allezon.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.mimuw.allezon.dto.request.UserTagEvent;
import pl.mimuw.allezon.service.ProfileService;

@RestController
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class EchoController {

    private final ProfileService profileService;

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PostMapping(value = "/user_tags", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addUserTag(
            @RequestBody(required = false) UserTagEvent userTag
    ) {
        profileService.addUserTag(userTag);
    }

    /*
    @PostMapping("/user_profiles/{cookie}")
    public ResponseEntity<UserProfileResult> getUserProfile(@PathVariable("cookie") String cookie,
            @RequestParam("time_range") String timeRangeStr,
            @RequestParam(defaultValue = "200") int limit,
            @RequestBody(required = false) UserProfileResult expectedResult) {

        return ResponseEntity.ok(expectedResult);
    }

    @PostMapping("/aggregates")
    public ResponseEntity<AggregatesQueryResult> getAggregates(@RequestParam("time_range") String timeRangeStr,
            @RequestParam("action") Action action,
            @RequestParam("aggregates") List<Aggregate> aggregates,
            @RequestParam(value = "origin", required = false) String origin,
            @RequestParam(value = "brand_id", required = false) String brandId,
            @RequestParam(value = "category_id", required = false) String categoryId,
            @RequestBody(required = false) AggregatesQueryResult expectedResult) {

        return ResponseEntity.ok(expectedResult);
    }
     */
}
