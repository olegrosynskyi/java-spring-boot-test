package io.skai.template.cucumber.steps;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.client.WireMockBuilder;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class HttpSteps {

    private static final int WIREMOCK_PORT = 8089;
    @LocalServerPort
    private int applicationPort;
    @Autowired
    private RestTemplate restTemplate;

    protected final static WireMock wireMock = new WireMockBuilder()
            .scheme("http")
            .port(WIREMOCK_PORT)
            .build();

    @When("^I go to service with path (/.*) response should be (\\d+) with wiremock stub$")
    public void wireMockStubTest(String path, int expectedResponseCode) {

        wireMock.register(get(urlEqualTo(path)).willReturn(aResponse()
                .withStatus(expectedResponseCode)
                .withHeader("Content-Type", "text/plain")
                .withBody("Hello world!"))
        );
        httpResponseCode(path, expectedResponseCode, WIREMOCK_PORT);
    }

    @When("^I go to service path (/.*) response should be (\\d+)$")
    public void httpServiceResponseCode(String path, int expectedResponseCode) {
        httpResponseCode(path, expectedResponseCode, applicationPort);
    }

    private void httpResponseCode(String path, int expectedResponseCode, int port) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + path, String.class);
            int statusCode = response.getStatusCode().value();
            assertThat("Request was " + path, statusCode, is(expectedResponseCode));
        } catch (HttpClientErrorException e) {
            assertThat("Request was " + path, e.getStatusCode().value(), is(expectedResponseCode));
        }
    }
}
