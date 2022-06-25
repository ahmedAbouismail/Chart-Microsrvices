package htw.berlin.springcloud.config;


import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

import htw.berlin.springcloud.jose.Jwks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@Import(OAuth2AuthorizationServerConfiguration.class)
public class AuthorizationServerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationServerConfig.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        return http.formLogin(Customizer.withDefaults()).build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        LOG.info("register OAUth client allowing all grant flows...");


   LOG.info("register OAUth client allowing all grant flows...");
           RegisteredClient writerClient = RegisteredClient.withId(UUID.randomUUID().toString())
           .clientId("writer")
           .clientSecret("secret")
           .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
           .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
           .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
           .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
           .redirectUri("http://127.0.0.1:8080/login/oauth2/code/backend-client-oidc")
           .redirectUri("https://my.redirect.uri")
           .redirectUri("https://localhost:8443/webjars/swagger-ui/oauth2-redirect.html")
           .scope(OidcScopes.OPENID)
           .scope("chart:read")
           .scope("chart:write")
           .clientSettings(clientSettings -> clientSettings.requireUserConsent(true))
           .tokenSettings(ts -> ts.accessTokenTimeToLive(Duration.ofHours(1)))
           .build();

           RegisteredClient readerClient = RegisteredClient.withId(UUID.randomUUID().toString())
           .clientId("reader")
           .clientSecret("secret")
           .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
           .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
           .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
           .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
           .redirectUri("https://my.redirect.uri")
           .redirectUri("https://localhost:8443/webjars/swagger-ui/oauth2-redirect.html")
           .scope(OidcScopes.OPENID)
           .scope("chart:read")
           .clientSettings(clientSettings -> clientSettings.requireUserConsent(true))
           .tokenSettings(ts -> ts.accessTokenTimeToLive(Duration.ofHours(1)))
           .build();
           return new InMemoryRegisteredClientRepository(writerClient, readerClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public ProviderSettings providerSettings() {
        return new ProviderSettings().issuer("http://auth-server:9999");
    }
}

