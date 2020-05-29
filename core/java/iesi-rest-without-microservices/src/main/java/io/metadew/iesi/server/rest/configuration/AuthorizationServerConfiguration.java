package io.metadew.iesi.server.rest.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private DataSource dataSource;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    private TokenStore tokenStore;

    @Autowired
    public AuthorizationServerConfiguration(DataSource dataSource, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtAccessTokenConverter jwtAccessTokenConverter, TokenStore tokenStore) {
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenStore(tokenStore);
    }

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .passwordEncoder(passwordEncoder)
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

}
