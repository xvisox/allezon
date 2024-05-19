package pl.mimuw.allezon;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import pl.mimuw.allezon.dto.request.UserTagEvent;
import pl.mimuw.allezon.dto.response.UserProfileResponse;

import java.util.Map;

import static io.restassured.RestAssured.given;

@DirtiesContext
@AutoConfigureObservability
@ActiveProfiles("it")
@SpringBootTest(classes = {AllezonApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractIT {

    private static final String USER_TAGS_PATH = "/user_tags";
    private static final String USER_PROFILES_PATH = "/user_profiles/{cookie}";

    @Autowired
    protected AerospikeTemplate aerospikeTemplate;

    @LocalServerPort
    protected Integer port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    protected void callPostUserTags(final UserTagEvent userTagEvent) {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(userTagEvent)
                .when()
                .post(USER_TAGS_PATH)
                .then()
                .statusCode(204);
    }

    protected UserProfileResponse callPostUserProfiles(final String userId, final Map<String, Object> queryParams) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam(Constants.COOKIE_PARAM, userId)
                .queryParams(queryParams)
                .when()
                .post(USER_PROFILES_PATH)
                .then()
                .statusCode(200)
                .extract()
                .as(UserProfileResponse.class);
    }
}
