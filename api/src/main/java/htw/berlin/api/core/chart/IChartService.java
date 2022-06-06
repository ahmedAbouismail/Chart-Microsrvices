package htw.berlin.api.core.chart;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

public interface IChartService {

    Mono<Chart> createChart(Chart body);

    @GetMapping(
            value = "/chart",
            produces = "application/json"
    )
    Mono<Chart> getChart(@RequestParam(value = "chartId", required = false) int chartId,
                         @RequestParam(value = "studentId", required = true) String studentId,
                         @RequestParam(value = "chartType", required = true) String chartType,
                         @RequestParam(value = "chartLabel", required = true) String chartLabel) ;

    Mono<Chart> updateChart(Chart body);

    Mono<Void> deleteChart(int chartId);
}
