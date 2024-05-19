package pl.mimuw.allezon;

import io.restassured.RestAssured;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pl.mimuw.allezon.domain.Action;
import pl.mimuw.allezon.domain.Aggregate;
import pl.mimuw.allezon.dto.request.UserTagEvent;
import pl.mimuw.allezon.dto.response.AggregatesQueryResponse;
import pl.mimuw.allezon.dto.response.UserProfileResponse;
import pl.mimuw.allezon.kafka.KafkaTestListener;

import java.util.Map;

import static io.restassured.RestAssured.given;

@Testcontainers
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

    @SneakyThrows
    @BeforeEach
    protected void setUp() {
        // wait for Kafka to start
        Thread.sleep(2000);
        RestAssured.port = port;
    }

    @Container
    protected static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:6.2.1")
    );

    @DynamicPropertySource
    protected static void kafkaProperties(final DynamicPropertyRegistry registry) {
        registry.add("app.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    protected KafkaTestListener kafkaTestListener;

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

    protected AggregatesQueryResponse callPostAggregates() {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam(Constants.TIME_RANGE_PARAM, "2022-03-22T12:25:00_2022-03-22T12:28:00")
                .queryParam(Constants.ACTION_PARAM, Action.BUY.name())
                .queryParam(Constants.AGGREGATES_PARAM, Aggregate.COUNT.name())
                .queryParam(Constants.ORIGIN_PARAM, "NIKE_WOMEN_SHOES_CAMPAIGN")
                .queryParam(Constants.BRAND_ID_PARAM, "NIKE")
                .queryParam(Constants.CATEGORY_ID_PARAM, "WOMEN_SHOES")
                .when()
                .post("/aggregates")
                .then()
                .statusCode(200)
                .extract()
                .as(AggregatesQueryResponse.class);
    }
}
