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
    Mono<Chart> getChart(@RequestParam(value = "chartId", required = true) int chartId,
                         @RequestParam(value = "studentId", required = false) String studentId,
                         @RequestParam(value = "chartType", required = false) String chartType,
                         @RequestParam(value = "chartLabel", required = false) String chartLabel) ;

    Mono<Chart> updateChart(Chart body);

    Mono<Void> deleteChart(int chartId);
}
