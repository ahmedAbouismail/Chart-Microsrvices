package htw.berlin.microservices.core.data.services;

import htw.berlin.api.core.data.Data;
import htw.berlin.api.core.data.IDataService;
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

    private final IDataService dataService;

    @Autowired
    public MessageProcessorConfig(IDataService dataService) {
        this.dataService = dataService;
    }

    @Bean
    public Consumer<Event<Integer, Data>> messageProcessor(){
        return event->{
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()){
                case CREATE:
                    Data data = event.getData();
                    LOG.info("Create Data with ID: {}...", data.getDataId());
                    dataService.createData(data).block();
                    break;

                case UPDATE:
                    Data newData = event.getData();
                    LOG.info("Update Data with ID: {}...", newData.getDataId());
                    dataService.updateData(newData).block();
                    break;

                case DELETE:
                    int dataId = event.getKey();
                    LOG.info("Delete Data with ID: {}", dataId);
                    dataService.deleteData(dataId).block();
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                    LOG.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }
            LOG.info("Message processing done!");
        };
    }
}
