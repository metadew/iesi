package iesi.gateway.security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
@Configuration
@Order(2)
public class WebSecurity extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		http.headers().httpStrictTransportSecurity().disable().and().httpBasic()
		.and().formLogin().and().authorizeRequests().anyRequest().authenticated();
		http.requestMatcher(EndpointRequest.toAnyEndpoint())

				.authorizeRequests()

				.anyRequest().denyAll().and()

				.httpBasic();
	}

}
