package htw.berlin.api.composite.chart;

import htw.berlin.api.core.data.Transcript;

import java.util.Collection;
import java.util.Date;

public class DataSummery {

    private final int dataId;
    private final String studentId;
    private final Collection<Transcript> transcripts;
    private final Date createdAt;
    private final Date lastUpdate;

    public DataSummery() {
        dataId = 0;
        studentId = null;
        transcripts = null;
        createdAt = null;
        lastUpdate = null;
    }

    public DataSummery(int dataId, String studentId, Collection<Transcript> transcripts, Date createdAt, Date lastUpdate) {
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


    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

}
