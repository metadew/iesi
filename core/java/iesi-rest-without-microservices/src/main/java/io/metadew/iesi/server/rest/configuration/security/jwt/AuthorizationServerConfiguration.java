package io.metadew.iesi.server.rest.configuration.security.jwt;

import io.metadew.iesi.server.rest.configuration.security.IesiUserDetailsManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.*;
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
            DataSource dataSource
    ) {
        this.iesiUserDetailsManager = iesiUserDetailsManager;
        this.authenticationManager = authenticationManager;
        this.iesiUserAuthenticationConverter = iesiUserAuthenticationConverter;
        this.dataSource = dataSource;
    }

    @Bean
    public TokenStore tokenStore() {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
        return new JdbcTokenStore(dataSource) {
            @Override
            public void storeAccessToken(final OAuth2AccessToken token, final OAuth2Authentication authentication) {
                final String key = authenticationKeyGenerator.extractKey(authentication);
                jdbcTemplate.update("delete from oauth_access_token where authentication_id = ?", key);
                super.storeAccessToken(token, authentication);
            }
        };
    }

    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
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
                .tokenStore(tokenStore())
                .tokenServices(tokenServices())
                .userDetailsService(iesiUserDetailsManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer serverSecurity) {
        serverSecurity
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

}
