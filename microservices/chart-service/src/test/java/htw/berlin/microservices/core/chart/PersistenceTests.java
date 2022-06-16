package htw.berlin.microservices.core.chart;

import htw.berlin.microservices.core.chart.persistence.ChartEntity;
import htw.berlin.microservices.core.chart.persistence.IChartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import static htw.berlin.microservices.core.chart.persistence.ChartLabelObj.GRADES;
import static htw.berlin.microservices.core.chart.persistence.ChartTypeObj.BAR;


@DataMongoTest(
        excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class,
        properties = {"spring.cloud.config.enabled=false"})
public class PersistenceTests extends MongoDbTestBase{

    @Autowired
    private IChartRepository repository;

    private ChartEntity savedEntity;

    @BeforeEach
    void setUpDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        ChartEntity entity = new ChartEntity(1, "s01", BAR, GRADES, null, null);
        StepVerifier.create(repository.save(entity))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return areChartEqual(entity, savedEntity);
                })
                .verifyComplete();
    }

    @Test
    void create(){
        ChartEntity newEntity = new ChartEntity(2, "s01", BAR, GRADES, null, null);

        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches(createdEntity -> newEntity.getChartId() == createdEntity.getChartId())
                .verifyComplete();

        StepVerifier.create(repository.findById(newEntity.getId()))
                .expectNextMatches(foundEntity -> {
                    return  areChartEqual(newEntity, foundEntity);
                })
                .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();
    }

    private boolean areChartEqual(ChartEntity expected, ChartEntity actual) {
        return  (expected.getId().equals(actual.getId())) &&
                (expected.getVersion() == actual.getVersion()) &&
                (expected.getChartId() == actual.getChartId()) &&
                (expected.getStudentId() == actual.getStudentId() )&&
                (expected.getChartType() == actual.getChartType() )&&
                (expected.getChartLabel() == actual.getChartLabel());
    }
}
