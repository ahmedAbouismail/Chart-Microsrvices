package htw.berlin.api.core.chart;

import java.util.Collection;
import java.util.Date;

public class Chart {
    private int chartId;
    private String studentId;
    private ChartLabel chartLabel;
    private ChartType chartType;
    private Date createdAt;
    private Date lastUpdate;
    private String serviceAddress;


    public Chart() {
        chartId = 0;
        studentId = null;
        chartLabel = null;
        chartType = null;
        createdAt = null;
        lastUpdate = null;
        serviceAddress = null;
    }

    public Chart(int chartId, String studentId, ChartType chartType, ChartLabel chartLabel, Date createdAt, Date lastUpdate, String serviceAddress) {
        this.chartId = chartId;
        this.studentId = studentId;
        this.chartLabel = chartLabel;
        this.chartType = chartType;
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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
