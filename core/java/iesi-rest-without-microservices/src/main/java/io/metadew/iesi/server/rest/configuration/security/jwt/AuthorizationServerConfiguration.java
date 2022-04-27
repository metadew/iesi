package io.metadew.iesi.server.rest.configuration.security.jwt;

import io.metadew.iesi.server.rest.configuration.security.IesiUserDetailsManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final IesiUserDetailsManager iesiUserDetailsManager;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder bcryptPasswordEncoder;
    private final IesiUserAuthenticationConverter iesiUserAuthenticationConverter;


    public AuthorizationServerConfiguration(
            IesiUserDetailsManager iesiUserDetailsManager,
            AuthenticationManager authenticationManager,
            PasswordEncoder bcryptPasswordEncoder,
            IesiUserAuthenticationConverter iesiUserAuthenticationConverter
    ) {
        this.iesiUserDetailsManager = iesiUserDetailsManager;
        this.authenticationManager = authenticationManager;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
        this.iesiUserAuthenticationConverter = iesiUserAuthenticationConverter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        defaultTokenServices.setReuseRefreshToken(false);
        defaultTokenServices.setAccessTokenValiditySeconds(5);
        defaultTokenServices.setRefreshTokenValiditySeconds(24000);
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
        clients
                .inMemory()
                .withClient("iesi").secret(bcryptPasswordEncoder.encode("iesi"))
                .authorizedGrantTypes("password", "refresh_token")
                .authorities("CLIENT")
                .scopes("read-write")
                .resourceIds("oauth2-resource")
                .redirectUris("http://localhost:8081/login")
                .accessTokenValiditySeconds(5)
                .refreshTokenValiditySeconds(24000);
    }

}
