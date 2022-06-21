package htw.berlin.microservices.composite.chart;

import static org.springframework.http.HttpMethod.*;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange()
                .pathMatchers("/openapi/**").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers(POST, "/chart-composite/**").hasAuthority("SCOPE_chart:write")
                .pathMatchers(PATCH, "/chart-composite/**").hasAuthority("SCOPE_chart:write")
                .pathMatchers(DELETE, "/chart-composite/**").hasAuthority("SCOPE_chart:write")
                .pathMatchers(GET, "/chart-composite/**").hasAuthority("SCOPE_chart:read")
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }
}