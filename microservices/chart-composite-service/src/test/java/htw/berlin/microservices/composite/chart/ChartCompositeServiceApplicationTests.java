package htw.berlin.microservices.composite.chart;

import htw.berlin.api.core.chart.Chart;
import htw.berlin.api.core.chart.ChartLabel;
import htw.berlin.api.core.chart.ChartType;
import htw.berlin.api.core.data.Data;
import htw.berlin.api.exceptions.InvalidInputException;
import htw.berlin.api.exceptions.NotFoundException;
import htw.berlin.microservices.composite.chart.services.ChartCompositeIntegration;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
		webEnvironment = RANDOM_PORT,
		classes = {TestSecurityConfig.class},
		properties = {
				"spring.security.oauth2.resourceserver.jwt.issuer-uri=",
				"spring.main.allow-bean-definition-overriding=true",
				"eureka.client.enabled=false",
				"spring.cloud.config.enabled=false"})
class ChartCompositeServiceApplicationTests {

	private static final int CHART_ID_OK = 1;
	private static final int CHART_ID_NOT_FOUND = 2;
	private static final int CHART_ID_INVALID = 3;

	@Autowired
	private WebTestClient client;

	@MockBean
	private ChartCompositeIntegration compositeIntegration;

	@BeforeEach
	void setUp(){
		when(compositeIntegration.getChart(eq(CHART_ID_OK)))
				.thenReturn(Mono.just(new Chart(CHART_ID_OK, "s01", ChartType.LINE, ChartLabel.GRADES, null, null, "mock address")));

		when(compositeIntegration.getData(CHART_ID_OK))
				.thenReturn(Mono.just(new Data(CHART_ID_OK, 1, "s01", null, null, null, "mock address")));

		when(compositeIntegration.getChart(eq(CHART_ID_NOT_FOUND))).thenThrow(new NotFoundException("NOT FOUND: " + CHART_ID_NOT_FOUND));

		when(compositeIntegration.getChart(eq(CHART_ID_INVALID))).thenThrow(new InvalidInputException("INVALID: " + CHART_ID_INVALID));
	}
	@Test
	void contextLoads() {
	}

	@Test
	void getChartById(){
		getAndVerifyChart(CHART_ID_OK, OK)
				.jsonPath("$.chartId").isEqualTo(CHART_ID_OK);


	}
	@Test
	void getChartNotFound(){
		getAndVerifyChart(CHART_ID_NOT_FOUND, NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/chart-composite/" + CHART_ID_NOT_FOUND)
				.jsonPath("$.message").isEqualTo("NOT FOUND: " + CHART_ID_NOT_FOUND);
	}

	@Test
	void getChartInvalidInput() {
		getAndVerifyChart(CHART_ID_INVALID, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/chart-composite/" + CHART_ID_INVALID)
				.jsonPath("$.message").isEqualTo("INVALID: " + CHART_ID_INVALID);
	}

	private WebTestClient.BodyContentSpec getAndVerifyChart(int chartId, HttpStatus expectedStatus) {
		return client.get()
				.uri("/chart-composite/" + chartId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

}
