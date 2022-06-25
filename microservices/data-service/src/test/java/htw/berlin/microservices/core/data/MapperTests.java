package htw.berlin.microservices.core.data;

import static org.junit.jupiter.api.Assertions.*;

import htw.berlin.api.core.data.Data;
import htw.berlin.api.core.data.ModuleGrade;
import htw.berlin.api.core.data.Transcript;
import htw.berlin.microservices.core.data.persistence.DataEntity;
import htw.berlin.microservices.core.data.persistence.ModuleGradeObj;
import htw.berlin.microservices.core.data.persistence.TranscriptObj;
import htw.berlin.microservices.core.data.services.IDataMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;
import java.util.*;

public class MapperTests {

    private IDataMapper mapper = Mappers.getMapper(IDataMapper.class);

    @Test
    void mapperTests(){
        assertNotNull(mapper);
        List<ModuleGrade> moduleGrades = new ArrayList<>();
        moduleGrades.add( new ModuleGrade(10.0, "Math1", "math", 5));

        List<Transcript> transcripts = new ArrayList<>();

        transcripts.add(new Transcript("ss22", moduleGrades, 0, 0));

        Data api = new Data(1, 2, "s0", transcripts, null, null, "sa");

        DataEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getChartId(), entity.getChartId());
        assertEquals(api.getDataId(), entity.getDataId());
        assertEquals(api.getStudentId(), entity.getStudentId());
        assertTrue(assertTranscriptEquals(api.getTranscripts(), entity.getTranscripts()));

        Data api2 = mapper.entityToApi(entity);

        assertEquals(api.getChartId(), api2.getChartId());
        assertEquals(api.getDataId(), api2.getDataId());
        assertEquals(api.getStudentId(), api2.getStudentId());
        assertTrue(assertTranscriptEqualsApi(api.getTranscripts(), api2.getTranscripts()));
        assertNull(api2.getServiceAddress());
    }

    private boolean assertTranscriptEqualsApi(Collection<Transcript> expected, Collection<Transcript> actual) {
        ArrayList<Transcript> expectedTranscripts = new ArrayList<>(expected);
        Transcript expectedTranscript = expectedTranscripts.get(0);

        ArrayList<Transcript> actualTranscripts = new ArrayList<>(actual);
        Transcript actualTranscript = actualTranscripts.get(0);

        return  expectedTranscript.getSemester() == actualTranscript.getSemester() &&
                expectedTranscript.getAverage() == actualTranscript.getAverage() &&
                expectedTranscript.getCreditsTotal() == actualTranscript.getCreditsTotal() &&
                asserGradsEqualsApi(expectedTranscript.getGrades(), actualTranscript.getGrades());
    }

    private boolean asserGradsEqualsApi(Collection<ModuleGrade> expected, Collection<ModuleGrade> actual) {
        ArrayList<ModuleGrade> expectedGrades  = new ArrayList<>(expected);
        ModuleGrade expectedGrade = expectedGrades.get(0);

        ArrayList<ModuleGrade> actualGrades  = new ArrayList<>(actual);
        ModuleGrade actualGrade = actualGrades.get(0);

        return expectedGrade.getGrade() == actualGrade.getGrade() &&
                expectedGrade.getCredits() == actualGrade.getCredits() &&
                expectedGrade.getLabel() == actualGrade.getLabel() &&
                expectedGrade.getModule() == actualGrade.getModule();
    }

    private boolean assertTranscriptEquals(Collection<Transcript> expected, Collection<TranscriptObj> actual) {
        ArrayList<Transcript> expectedTranscripts = new ArrayList<>(expected);
        Transcript expectedTranscript = expectedTranscripts.get(0);

        ArrayList<TranscriptObj> actualTranscripts = new ArrayList<>(actual);
        TranscriptObj actualTranscript = actualTranscripts.get(0);

        return  expectedTranscript.getSemester() == actualTranscript.getSemester() &&
                expectedTranscript.getAverage() == actualTranscript.getAverage() &&
                expectedTranscript.getCreditsTotal() == actualTranscript.getCreditsTotal() &&
                asserGradsEquals(expectedTranscript.getGrades(), actualTranscript.getGrades());
    }

    private boolean asserGradsEquals(Collection<ModuleGrade> expected, Collection<ModuleGradeObj> actual) {
        ArrayList<ModuleGrade> expectedGrades  = new ArrayList<>(expected);
        ModuleGrade expectedGrade = expectedGrades.get(0);

        ArrayList<ModuleGradeObj> actualGrades  = new ArrayList<>(actual);
        ModuleGradeObj actualGrade = actualGrades.get(0);

        return expectedGrade.getGrade() == actualGrade.getGrade() &&
                expectedGrade.getCredits() == actualGrade.getCredits() &&
                expectedGrade.getLabel() == actualGrade.getLabel() &&
                expectedGrade.getModule() == actualGrade.getModule();
    }


}
