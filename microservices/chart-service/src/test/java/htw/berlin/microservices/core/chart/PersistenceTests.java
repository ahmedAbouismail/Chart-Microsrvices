package htw.berlin.microservices.core.chart;

import htw.berlin.microservices.core.chart.persistence.ChartEntity;
import htw.berlin.microservices.core.chart.persistence.IChartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.test.StepVerifier;

import static htw.berlin.microservices.core.chart.persistence.ChartLabelObj.GRADES;
import static htw.berlin.microservices.core.chart.persistence.ChartTypeObj.BAR;
import static htw.berlin.microservices.core.chart.persistence.ChartTypeObj.LINE;


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
                .expectNextMatches(foundEntity -> areChartEqual(newEntity, foundEntity))
                .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();
    }

    @Test
    void update() {
        savedEntity.setChartTypeObj(LINE);
        StepVerifier.create(repository.save(savedEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getChartTypeObj().toString().equals(savedEntity.getChartTypeObj().toString()) )
                .verifyComplete();

        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1
                                && foundEntity.getChartTypeObj().toString().equals(savedEntity.getChartTypeObj().toString()))
                .verifyComplete();
    }

    @Test
    void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }

    @Test
    void getByChartId() {

        StepVerifier.create(repository.findByChartId(savedEntity.getChartId()))
                .expectNextMatches(foundEntity -> areChartEqual(savedEntity, foundEntity))
                .verifyComplete();
    }

    @Test
    void duplicateError() {
        ChartEntity entity = new ChartEntity(1, "s01", BAR, GRADES, null, null);
        StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();
    }

    @Test
    void optimisticLockError() {

        ChartEntity entity1 = repository.findById(savedEntity.getId()).block();
        ChartEntity entity2 = repository.findById(savedEntity.getId()).block();

        entity1.setStudentId("n1");
        repository.save(entity1).block();

        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1
                                && foundEntity.getStudentId().equals("n1"))
                .verifyComplete();
    }

    private boolean areChartEqual(ChartEntity expected, ChartEntity actual) {
        return  (expected.getId().equals(actual.getId())) &&
                (expected.getVersion() == actual.getVersion()) &&
                (expected.getChartId() == actual.getChartId()) &&
                (expected.getStudentId().equals(actual.getStudentId()) ) &&
                (expected.getChartTypeObj().toString().equals(actual.getChartTypeObj().toString()))&&
                (expected.getChartLabelObj().toString().equals(actual.getChartLabelObj().toString())) &&
                expected.getCreatedAt() == actual.getCreatedAt() &&
                expected.getLastUpdate() == actual.getLastUpdate();
    }
}
