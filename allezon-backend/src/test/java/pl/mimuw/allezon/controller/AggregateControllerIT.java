package pl.mimuw.allezon.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import pl.mimuw.allezon.AbstractIT;
import pl.mimuw.allezon.dto.response.AggregatesQueryResponse;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

@Slf4j
public class AggregateControllerIT extends AbstractIT {

    private static ObjectMapper objectMapper;
    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        startWireMockServer();
    }

    @AfterAll
    public static void teardown() {
        stopWireMockServer();
    }

    private static void startWireMockServer() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        System.setProperty(
                "app.client.aggregator.url",
                "http://localhost:" + wireMockServer.port()
        );
        log.info("WireMock server started on port {}", wireMockServer.port());
    }

    private static void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    @SneakyThrows
    void testAggregatesServiceResponse() {
        // given
        final var clientResponse = AggregatesQueryResponse.builder()
                .columns(List.of("1m_bucket", "action", "brand_id", "sum_price", "count"))
                .rows(List.of(
                        List.of("2022-03-01T00:05:00", "BUY", "Nike", "1000", "3"),
                        List.of("2022-03-01T00:06:00", "BUY", "Nike", "1500", "4"),
                        List.of("2022-03-01T00:07:00", "BUY", "Nike", "1200", "2")
                )).build();

        stubFor(get(urlMatching("/aggregates.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(clientResponse))));

        // when
        final var response = callPostAggregates();

        // then
        Assertions.assertEquals(clientResponse, response);
    }
}
