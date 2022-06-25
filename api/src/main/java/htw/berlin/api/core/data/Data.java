package htw.berlin.api.core.data;

import java.util.Collection;
import java.util.Date;

public class Data {
    private int chartId;
    private int dataId;
    private String studentId;
    private Collection<Transcript> transcripts;
    private Date createdAt;
    private Date lastUpdate;
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

    public Data(int chartId, int dataId, String studentId, Collection<Transcript> transcripts, Date createdAt, Date lastUpdate,String serviceAddress) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }


}
