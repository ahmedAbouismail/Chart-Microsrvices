package htw.berlin.api.core.data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;


public interface IDataService {
    Mono<Data> createData(Data body);

    @GetMapping(
            value = "/data",
            produces = "application/json"
    )
    Mono<Data> getData(@RequestParam(value = "chartId", required = true) int chartId);

    Mono<Data> updateData(Data body);

    Mono<Void> deleteData(int chartId);
}
