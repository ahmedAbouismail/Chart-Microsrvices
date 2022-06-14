package htw.berlin.microservices.core.chart.services;

import htw.berlin.api.core.chart.Chart;
import htw.berlin.microservices.core.chart.persistence.ChartEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IChartMapper {
    @Mappings({
            @Mapping(target= "serviceAddress", ignore = true)
    })
    Chart entityToApi(ChartEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true)
    })
    ChartEntity apiToEntity(Chart chart);
}
