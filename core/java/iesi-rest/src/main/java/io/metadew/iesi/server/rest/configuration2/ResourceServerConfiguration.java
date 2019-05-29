//package io.metadew.iesi.server.rest.configuration2;
//
//import static java.nio.charset.StandardCharsets.UTF_8;
//
//import java.io.IOException;
//
//import org.apache.commons.io.IOUtils;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
//import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
//import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;
//
//@Configuration
//@EnableResourceServer
//@EnableConfigurationProperties(SecurityProperties.class)
//public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
//
//	private final SecurityProperties securityProperties;
//
//	private TokenStore tokenStore;
//
//	public ResourceServerConfiguration(final SecurityProperties securityProperties) {
//		this.securityProperties = securityProperties;
//	}
//
//	@Override
//	public void configure(final ResourceServerSecurityConfigurer resources) {
//		resources.tokenStore(tokenStore());
//	}
//
//	private static final String[] AUTH_WHITELIST = { "/v2/api-docs", "/swagger-resources", "/swagger-resources/**",
//	"/swagger-ui.html", "/webjars/**", "/h2-console/**" };
//
//@Override
//public void configure(HttpSecurity http) throws Exception {
//http.headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN));
//
//http.cors().and().csrf().disable().exceptionHandling().and().sessionManagement()
//		.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
//		.antMatchers(AUTH_WHITELIST).permitAll().antMatchers("/actuator/health").permitAll()
//		.antMatchers("/", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg", "/**/*.html",
//				"/**/*.css", "/**/*.js")
//		.permitAll().antMatchers("/**").authenticated().anyRequest().authenticated();
//}
//
//	@Primary
//	@Bean
//	public DefaultTokenServices tokenServices(final TokenStore tokenStore) {
//		DefaultTokenServices tokenServices = new DefaultTokenServices();
//		tokenServices.setTokenStore(tokenStore);
//		return tokenServices;
//	}
//
//	@Bean
//	public TokenStore tokenStore() {
//		if (tokenStore == null) {
//			tokenStore = new JwtTokenStore(jwtAccessTokenConverter());
//		}
//		return tokenStore;
//	}
//
//	@Bean
//	public JwtAccessTokenConverter jwtAccessTokenConverter() {
//		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//		converter.setVerifierKey(getPublicKeyAsString());
//		return converter;
//	}
//
//	private String getPublicKeyAsString() {
//		try {
//			return IOUtils.toString(securityProperties.getJwt().getPublicKey().getInputStream(), UTF_8);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//}
