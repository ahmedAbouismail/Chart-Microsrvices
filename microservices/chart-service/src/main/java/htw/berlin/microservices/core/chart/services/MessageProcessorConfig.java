package htw.berlin.microservices.core.chart.services;


import htw.berlin.api.core.chart.Chart;
import htw.berlin.api.core.chart.IChartService;
import htw.berlin.api.event.Event;
import htw.berlin.api.exceptions.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final IChartService chartService;

    @Autowired
    public MessageProcessorConfig(IChartService chartService) {
        this.chartService = chartService;
    }

    @Bean
    public Consumer<Event<Integer, Chart>> messageProcessor(){
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()){
                case CREATE:
                    Chart chart = event.getData();
                    LOG.info("Create Chart with ID: {}", chart.getChartId());
                    chartService.createChart(chart).block();
                    break;

                case UPDATE:
                    Chart newChart = event.getData();
                    LOG.info("Update Chart with ID: {}", newChart.getChartId());
                    chartService.updateChart(newChart).block();
                    break;

                case DELETE:
                    int chartId = event.getKey();
                    LOG.info("Delete Chart with ChartID: {}", chartId);
                    chartService.deleteChart(chartId).block();
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE UPDATE, or DELETE event";
                    LOG.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);

            }
            LOG.info("Message processing done!");
        };
    }
}
