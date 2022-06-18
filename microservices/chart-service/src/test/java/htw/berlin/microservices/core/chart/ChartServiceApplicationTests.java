package htw.berlin.microservices.core.chart;

import static htw.berlin.api.core.chart.ChartLabel.GRADES;
import static htw.berlin.api.core.chart.ChartType.BAR;
import static htw.berlin.api.event.Event.Type.CREATE;
import static htw.berlin.api.event.Event.Type.DELETE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.function.Consumer;

import htw.berlin.api.core.chart.Chart;
import htw.berlin.api.event.Event;
import htw.berlin.api.exceptions.InvalidInputException;
import htw.berlin.microservices.core.chart.persistence.ChartEntity;
import htw.berlin.microservices.core.chart.persistence.IChartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
		"eureka.client.enabled=false",
		"spring.cloud.config.enabled=false"})
class ChartServiceApplicationTests extends MongoDbTestBase{

	@Autowired
	private WebTestClient client;

	@Autowired
	private IChartRepository repository;

	@Autowired
	@Qualifier("messageProcessor")
	private Consumer<Event<Integer, Chart>> messageProcessor;

	@BeforeEach
	void setupDb() {
		repository.deleteAll().block();
	}
	
	@Test
	void getChartById(){
		int chartId = 1;
		
		assertNull(repository.findByChartId(chartId).block());
		assertEquals(0, (long)repository.count().block());
		
		sendCreateChartEvent(chartId);

		ChartEntity entity = repository.findByChartId(chartId).block();
		assertNotNull(repository.findByChartId(chartId).block());
		assertEquals(1, (long)repository.count().block());

		getAndVerifyChart(chartId, OK)
				.jsonPath("$.chartId").isEqualTo(chartId);
	}

	@Test
	void duplicateError() {

		int chartId = 1;

		assertNull(repository.findByChartId(chartId).block());

		sendCreateChartEvent(chartId);

		assertNotNull(repository.findByChartId(chartId).block());

		InvalidInputException thrown = assertThrows(
				InvalidInputException.class,
				() -> sendCreateChartEvent(chartId),
				"Expected a InvalidInputException here!");
		assertEquals("Duplicate key, Chart Id: " + chartId, thrown.getMessage());
	}

	@Test
	void deleteChart() {

		int chartId = 1;

		sendCreateChartEvent(chartId);
		assertNotNull(repository.findByChartId(chartId).block());

		sendDeleteChartEvent(chartId);
		assertNull(repository.findByChartId(chartId).block());

		sendDeleteChartEvent(chartId);
	}

	@Test
	void getChartInvalidParameterString() {

		getAndVerifyChart("?chartId=no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/chart")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	void getChartNotFound() {

		int chartIdNotFound = 13;
		getAndVerifyChart(chartIdNotFound, NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/chart")
				.jsonPath("$.message").isEqualTo("No chart found for chartId: " + chartIdNotFound);
	}

	@Test
	void getChartInvalidParameterNegativeValue() {

		int chartIdInvalid = -1;

		getAndVerifyChart(chartIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/chart")
				.jsonPath("$.message").isEqualTo("Invalid chartId: " + chartIdInvalid);
	}


	private WebTestClient.BodyContentSpec getAndVerifyChart(int chartId, HttpStatus expectedStatus) {
		return getAndVerifyChart("?chartId=" + chartId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyChart(String chartIdPath, HttpStatus expectedStatus) {
		return client.get()
				.uri("/chart" + chartIdPath)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private void sendCreateChartEvent(int chartId) {
		Chart chart = new Chart(1, "s01", BAR, GRADES, null, null, "sa");
		Event<Integer, Chart> event = new Event(CREATE, chartId, chart);
		messageProcessor.accept(event);
	}

	private void sendDeleteChartEvent(int chartId){
		Event<Integer, Chart> event = new Event(DELETE, chartId, null);
		messageProcessor.accept(event);
	}


}
