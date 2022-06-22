package htw.berlin.api.composite.chart;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@SecurityRequirement(name = "security_auth")
@Tag(name = "ChartComposite", description = "REST API for composite chart information.")
public interface IChartCompositeService {

    @Operation(
            summary = "${api.chart-composite.create-composite-chart.description}",
            description = "${api.chart-composite.create-composite-chart.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(
            value = "/chart-composite",
            consumes = "application/json"
    )
    Mono<Void> createChart(@RequestBody ChartAggregate body);

    @Operation(
            summary = "${api.chart-composite.get-composite-chart.description}",
            description = "${api.chart-composite.get-composite-chart.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @GetMapping(
            value = "/chart-composite/{chartId}",
            produces = "application/json")
    Mono<ChartAggregate> getChart(@PathVariable int chartId);


    @Operation(
            summary = "${api.chart-composite.update-composite-chart.description}",
            description = "${api.chart-composite.update-composite-chart.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @PatchMapping(
            value = "/chart-composite",
            consumes = "application/json")
    Mono<ChartAggregate> updateChart(@RequestBody ChartAggregate body);

    @Operation(
            summary = "${api.chart-composite.delete-composite-chart.description}",
            description = "${api.chart-composite.delete-composite-chart.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(value = "/chart-composite/{chartId}")
    Mono<Void> deleteChart(@PathVariable int chartId);
}
