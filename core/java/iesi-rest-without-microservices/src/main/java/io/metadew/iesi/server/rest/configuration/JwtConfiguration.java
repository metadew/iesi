package io.metadew.iesi.server.rest.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

@Configuration
@Order(0)
public class JwtConfiguration {

    private Logger log = LoggerFactory.getLogger(AuthorizationServerConfiguration.class);

    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    public DefaultTokenServices tokenServices(TokenStore tokenStore,
                                              ClientDetailsService clientDetailsService,
                                              AuthenticationManager authenticationManager) {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStore);
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setAuthenticationManager(authenticationManager);
        return tokenServices;
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(SecurityPropertiesServer securityProperties) {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(securityProperties.getKeyStore(), securityProperties.getKeyStorePassword().toCharArray());
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(securityProperties.getKeyPairAlias(), securityProperties.getKeyPairPassword().toCharArray());
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair);
        return jwtAccessTokenConverter;
    }

}
