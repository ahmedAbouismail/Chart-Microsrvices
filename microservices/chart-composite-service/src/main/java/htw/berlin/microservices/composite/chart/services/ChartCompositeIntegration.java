package htw.berlin.microservices.composite.chart.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import htw.berlin.api.core.chart.Chart;
import htw.berlin.api.core.chart.IChartService;
import htw.berlin.api.core.data.Data;
import htw.berlin.api.core.data.IDataService;
import htw.berlin.api.event.Event;
import htw.berlin.api.exceptions.InvalidInputException;
import htw.berlin.api.exceptions.NotFoundException;
import htw.berlin.util.http.HttpErrorInfo;
import htw.berlin.util.http.ServiceUtil;
import java.io.IOException;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static htw.berlin.api.event.Event.Type.*;
import static java.util.logging.Level.FINE;

@Component
public class ChartCompositeIntegration implements IChartService, IDataService {

    private static final Logger LOG = LoggerFactory.getLogger(ChartCompositeIntegration.class);

    private static final String CHART_SERVICE_URL = "http://chart";
    private static final String DATA_SERVICE_URL = "http://data";

    private final Scheduler publishEventScheduler;
    private final WebClient webClient;
    private final ObjectMapper mapper;
    private final StreamBridge streamBridge;

    private final ServiceUtil serviceUtil;

    @Autowired
    public ChartCompositeIntegration(@Qualifier("publishEventScheduler") Scheduler publishEventScheduler, WebClient.Builder webClientBuilder, ObjectMapper mapper, StreamBridge streamBridge, ServiceUtil serviceUtil) {
        this.publishEventScheduler = publishEventScheduler;
        this.webClient = webClientBuilder.build();
        this.mapper = mapper;
        this.streamBridge = streamBridge;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<Chart> createChart(Chart body) {
        return Mono.fromCallable(()->{
            sendMessage("charts-out-0", new Event(CREATE, body.getChartId(), body));

            return body;
        }).subscribeOn(publishEventScheduler);
    }



    @Override
    public Mono<Chart> getChart(int chartId) {
        URI url = UriComponentsBuilder.fromUriString(CHART_SERVICE_URL + "/chart?chartId={chartId}").build(chartId);
        LOG.debug("Will call the getChart API on URL: {}", url);

        return webClient.get().uri(url)
                .retrieve().bodyToMono(Chart.class).log(LOG.getName(), FINE)
                .onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
    }

    @Override
    public Mono<Chart> updateChart(Chart body) {
        return Mono.fromCallable(() -> {
            sendMessage("charts-out-0", new Event(UPDATE, body.getChartId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);

    }

    @Override
    public Mono<Void> deleteChart(int chartId) {
        return Mono.fromRunnable(() -> sendMessage("charts-out-0", new Event(DELETE, chartId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    @Override
    public Mono<Data> createData(Data body) {
        return Mono.fromCallable(()->{
            sendMessage("data-out-0", new Event(CREATE, body.getChartId(), body));

            return body;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Data> getData(int chartId) {
        URI url = UriComponentsBuilder.fromUriString(DATA_SERVICE_URL + "/data?chartId={chartId}").build(chartId);
        LOG.debug("Will call the getData API on URL: {}", url);

        return webClient.get().uri(url)
                .retrieve().bodyToMono(Data.class).log(LOG.getName(), FINE)
                .onErrorResume(error -> Mono.empty());
    }

    @Override
    public Mono<Data> updateData(Data body) {
        return Mono.fromCallable(() -> {
            sendMessage("data-out-0", new Event(UPDATE, body.getChartId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteData(int chartId) {
        return Mono.fromRunnable(() -> sendMessage("data-out-0", new Event(DELETE, chartId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, Event event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(bindingName, message);
    }

    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException)ex;

        switch (wcre.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(wcre));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(wcre));

            default:
                LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

}
