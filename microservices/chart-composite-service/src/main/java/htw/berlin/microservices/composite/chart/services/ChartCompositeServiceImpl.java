package htw.berlin.microservices.composite.chart.services;


import static java.util.logging.Level.FINE;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import htw.berlin.api.composite.chart.ChartAggregate;
import htw.berlin.api.composite.chart.DataSummery;
import htw.berlin.api.composite.chart.IChartCompositeService;
import htw.berlin.api.composite.chart.ServiceAddresses;
import htw.berlin.api.core.chart.Chart;
import htw.berlin.api.core.chart.ChartLabel;
import htw.berlin.api.core.chart.ChartType;
import htw.berlin.api.core.data.Data;
import htw.berlin.util.http.ServiceUtil;

@RestController
public class ChartCompositeServiceImpl implements IChartCompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(ChartCompositeServiceImpl.class);

    private final SecurityContext nullSecCtx = new SecurityContextImpl();

    private final ServiceUtil serviceUtil;

    private final ChartCompositeIntegration integration;

    @Autowired
    public ChartCompositeServiceImpl(ServiceUtil serviceUtil, ChartCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public Mono<Void> createChart(ChartAggregate body) {

        try {
            List<Mono> monoList = new ArrayList<>();

            monoList.add(getLogAuthorizationInfoMono());

            LOG.debug("createCompositeChart: creates a new composite entity for chartID: {}", body.getChartId());

            Chart chart = new Chart(body.getChartId(), body.getStudentId(), body.getChartType(), body.getChartLabel(), body.getCreatedAt(), body.getLastUpdate(), null);
            monoList.add(integration.createChart(chart));

            if (body.getDataSummery() != null){
                DataSummery dataSummery = body.getDataSummery();
                Data data = new Data(body.getChartId(), dataSummery.getDataId(), dataSummery.getStudentId(), dataSummery.getTranscripts(), dataSummery.getCreatedAt(), dataSummery.getLastUpdate(), null);
                monoList.add(integration.createData(data));
            }

            LOG.debug("createCompositeChart: composite entities created for chartId: {}", body.getChartId());

            return Mono.zip(r -> "", monoList.toArray(new Mono[0]))
                    .doOnError(ex -> LOG.warn("createCompositeChart failed: {}", ex.toString()))
                    .then();
        }catch (RuntimeException re){
            LOG.warn("createCompositeChart failed: {}", re.toString());
            throw  re;
        }
    }

    @Override
    public Mono<ChartAggregate> getChart(int chartId) {
        LOG.info("Will get composite chart info for chart.id={}", chartId);
        return Mono.zip(
                values -> createChartAggregate(
                        (SecurityContext) values[0], (Chart) values[1], (Data) values[2], serviceUtil.getServiceAddress()),
                getSecurityContextMono(),
                integration.getChart(chartId),
                integration.getData(chartId))
                .doOnError(ex -> LOG.warn("getCompositeChart failed: {}", ex.toString()))
                .log(LOG.getName(), FINE);
    }


    @Override
    public Mono<ChartAggregate> updateChart(ChartAggregate body) {
        LOG.info("Will update composite chart info for chart.id={}", body.getChartId());

        Chart chart = new Chart(body.getChartId(), body.getStudentId(), body.getChartType(), body.getChartLabel(), body.getCreatedAt(), body.getLastUpdate(), null);
        return Mono.zip(
                values -> createChartAggregate(
                        (SecurityContext) values[0], (Chart) values[1], (Data) values[2], serviceUtil.getServiceAddress()),
                getSecurityContextMono(),
                integration.updateChart(chart),
                integration.getData(body.getChartId()))
                .doOnError(ex -> LOG.warn("updateCompositeChart failed; {}", ex.toString()))
                .log(LOG.getName(), FINE);
    }

    @Override
    public Mono<Void> deleteChart(int chartId) {
        try {
            LOG.debug("deleteCompositeChart : Delete a chart aggregate for chartId: {}", chartId);

            return Mono.zip(r->"",
                    getLogAuthorizationInfoMono(),
                    integration.deleteChart(chartId),
                    integration.deleteData(chartId))
                    .doOnError(ex -> LOG.warn("delete failed: {}", ex.toString()))
                    .log(LOG.getName(), FINE).then();
        }catch (RuntimeException re){
            LOG.warn("deleteCompositeChart failed: {}", re.toString());
            throw re;
        }
    }

    private ChartAggregate createChartAggregate(SecurityContext sc, Chart chart, Data data, String serviceAddress) {

        logAuthorizationInfo(sc);

        int chartId = chart.getChartId();
        String studentId = chart.getStudentId();
        ChartLabel chartLabel = chart.getChartLabel();
        ChartType chartType = chart.getChartType();
        ZonedDateTime createdAt = chart.getCreatedAt();
        ZonedDateTime lastUpdate = chart.getLastUpdate();

        DataSummery dataSummery = (data == null) ? null : new DataSummery(data.getDataId(), data.getStudentId(), data.getTranscripts(), data.getCreatedAt(), data.getLastUpdate());

        String chartAddress = chart.getServiceAddress();
        String dataAddress = data.getServiceAddress();
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, chartAddress, dataAddress);

        return new ChartAggregate(chartId, studentId, chartLabel, chartType, createdAt, lastUpdate, dataSummery, serviceAddresses);

    }

    private Mono<SecurityContext> getLogAuthorizationInfoMono() {
        return getSecurityContextMono().doOnNext(sc -> logAuthorizationInfo(sc));
    }

    private Mono<SecurityContext> getSecurityContextMono() {
        return ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSecCtx);
    }

    private void logAuthorizationInfo(SecurityContext sc) {
        if (sc != null && sc.getAuthentication() != null && sc.getAuthentication() instanceof JwtAuthenticationToken) {
            Jwt jwtToken = ((JwtAuthenticationToken)sc.getAuthentication()).getToken();
            logAuthorizationInfo(jwtToken);
        } else {
            LOG.warn("No JWT based Authentication supplied, running tests are we?");
        }
    }

    private void logAuthorizationInfo(Jwt jwt) {
        if (jwt == null) {
            LOG.warn("No JWT supplied, running tests are we?");
        } else {
            if (LOG.isDebugEnabled()) {
                URL issuer = jwt.getIssuer();
                List<String> audience = jwt.getAudience();
                Object subject = jwt.getClaims().get("sub");
                Object scopes = jwt.getClaims().get("scope");
                Object expires = jwt.getClaims().get("exp");

                LOG.debug("Authorization info: Subject: {}, scopes: {}, expires {}: issuer: {}, audience: {}", subject, scopes, expires, issuer, audience);
            }
        }
    }
}
