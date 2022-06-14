package htw.berlin.microservices.core.chart.persistence;

import htw.berlin.api.core.chart.ChartLabel;
import htw.berlin.api.core.chart.ChartType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.Collection;

@Document(collection = "charts")
public class ChartEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private int chartId;

    @Indexed(unique = true)
    private String studentId;

    private ChartType chartTypeObj;

    private ChartLabel chartLabelObj;

    private ZonedDateTime createdAt;

    private ZonedDateTime lastUpdate;

    public ChartEntity() {
    }

    public ChartEntity(int chartId, String studentId, ChartType chartTypeObj, ChartLabel chartLabelObj, ZonedDateTime createdAt, ZonedDateTime lastUpdate) {
        this.chartId = chartId;
        this.studentId = studentId;
        this.chartTypeObj = chartTypeObj;
        this.chartLabelObj = chartLabelObj;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public ChartType getChartType() {
        return chartTypeObj;
    }

    public void setChartType(ChartType chartTypeObj) {
        this.chartTypeObj = chartTypeObj;
    }

    public ChartLabel getChartLabel() {
        return chartLabelObj;
    }

    public void setChartLabel(ChartLabel chartLabelObj) {
        this.chartLabelObj = chartLabelObj;
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
}
