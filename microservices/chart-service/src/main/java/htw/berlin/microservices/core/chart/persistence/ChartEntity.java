package htw.berlin.microservices.core.chart.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Document(collection = "charts")
public class ChartEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private int chartId;

    private String studentId;

    private ChartTypeObj chartTypeObj;

    private ChartLabelObj chartLabelObj;

    private Date createdAt;

    private Date lastUpdate;

    public ChartEntity() {
    }

    public ChartEntity(int chartId, String studentId, ChartTypeObj chartTypeObj, ChartLabelObj chartLabelObj, Date createdAt, Date lastUpdate) {
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

    public ChartTypeObj getChartTypeObj() {
        return chartTypeObj;
    }

    public void setChartTypeObj(ChartTypeObj chartTypeObj) {
        this.chartTypeObj = chartTypeObj;
    }

    public ChartLabelObj getChartLabelObj() {
        return chartLabelObj;
    }

    public void setChartLabelObj(ChartLabelObj chartLabelObj) {
        this.chartLabelObj = chartLabelObj;
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
}
