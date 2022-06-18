package htw.berlin.microservices.core.chart.services;

import htw.berlin.api.core.chart.Chart;
import htw.berlin.api.core.chart.IChartService;
import htw.berlin.api.exceptions.InvalidInputException;
import htw.berlin.api.exceptions.NotFoundException;
import htw.berlin.microservices.core.chart.persistence.ChartEntity;
import htw.berlin.microservices.core.chart.persistence.IChartRepository;
import htw.berlin.util.http.ServiceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;

@RestController
public class ChartServiceImpl implements IChartService {
    private static final Logger LOG = LoggerFactory.getLogger(ChartServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final IChartRepository repository;

    private final IChartMapper mapper;

    @Autowired
    public ChartServiceImpl(ServiceUtil serviceUtil, IChartRepository repository, IChartMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Chart> createChart(Chart body) {
        if (body.getChartId() < 1){
            throw new InvalidInputException("Invalid chartId: " + body.getChartId());
        }

        ChartEntity entity = mapper.apiToEntity(body);
        Mono<Chart> newEntity = repository.save(entity)
                .log(LOG.getName(), FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Chart Id: " + body.getChartId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity;
    }

    @Override
    public Mono<Chart> getChart(int chartId) {
        if (chartId < 1){
            throw new InvalidInputException("Invalid chartId: " + chartId);
        }
        LOG.info("Will get chart info for id0{}", chartId);


        return repository.findByChartId(chartId)
                .switchIfEmpty(Mono.error(new NotFoundException("No chart found for chartId: " + chartId)))
                .log(LOG.getName(), FINE)
                .map(e -> mapper.entityToApi(e))
                .map(e -> setServiceAddress(e));
    }

    @Override
    public Mono<Chart> updateChart(Chart body) {
        if (body.getChartId() < 1){
            throw new InvalidInputException("Invalid chartId: " + body.getChartId());
        }
        return repository.findByChartId(body.getChartId())
                .switchIfEmpty(Mono.error(new NotFoundException("No chart found for chartId: " + body.getChartId())))
                .map(e -> checkAndUpdate(e, body))
                .map(e-> setServiceAddress(e));
    }

    private Chart checkAndUpdate(ChartEntity e, Chart body) {
        //Only update if there is a difference
//        if (!checkEquals(e, body)){
//            e.setChartLabel(body.getChartLabel());
//            e.setChartType(body.getChartType());
//            repository.save(e);
//        }
        return mapper.entityToApi(e);
    }

    private boolean checkEquals(ChartEntity e, Chart body) {
        return e.getChartLabelObj().toString() == body.getChartLabel().toString() &&
                e.getChartTypeObj().toString() == body.getChartLabel().toString();
    }

    @Override
    public Mono<Void> deleteChart(int chartId) {
        if (chartId < 1) {
            throw new InvalidInputException("Invalid chartId: " + chartId);
        }
        LOG.debug("deleteChart: tries to delete an entity with chartId: {}", chartId);
        return repository.findByChartId(chartId).log(LOG.getName(), FINE).map(e -> repository.delete(e)).flatMap(e -> e);
    }

    private Chart setServiceAddress(Chart e) {
        e.setServiceAddress(serviceUtil.getServiceAddress());
        return e;
    }
}
