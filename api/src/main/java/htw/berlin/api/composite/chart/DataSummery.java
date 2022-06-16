package htw.berlin.api.composite.chart;

import htw.berlin.api.core.data.Transcript;

import java.time.ZonedDateTime;
import java.util.Collection;

public class DataSummery {

    private final int dataId;
    private final String studentId;
    private final Collection<Transcript> transcripts;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime lastUpdate;

    public DataSummery() {
        dataId = 0;
        studentId = null;
        transcripts = null;
        createdAt = null;
        lastUpdate = null;
    }

    public DataSummery(int dataId, String studentId, Collection<Transcript> transcripts, ZonedDateTime createdAt, ZonedDateTime lastUpdate) {
        this.dataId = dataId;
        this.studentId = studentId;
        this.transcripts = transcripts;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;

    }

    public int getDataId() {
        return dataId;
    }

    public String getStudentId() {
        return studentId;
    }

    public Collection<Transcript> getTranscripts() {
        return transcripts;
    }


    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

}
