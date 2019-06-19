package iesi.cloud;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
    @Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
}
	@Autowired
	private DataSource dataSource;

	@Autowired

	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {

		auth.jdbcAuthentication().dataSource(dataSource);

	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		http.headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN));
//		http.csrf().disable().httpBasic().and().authorizeRequests()
//				.antMatchers("/**/**").permitAll();
		http.csrf().disable().httpBasic().and().authorizeRequests()
				.antMatchers("/encrypt/**").authenticated()
				.antMatchers("/decrypt/**").authenticated();
		http.authorizeRequests()
				.antMatchers("/iesi/**").denyAll()
				.antMatchers("/actuator/**" ).denyAll()
				.antMatchers("/h2-console/**").denyAll();

	}

}