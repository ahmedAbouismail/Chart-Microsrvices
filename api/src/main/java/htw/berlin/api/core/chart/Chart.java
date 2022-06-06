package htw.berlin.api.core.chart;

import java.time.ZonedDateTime;
import java.util.Collection;

public class Chart {
    private int chartId;
    private String studentId;
    private String studentEmail;
    private ChartLabel chartLabel;
    private ChartType chartType;
    private Collection<Transcript> transcripts;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public Chart() {
        chartId = 0;
        studentId = null;
        studentEmail = null;
        chartLabel = null;
        chartType = null;
        transcripts = null;
        createdAt = null;
        updatedAt = null;
    }

    public Chart(int chartId, String studentId, String studentEmail, ChartLabel chartLabel,ChartType chartType, Collection<Transcript> transcripts, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.chartId = chartId;
        this.studentId = studentId;
        this.studentEmail = studentEmail;
        this.chartLabel = chartLabel;
        this.chartType = chartType;
        this.transcripts = transcripts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getChartId() {
        return chartId;
    }

    public void setChartId(int chartId) {
        this.chartId = chartId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public ChartLabel getChartLabel() {
        return chartLabel;
    }

    public void setChartLabel(ChartLabel chartLabel) {
        this.chartLabel = chartLabel;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
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

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
