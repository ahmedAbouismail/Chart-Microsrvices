package htw.berlin.microservices.core.chart;

import static htw.berlin.api.core.chart.ChartLabel.GRADES;
import static htw.berlin.api.core.chart.ChartType.BAR;
import static org.junit.jupiter.api.Assertions.*;

import htw.berlin.api.core.chart.Chart;
import htw.berlin.microservices.core.chart.persistence.ChartEntity;
import htw.berlin.microservices.core.chart.services.IChartMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class MapperTests {

    private IChartMapper mapper = Mappers.getMapper(IChartMapper.class);

    @Test
    void mapperTests(){
        assertNotNull(mapper);

        Chart api = new Chart(1, "s01", BAR, GRADES, null, null, "sa");

        ChartEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getChartId(), entity.getChartId());
        assertEquals(api.getStudentId(), entity.getStudentId());
        assertEquals(api.getChartLabel().toString(), entity.getChartLabelObj().toString());
        assertEquals(api.getChartType().toString(), entity.getChartTypeObj().toString());

        Chart api2 = mapper.entityToApi(entity);

        assertEquals(api.getChartId(), api2.getChartId());
        assertEquals(api.getStudentId(), api2.getStudentId());
        assertEquals(api.getChartLabel(), api2.getChartLabel());
        assertEquals(api.getChartType(), api2.getChartType());
        assertNull(api2.getServiceAddress());

    }
}
