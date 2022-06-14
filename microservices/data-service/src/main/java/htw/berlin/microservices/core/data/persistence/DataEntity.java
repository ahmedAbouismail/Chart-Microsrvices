package htw.berlin.microservices.core.data.persistence;

import htw.berlin.api.core.data.Transcript;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.Collection;

@Document(collection = "data")
@CompoundIndex(name = "chart-data-id", unique = true, def = "{'chartId': 1, 'dataId': 1}")
public class DataEntity {

    @Id
    private String id;

    @Version
    private Integer version;


    private int chartId;
    private int dataId;
    private String studentId;
    private Collection<TranscriptObj> transcripts;
    private ZonedDateTime createdAt;
    private ZonedDateTime lastUpdate;


    public DataEntity() {
    }

    public DataEntity(int dataId, int chartId,String studentId, Collection<TranscriptObj> transcripts, ZonedDateTime createdAt, ZonedDateTime lastUpdate) {
        this.dataId = dataId;
        this.chartId = chartId;
        this.studentId = studentId;
        this.transcripts = transcripts;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getChartId() {
        return chartId;
    }

    public void setChartId(int chartId) {
        this.chartId = chartId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Collection<TranscriptObj> getTranscripts() {
        return transcripts;
    }

    public void setTranscripts(Collection<TranscriptObj> transcripts) {
        this.transcripts = transcripts;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null){
            return false;
        }
        if (obj.getClass() != obj.getClass()){
            return false;
        }

        final DataEntity other = (DataEntity) obj;

        return this.getDataId() == ((DataEntity) obj).getDataId() &&
                this.getStudentId() == ((DataEntity) obj).getStudentId() &&
                this.getTranscripts() == ((DataEntity) obj).getTranscripts() &&
                this.getCreatedAt() == ((DataEntity) obj).getCreatedAt();
    }
}
