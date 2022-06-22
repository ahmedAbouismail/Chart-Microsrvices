package htw.berlin.microservices.core.data.services;

import htw.berlin.api.core.data.Data;
import htw.berlin.api.core.data.IDataService;
import htw.berlin.api.exceptions.InvalidInputException;
import htw.berlin.api.exceptions.NotFoundException;
import htw.berlin.microservices.core.data.persistence.DataEntity;
import htw.berlin.microservices.core.data.persistence.IDataRepository;
import htw.berlin.microservices.core.data.persistence.TranscriptObj;
import htw.berlin.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


import java.time.ZonedDateTime;
import java.util.ArrayList;

import static java.util.logging.Level.FINE;

@RestController
public class  DataServiceImpl implements IDataService {
    private static final Logger LOG = LoggerFactory.getLogger(DataServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final IDataRepository dataRepository;

    private final IDataMapper dataMapper;

    @Autowired
    public DataServiceImpl(ServiceUtil serviceUtil, IDataRepository dataRepository, IDataMapper dataMapper) {
        this.serviceUtil = serviceUtil;
        this.dataRepository = dataRepository;
        this.dataMapper = dataMapper;
    }

    @Override
    public Mono<Data> createData(Data body) {

        if (body.getChartId() < 1) {
            throw new InvalidInputException("Invalid dataId: " + body.getDataId());
        }

        DataEntity entity = dataMapper.apiToEntity(body);
        entity = calculateSemesterAverage(entity);
        entity = calculateCreditsTotal(entity);
        Mono<Data> newEntity = dataRepository.save(entity)
                .log(LOG.getName(), FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Chart Id: " + body.getChartId() + ", Data Id: " + body.getDataId()))
                .map(e -> dataMapper.entityToApi(e));

        return newEntity;
    }


    private DataEntity calculateSemesterAverage(DataEntity entity) {
        if (entity.getTranscripts() != null){
            Double sum = 0.0;
            long count = 0;
            Double average = 0.0;
            ArrayList<TranscriptObj> transcriptObjs = new ArrayList<>(entity.getTranscripts());

            for (TranscriptObj transcriptObj : transcriptObjs) {
                sum = 0.0;
                count = 0;
                average = 0.0;
                sum = transcriptObj.getGrades().stream().mapToDouble(value -> value.getGrade()).sum();
                count = transcriptObj.getGrades().stream().count();
                average = count == 0 ? 0.0 : (sum/count);
                transcriptObj.setAverage(average);
            }
        }
        return entity;
    }

    private DataEntity calculateCreditsTotal(DataEntity entity) {
        if (entity.getTranscripts() != null){
            int total = 0;
            ArrayList<TranscriptObj> transcriptObjs = new ArrayList<>(entity.getTranscripts());

            for (TranscriptObj transcriptObj : transcriptObjs){
                total = transcriptObj.getGrades().stream().mapToInt(value -> value.getCredits()).sum();
                transcriptObj.setCreditsTotal(total);
            }
        }
        return entity;
    }


    @Override
    public Mono<Data> getData(int chartId) {
        if (chartId < 1) {
            throw new InvalidInputException("Invalid chartId: " + chartId);
        }

        LOG.info("Will get data info for chart with id={}", chartId);


        return dataRepository.findByChartId(chartId)
                .log(LOG.getName(), FINE)
                .map(e -> dataMapper.entityToApi(e))
                .map(e -> setServiceAddress(e));
    }

    @Override
    public Mono<Data> updateData(Data body) {
        if (body.getChartId() < 1) {
            throw new InvalidInputException("Invalid chartId: " + body.getChartId());
        }

        LOG.info("Will update data with chart id: {}", body.getChartId());

        DataEntity newEntity = dataMapper.apiToEntity(body);


         Mono<DataEntity> updatedEntity = dataRepository.findByChartId(body.getChartId())
        .switchIfEmpty(Mono.error(new NotFoundException("updateData: Can't find Data with data Id: " + body.getDataId())))
        .map(foundEntity -> updateDataEntity(foundEntity, newEntity));

        Mono<Data> updatedData = dataRepository.save(updatedEntity.block())
                .log(LOG.getName(), FINE)
                .switchIfEmpty(Mono.error(new NotFoundException("updateDate: Can't find Data with data Id: " + body.getDataId())))
                .map(e -> dataMapper.entityToApi(e));

        return updatedData;

    }


    private DataEntity updateDataEntity(DataEntity foundEntity, DataEntity newEntity) {
        if (foundEntity != null && newEntity != null){
            foundEntity.setTranscripts(newEntity.getTranscripts());
        }

        return foundEntity;
    }


    @Override
    public Mono<Void> deleteData(int chartId) {
        if (chartId < 1) {
            throw new InvalidInputException("Invalid chartId: " + chartId);
        }


        LOG.debug("deleteData: tries to delete data for the chart with chartId: {}", chartId);

        return dataRepository.deleteAll(dataRepository.findByChartId(chartId));
    }


    private Data setServiceAddress(Data e) {
        e.setServiceAddress(serviceUtil.getServiceAddress());
        return e;
    }
}
