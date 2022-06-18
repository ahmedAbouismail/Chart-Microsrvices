package htw.berlin.microservices.core.chart.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface IChartRepository extends ReactiveCrudRepository<ChartEntity, String> {
    Mono<ChartEntity> findByChartId(int chartId);
}
