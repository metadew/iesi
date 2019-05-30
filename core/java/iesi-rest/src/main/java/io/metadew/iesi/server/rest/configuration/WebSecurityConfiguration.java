package io.metadew.iesi.server.rest.configuration;

import javax.sql.DataSource;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Order(2)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final DataSource dataSource;

	public WebSecurityConfiguration(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	@Bean
	public UserDetailsService userDetailsServiceBean() throws Exception {
		return super.userDetailsServiceBean();
	}

	@Bean
	public JdbcUserDetailsManager userDetailsManager(DataSource dataSource) {
		JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
		manager.setDataSource(dataSource);
		manager.getDataSource();
		return manager;
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> cfg = auth.jdbcAuthentication()
				.passwordEncoder(passwordEncoder()).dataSource(dataSource);
		cfg.getUserDetailsService().setEnableAuthorities(true);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatcher(EndpointRequest.toAnyEndpoint())
				.authorizeRequests()
				.anyRequest().hasAuthority("AUTHORIZED_ADMIN")
				.and()
				.httpBasic();
	}

}
