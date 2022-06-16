package htw.berlin.api.composite.chart;

import htw.berlin.api.core.chart.ChartLabel;
import htw.berlin.api.core.chart.ChartType;

import java.time.ZonedDateTime;

public class ChartAggregate {

    private final int chartId;
    private final String studentId;
    private final ChartLabel chartLabel;
    private final ChartType chartType;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime lastUpdate;
    private final DataSummery dataSummery;
    private final ServiceAddresses serviceAddresses;


    public ChartAggregate() {
        chartId = 0;
        studentId = null;
        chartLabel = null;
        chartType = null;
        createdAt = null;
        lastUpdate = null;
        dataSummery = null;
        serviceAddresses = null;

    }

    public ChartAggregate(int chartId,String studentId, ChartLabel chartLabel, ChartType chartType, ZonedDateTime createdAt, ZonedDateTime lastUpdate, DataSummery dataSummery, ServiceAddresses serviceAddresses) {
        this.chartId = chartId;
        this.studentId = studentId;
        this.chartLabel = chartLabel;
        this.chartType = chartType;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.dataSummery = dataSummery;
        this.serviceAddresses = serviceAddresses;
    }

    public int getChartId() {
        return chartId;
    }

    public String getStudentId() {
        return studentId;
    }

    public ChartLabel getChartLabel() {
        return chartLabel;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public DataSummery getDataSummery() {
        return dataSummery;
    }

    public ServiceAddresses getServiceAddresses() {
        return serviceAddresses;
    }
}
