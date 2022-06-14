package htw.berlin.api.core.data;

import java.time.ZonedDateTime;
import java.util.Collection;

public class Data {
    private int chartId;
    private int dataId;
    private String studentId;
    private Collection<Transcript> transcripts;
    private ZonedDateTime createdAt;
    private ZonedDateTime lastUpdate;
    private String serviceAddress;

    public Data() {
        chartId = 0;
        dataId = 0;
        studentId = null;
        transcripts = null;
        createdAt = null;
        lastUpdate = null;
        serviceAddress = null;
    }

    public Data(int chartId, int dataId, String studentId, Collection<Transcript> transcripts, ZonedDateTime createdAt, ZonedDateTime lastUpdate,String serviceAddress) {
        this.chartId = chartId;
        this.dataId = dataId;
        this.studentId = studentId;
        this.transcripts = transcripts;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.serviceAddress = serviceAddress;
    }

    public int getChartId() {
        return chartId;
    }

    public void setChartId(int chartId) {
        this.chartId = chartId;
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

    public Collection<Transcript> getTranscripts() {
        return transcripts;
    }

    public void setTranscripts(Collection<Transcript> transcripts) {
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

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }


}
