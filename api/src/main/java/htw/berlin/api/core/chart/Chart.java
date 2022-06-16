package htw.berlin.api.core.chart;

import java.time.ZonedDateTime;
import java.util.Collection;

public class Chart {
    private int chartId;
    private String studentId;
    private ChartLabel chartLabel;
    private ChartType chartType;
    private ZonedDateTime createdAt;
    private ZonedDateTime lastUpdate;
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

    public Chart(int chartId, String studentId, ChartType chartType, ChartLabel chartLabel, ZonedDateTime createdAt, ZonedDateTime lastUpdate, String serviceAddress) {
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
