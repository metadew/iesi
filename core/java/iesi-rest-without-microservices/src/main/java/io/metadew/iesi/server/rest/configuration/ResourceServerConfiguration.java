package io.metadew.iesi.server.rest.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@Profile("oauth2")
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	@Autowired
	private TokenStore tokenStore;

//	public ResourceServerConfiguration(TokenStore tokenStore) {
//		super();
//		this.tokenStore = tokenStore;
//	}

	@Override
	public void configure(final ResourceServerSecurityConfigurer resources) {
		resources.tokenStore(tokenStore);
	}

////	"/**/**" 
	private static final String[] AUTH_WHITELIST = {};

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.cors().and()
				.csrf().disable().exceptionHandling().and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests().mvcMatchers(AUTH_WHITELIST).permitAll().anyRequest().authenticated();
	}

//	public JwtAccessTokenConverter jwtAccessTokenConverter() {
//		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//		converter.setVerifierKey(getPublicKeyAsString());
//		return converter;
//	}
//
//	private String getPublicKeyAsString() {
//		try {
//			return IOUtils.toString(securityProperties.getPublicKey().getInputStream(), UTF_8);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
}