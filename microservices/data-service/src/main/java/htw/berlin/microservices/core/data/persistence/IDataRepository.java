package htw.berlin.microservices.core.data.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IDataRepository extends ReactiveCrudRepository<DataEntity, String> {

    Mono<DataEntity> findByChartId (int chartId);
    Mono<DataEntity> findByStudentId(String studentId);
}
