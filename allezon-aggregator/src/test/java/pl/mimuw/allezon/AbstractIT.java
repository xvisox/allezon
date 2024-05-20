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
import org.springframework.kafka.core.KafkaTemplate;
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
import pl.mimuw.allezon.domain.UserTagMessage;
import pl.mimuw.allezon.dto.response.AggregatesQueryResponse;

import java.util.List;

import static io.restassured.RestAssured.given;

@Testcontainers
@DirtiesContext
@AutoConfigureObservability
@ActiveProfiles("it")
@SpringBootTest(classes = AggregatorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractIT {

    @Autowired
    protected AerospikeTemplate aerospikeTemplate;

    @Autowired
    protected KafkaTemplate<String, UserTagMessage> kafkaTemplate;

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

    protected AggregatesQueryResponse callGetAggregates(
            final String timeRange, final Action action, final List<Aggregate> aggregates,
            final String origin, final String brandId, final String categoryId
    ) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam(Constants.TIME_RANGE_PARAM, timeRange)
                .queryParam(Constants.ACTION_PARAM, action)
                .queryParam(Constants.AGGREGATES_PARAM, aggregates)
                .queryParam(Constants.ORIGIN_PARAM, origin)
                .queryParam(Constants.BRAND_ID_PARAM, brandId)
                .queryParam(Constants.CATEGORY_ID_PARAM, categoryId)
                .when()
                .get("/aggregates")
                .then()
                .statusCode(200)
                .extract()
                .as(AggregatesQueryResponse.class);
    }
}
