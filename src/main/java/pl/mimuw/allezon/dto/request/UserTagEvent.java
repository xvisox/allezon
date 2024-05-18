package pl.mimuw.allezon.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Device;
import pl.mimuw.allezon.domain.Product;
import pl.mimuw.allezon.domain.UserTag;

import java.time.Instant;

@Schema(description = "Details about the user tag event")
@Value
@Jacksonized
@Builder
public class UserTagEvent {

    @Schema(description = "The timestamp of the event", example = "2022-03-22T12:15:00.000Z")
    String time;

    @Schema(description = "The country where the action occurred", example = "Poland")
    String country;

    @Schema(description = "The device used by the user")
    Device device;

    @Schema(description = "The action performed by the user")
    Action action;

    @Schema(description = "The origin of the event", example = "NIKE_WOMEN_SHOES_CAMPAIGN")
    String origin;

    @Schema(description = "Information about the product involved in the event")
    @JsonProperty("product_info")
    Product productInfo;

    @Schema(description = "The user's cookie identifier", example = "cookie")
    String cookie;

    public UserTag toUserTag() {
        return UserTag.builder()
                .timestamp(Instant.parse(time).toEpochMilli())
                .country(country)
                .device(device)
                .action(action)
                .origin(origin)
                .productInfo(productInfo)
                .build();
    }
}
