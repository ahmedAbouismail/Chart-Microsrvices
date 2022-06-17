package htw.berlin.microservices.composite.chart;

import static htw.berlin.api.core.chart.ChartLabel.GRADES;
import static htw.berlin.api.core.chart.ChartType.*;
import static htw.berlin.api.event.Event.Type.CREATE;
import static htw.berlin.api.event.Event.Type.DELETE;
import static htw.berlin.microservices.composite.chart.IsSameEvent.sameEventExceptCreatedAt;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static reactor.core.publisher.Mono.just;

import java.util.ArrayList;
import java.util.List;

import htw.berlin.api.composite.chart.ChartAggregate;
import htw.berlin.api.composite.chart.DataSummery;
import htw.berlin.api.core.chart.Chart;
import htw.berlin.api.core.chart.ChartLabel;
import htw.berlin.api.core.chart.ChartType;
import htw.berlin.api.core.data.Data;
import htw.berlin.api.event.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {TestSecurityConfig.class},
        properties = {
                "spring.security.oauth2.resourceserver.jwt.issuer-uri=",
                "spring.main.allow-bean-definition-overriding=true",
                "eureka.client.enabled=false",
                "spring.cloud.stream.defaultBinder=rabbit",
                "spring.cloud.config.enabled=false"})
@Import({TestChannelBinderConfiguration.class})
public class MessagingTests {

    private static final Logger LOG = LoggerFactory.getLogger(MessagingTests.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private OutputDestination target;

    @BeforeEach
    void setUp(){
        purgeMessages("charts");
        purgeMessages("data");
    }

    @Test
    void createCompositeChart1(){

        ChartAggregate composite = new ChartAggregate(1, "s01", GRADES, LINE, null, null, null, null);
        postAndVerifyChart(composite, ACCEPTED);

        final List<String> chartMessages = getMessages("charts");
        final List<String> dataMessages = getMessages("data");

        assertEquals(1, chartMessages.size());

        Event<Integer, Chart> expectedEvent = new Event(CREATE, composite.getChartId(),
                new Chart(composite.getChartId(), composite.getStudentId(), composite.getChartType(), composite.getChartLabel(), composite.getCreatedAt(), composite.getLastUpdate(), null));
        assertThat(chartMessages.get(0), is(sameEventExceptCreatedAt(expectedEvent)));

        assertEquals(0, dataMessages.size());
    }

    @Test
    void createCompositeChart2(){
        ChartAggregate composite = new ChartAggregate(1, "s01", GRADES, LINE, null, null,
                new DataSummery(1, "s01", null, null, null), null);

        postAndVerifyChart(composite, ACCEPTED);

        final List<String> chartMessages = getMessages("charts");
        final List<String> dataMessages = getMessages("data");

        assertEquals(1, chartMessages.size());

        Event<Integer, Chart> expectedEvent = new Event(CREATE, composite.getChartId(),
                new Chart(composite.getChartId(), composite.getStudentId(), composite.getChartType(), composite.getChartLabel(), composite.getCreatedAt(), composite.getLastUpdate(), null));
        assertThat(chartMessages.get(0), is(sameEventExceptCreatedAt(expectedEvent)));

        assertEquals(1, dataMessages.size());

        DataSummery dat = composite.getDataSummery();
        Event<Integer, Chart> expectedDataEvent =
                new Event(CREATE, composite.getChartId(),
                        new Data(composite.getChartId(), dat.getDataId(), dat.getStudentId(), dat.getTranscripts(), dat.getCreatedAt(), dat.getLastUpdate(), null));
        assertThat(dataMessages.get(0), is(sameEventExceptCreatedAt(expectedDataEvent)));

    }

    @Test
    void deleteCompositeChart(){
        deleteAndVerifyChart(1, ACCEPTED);

        final List<String> chartMessages = getMessages("charts");
        final List<String> dataMessages = getMessages("data");

        assertEquals(1, chartMessages.size());
        Event<Integer, Chart> expectedChartEvent = new Event (DELETE, 1, null);
        assertThat(chartMessages.get(0), is(sameEventExceptCreatedAt(expectedChartEvent)));

        assertEquals(1, dataMessages.size());
        Event<Integer, Chart> expectedDataEvent = new Event (DELETE, 1, null);
        assertThat(dataMessages.get(0), is(sameEventExceptCreatedAt(expectedDataEvent)));

    }

    private void purgeMessages(String bindingName) {
        getMessages(bindingName);
    }

    private List<String> getMessages(String bindingName) {
        List<String> messages = new ArrayList<>();
        boolean anyMoreMessages = true;

        while (anyMoreMessages) {
            Message<byte[]> message = getMessage(bindingName);

            if (message == null) {
                anyMoreMessages = false;

            } else {
                messages.add(new String(message.getPayload()));
            }
        }
        return messages;
    }

    private Message<byte[]> getMessage(String bindingName) {
        try {
            return target.receive(0, bindingName);
        } catch (NullPointerException npe) {
            // If the messageQueues member variable in the target object contains no queues when the receive method is called, it will cause a NPE to be thrown.
            // So we catch the NPE here and return null to indicate that no messages were found.
            LOG.error("getMessage() received a NPE with binding = {}", bindingName);
            return null;
        }
    }

    private void postAndVerifyChart(ChartAggregate compositeChart, HttpStatus expectedStatus) {
        client.post()
                .uri("/chart-composite")
                .body(just(compositeChart), ChartAggregate.class)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    private void deleteAndVerifyChart(int chartId, HttpStatus expectedStatus) {
        client.delete()
                .uri("/chart-composite/" + chartId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }
}
