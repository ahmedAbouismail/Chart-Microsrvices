package htw.berlin.api.core.chart;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;


public interface IChartService {
    Mono<Chart> createChart(Chart body);

    @GetMapping(
            value = "/chart",
            produces = "application/json"
    )
    Mono<Chart> getChart(@RequestParam(value = "chartId", required = true) int chartId) ;

    Mono<Chart> updateChart(Chart body);

    Mono<Void> deleteChart(int chartId);
}
