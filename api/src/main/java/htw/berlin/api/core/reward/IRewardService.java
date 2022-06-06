package htw.berlin.api.core.reward;

import htw.berlin.api.core.chart.Chart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

public interface IRewardService {

    Mono<Chart> createReward(Reward body);

    @GetMapping(
            value = "/reward",
            produces = "application/json"
    )
    Mono<Chart> getReward(@RequestParam(value = "rewardId", required = true) int rewardId) ;

    Mono<Chart> updateChart(Reward body);

    Mono<Void> deleteChart(int rewardId);
}
