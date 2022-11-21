package io.metadew.iesi.server.rest.configuration.security.jwt;

import io.metadew.iesi.server.rest.configuration.security.IesiUserDetailsManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final IesiUserDetailsManager iesiUserDetailsManager;
    private final AuthenticationManager authenticationManager;
    private final IesiUserAuthenticationConverter iesiUserAuthenticationConverter;
    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    @Value("${iesi.security.jwt.access-token-validity}")
    private int accessTokenValidityInSeconds;
    @Value("${iesi.security.jwt.refresh-token-validity}")
    private int refreshTokenValidityInSeconds;
    @Value("${iesi.security.client_id}")
    private String clientId;
    @Value("${iesi.security.client_secret}")
    private String clientSecret;


    public AuthorizationServerConfiguration(
            IesiUserDetailsManager iesiUserDetailsManager,
            AuthenticationManager authenticationManager,
            IesiUserAuthenticationConverter iesiUserAuthenticationConverter,
            DataSource dataSource,
            PasswordEncoder passwordEncoder,
            Environment environment) {
        this.iesiUserDetailsManager = iesiUserDetailsManager;
        this.authenticationManager = authenticationManager;
        this.iesiUserAuthenticationConverter = iesiUserAuthenticationConverter;
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Bean("tokenStore")
    @Profile("!test & !sqlite")
    public TokenStore jdbcTokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean("tokenStore")
    @Profile({ "test", "sqlite"})
    public TokenStore inMemoryTokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        if (environment.acceptsProfiles(Profiles.of("test", "sqlite"))) {
            defaultTokenServices.setTokenStore(inMemoryTokenStore());
        } else {
            defaultTokenServices.setTokenStore(jdbcTokenStore());
        }



        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        defaultTokenServices.setReuseRefreshToken(false);
        defaultTokenServices.setAccessTokenValiditySeconds(accessTokenValidityInSeconds);
        defaultTokenServices.setRefreshTokenValiditySeconds(refreshTokenValidityInSeconds);
        return defaultTokenServices;
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAuthenticationConverter = new JwtAccessTokenConverter();
        ((DefaultAccessTokenConverter) jwtAuthenticationConverter.getAccessTokenConverter()).setUserTokenConverter(iesiUserAuthenticationConverter);
        return jwtAuthenticationConverter;
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .accessTokenConverter(accessTokenConverter())
                .authenticationManager(authenticationManager)
                .tokenServices(tokenServices())
                .userDetailsService(iesiUserDetailsManager);

        if (environment.acceptsProfiles(Profiles.of("test", "sqlite"))) {
            endpoints.tokenStore(inMemoryTokenStore());
        } else {
            endpoints.tokenStore(jdbcTokenStore());
        }
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer serverSecurity) {
        serverSecurity
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        if (environment.acceptsProfiles(Profiles.of("test", "sqlite"))) {
            clients.inMemory()
                    .withClient("iesi").secret(passwordEncoder.encode("iesi"))
                    .accessTokenValiditySeconds(accessTokenValidityInSeconds)
                    .refreshTokenValiditySeconds(refreshTokenValidityInSeconds)
                    .authorizedGrantTypes("password","refresh_token")
                    .scopes("write-read")
                    .autoApprove(true);
        } else {
            clients.jdbc(dataSource);
        }
    }

}
