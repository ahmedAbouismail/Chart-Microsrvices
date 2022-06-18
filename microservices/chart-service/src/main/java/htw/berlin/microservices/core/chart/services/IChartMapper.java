package htw.berlin.microservices.core.chart.services;

import htw.berlin.api.core.chart.Chart;
import htw.berlin.api.core.chart.ChartLabel;
import htw.berlin.api.core.chart.ChartType;
import htw.berlin.microservices.core.chart.persistence.ChartEntity;
import htw.berlin.microservices.core.chart.persistence.ChartLabelObj;
import htw.berlin.microservices.core.chart.persistence.ChartTypeObj;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IChartMapper {
    @Mappings({
            @Mapping(target= "serviceAddress", ignore = true),
            @Mapping(target = "chartLabel", source = "chartLabelObj"),
            @Mapping(target = "chartType", source = "chartTypeObj")
    })
    Chart entityToApi(ChartEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "chartLabelObj", source = "chartLabel"),
            @Mapping(target = "chartTypeObj", source = "chartType")
    })
    ChartEntity apiToEntity(Chart chart);
}
