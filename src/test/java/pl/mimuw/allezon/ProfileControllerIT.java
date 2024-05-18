package pl.mimuw.allezon;

import org.junit.jupiter.api.Test;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Device;
import pl.mimuw.allezon.dto.request.UserTagEvent;

import static io.restassured.RestAssured.given;

public class ProfileControllerIT extends AbstractIT {

    @Test
    public void testUserTagsEndpoint() {
        UserTagEvent userTagEvent = UserTagEvent.builder()
                .time("2022-03-22T12:15:00.000Z")
                .country("Poland")
                .device(Device.MOBILE)
                .action(Action.BUY)
                .origin("origin")
                .productInfo(null)
                .cookie("cookie")
                .build();


        given()
                .contentType("application/json")
                .body(userTagEvent)
                .when()
                .post("/user_tags")
                .then()
                .statusCode(204);
    }
}
