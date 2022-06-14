package htw.berlin.microservices.core.data;

import static htw.berlin.api.event.Event.Type.CREATE;
import static htw.berlin.api.event.Event.Type.DELETE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import htw.berlin.api.core.data.Data;
import htw.berlin.api.core.data.ModuleGrade;
import htw.berlin.api.core.data.Transcript;
import htw.berlin.api.event.Event;

import htw.berlin.api.exceptions.InvalidInputException;
import htw.berlin.microservices.core.data.persistence.DataEntity;
import htw.berlin.microservices.core.data.persistence.IDataRepository;
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
class DataServiceApplicationTests extends MongoDbTestBase{

	@Autowired
	private WebTestClient client;

	@Autowired
	private IDataRepository repository;

	@Autowired
	@Qualifier("messageProcessor")
	private Consumer<Event<Integer, Data>> messageProcessor;

	@BeforeEach
	void setupDb() {
		repository.deleteAll().block();
	}

	// TODO: 6/13/2022 Test Logic
	@Test
	void getDataByChartId() {
		int chartId = 1;
		int dataId = 1;

		assertNull(repository.findByChartId(chartId).block());
		assertEquals(0, (long) repository.count().block());

		sendCreateDataEvent(chartId, dataId);

		DataEntity example = repository.findByChartId(chartId).block();

		assertNotNull(repository.findByChartId(chartId).block());
		assertEquals(1, (long) repository.count().block());

		getAndVerifyDataByChartId(chartId, OK)
				.jsonPath("$.chartId").isEqualTo(chartId);

	}

	@Test
	void duplicateError() {

		int chartId = 1;
		int dataId = 2;

		assertNull(repository.findByChartId(chartId).block());

		sendCreateDataEvent(chartId, dataId);

		assertNotNull(repository.findByChartId(chartId).block());

		InvalidInputException thrown = assertThrows(
				InvalidInputException.class,
				() -> sendCreateDataEvent(chartId, dataId),
				"Expected a InvalidInputException here!");
		assertEquals("Duplicate key, Chart Id: 1, Data Id: 2", thrown.getMessage());
	}

	@Test
	void deleteData() {

		int chartId = 1;
		int dataId = 1;

		sendCreateDataEvent(chartId, dataId);
		assertNotNull(repository.findByChartId(chartId).block());

		sendDeleteDataEvent(chartId);
		assertNull(repository.findByChartId(chartId).block());

		sendDeleteDataEvent(chartId);
	}

	@Test
	void getDataInvalidParameterString() {

		 getAndVerifyDataByChartId("?chartId=no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/data")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	void getDataNotFound() {

		int chartIdNotFound = 113;
		getAndVerifyDataByChartId("?chartId=113", OK);
	}

	@Test
	void getDataInvalidParameterNegativeValue() {

		int chartIdInvalid = -1;

		getAndVerifyDataByChartId("?chartId=" + chartIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/data")
				.jsonPath("$.message").isEqualTo("Invalid chartId: " + chartIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyDataByChartId(int chartId, HttpStatus expectedStatus) {
		return getAndVerifyDataByChartId("?chartId=" + chartId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyDataByChartId(String chartIdPath, HttpStatus expectedStatus) {
		return client.get()
				.uri("/data" + chartIdPath)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private void sendCreateDataEvent(int chartId, int dataId) {
		List<ModuleGrade> moduleGrades = new ArrayList<>();
		moduleGrades.add( new ModuleGrade(10.0, "Math1", "math", 5));
		moduleGrades.add(new ModuleGrade(10.0, "Math2", "math", 5));

		List<ModuleGrade> moduleGrades2 = new ArrayList<>();
		moduleGrades2.add( new ModuleGrade(10.0, "Mode1", "mode", 5));
		
		List<Transcript> transcripts = new ArrayList<>();
		transcripts.add(new Transcript("ss22", moduleGrades, 0, 0));
		transcripts.add(new Transcript("ws21/22", moduleGrades2, 0, 0));
		Data data = new Data(chartId, dataId, "s0", transcripts, null, null, "sa");

		Event<Integer, Data> event = new Event(CREATE, chartId, data);
		messageProcessor.accept(event);
	}

	private void sendDeleteDataEvent(int chartId) {
		Event<Integer, Data> event = new Event(DELETE, chartId, null);
		messageProcessor.accept(event);
	}

}
