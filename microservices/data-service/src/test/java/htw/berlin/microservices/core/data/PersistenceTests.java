package htw.berlin.microservices.core.data;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import htw.berlin.microservices.core.data.persistence.DataEntity;
import htw.berlin.microservices.core.data.persistence.IDataRepository;
import htw.berlin.microservices.core.data.persistence.ModuleGradeObj;
import htw.berlin.microservices.core.data.persistence.TranscriptObj;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;

@DataMongoTest(
        excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class,
        properties = {"spring.cloud.config.enabled=false"})
public class PersistenceTests extends MongoDbTestBase{

    @Autowired
    private IDataRepository repository;

    private DataEntity savedEntity;

    @BeforeEach
    void setupDb(){
        repository.deleteAll().block();

        List<ModuleGradeObj> moduleGrades = new ArrayList<>();
        moduleGrades.add( new ModuleGradeObj(10.0, "Math1", "math", 5));

        List<TranscriptObj> transcripts = new ArrayList<>();
        transcripts.add(new TranscriptObj("ss22", moduleGrades, 0, 0));

        DataEntity entity = new DataEntity(1, 2, "s0", transcripts, null, null);

        savedEntity = repository.save(entity).block();

        assertEqualsData(entity, savedEntity);
    }

    @Test
    void create(){
        List<ModuleGradeObj> moduleGrades = new ArrayList<>();
        moduleGrades.add( new ModuleGradeObj(10.0, "Math1", "math", 5));

        List<TranscriptObj> transcripts = new ArrayList<>();
        transcripts.add(new TranscriptObj("ss22", moduleGrades, 0, 0));

        DataEntity newEntity = new DataEntity(2, 1, "s0", transcripts, null, null);
        repository.save(newEntity).block();

        DataEntity foundEntity = repository.findById(newEntity.getId()).block();
        assertEqualsData(newEntity, foundEntity);

        assertEquals(2, (long)repository.count().block());
    }

    @Test
    void update() {
        savedEntity.setStudentId("a2");
        repository.save(savedEntity).block();

        DataEntity foundEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("a2", foundEntity.getStudentId());
    }

    @Test
    void delete() {
        repository.delete(savedEntity).block();
        assertFalse(repository.existsById(savedEntity.getId()).block());
    }

    @Test
    void getByChartId() {
        DataEntity entity = repository.findByChartId(savedEntity.getChartId()).block();
        assertEqualsData(savedEntity, entity);
    }

    @Test
    void duplicateError() {
        List<ModuleGradeObj> moduleGrades = new ArrayList<>();
        moduleGrades.add( new ModuleGradeObj(10.0, "Math1", "math", 5));

        List<TranscriptObj> transcripts = new ArrayList<>();
        transcripts.add(new TranscriptObj("ss22", moduleGrades, 0, 0));


        assertThrows(DuplicateKeyException.class, () -> {
            DataEntity entity = new DataEntity(1, 2, "s0", transcripts, null, null);
            repository.save(entity).block();
        });
    }

    @Test
    void optimisticLockError() {


        DataEntity entity1 = repository.findById(savedEntity.getId()).block();
        DataEntity entity2 = repository.findById(savedEntity.getId()).block();


        entity1.setStudentId("a1");
        repository.save(entity1).block();


        assertThrows(OptimisticLockingFailureException.class, () -> {
            entity2.setStudentId("a2");
            repository.save(entity2).block();
        });


        DataEntity updatedEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getStudentId());
    }


    private void assertEqualsData(DataEntity expected, DataEntity actual) {
        assertEquals(expected.getId(),          actual.getId());
        assertEquals(expected.getVersion(),     actual.getVersion());
        assertEquals(expected.getDataId(),      actual.getDataId());
        assertEquals(expected.getChartId(),     actual.getChartId());
        assertEquals(expected.getStudentId(),   actual.getStudentId());
        assertEquals(expected.getVersion(),     actual.getVersion());

        assertTranscriptEquals(expected.getTranscripts(), actual.getTranscripts());

    }

    private boolean assertTranscriptEquals(Collection<TranscriptObj>  expected, Collection<TranscriptObj> actual) {
        ArrayList<TranscriptObj> expectedTranscripts = new ArrayList<>(expected);
        TranscriptObj expectedTranscript = expectedTranscripts.get(0);

        ArrayList<TranscriptObj> actualTranscripts = new ArrayList<>(actual);
        TranscriptObj actualTranscript = actualTranscripts.get(0);

        return  expectedTranscript.getSemester() == actualTranscript.getSemester() &&
                expectedTranscript.getAverage() == actualTranscript.getAverage() &&
                expectedTranscript.getCreditsTotal() == actualTranscript.getCreditsTotal() &&
                asserGradsEquals(expectedTranscript.getGrades(), actualTranscript.getGrades());
    }

    private boolean asserGradsEquals(Collection<ModuleGradeObj> expected, Collection<ModuleGradeObj> actual) {
        ArrayList<ModuleGradeObj> expectedGrades  = new ArrayList<>(expected);
        ModuleGradeObj expectedGrade = expectedGrades.get(0);

        ArrayList<ModuleGradeObj> actualGrades  = new ArrayList<>(actual);
        ModuleGradeObj actualGrade = actualGrades.get(0);

        return expectedGrade.getGrade() == actualGrade.getGrade() &&
                expectedGrade.getCredits() == actualGrade.getCredits() &&
                expectedGrade.getLabel() == actualGrade.getLabel() &&
                expectedGrade.getModule() == actualGrade.getModule();
    }
}
