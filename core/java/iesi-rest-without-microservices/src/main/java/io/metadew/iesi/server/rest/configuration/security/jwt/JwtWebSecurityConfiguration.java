package io.metadew.iesi.server.rest.configuration.security.jwt;

import io.metadew.iesi.server.rest.configuration.security.IESIRole;
import io.metadew.iesi.server.rest.user.CustomUserDetailsManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@Profile("security")
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class JwtWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private CustomUserDetailsManager customUserDetailsManager;
    private PasswordEncoder passwordEncoder;
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public void setJwtAuthenticationFilter(JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Autowired
    public void setCustomUserDetailsManager(CustomUserDetailsManager customUserDetailsManager) {
        this.customUserDetailsManager = customUserDetailsManager;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(customUserDetailsManager)
                .passwordEncoder(passwordEncoder);
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
        log.info("IESI REST endpoint security enabled");
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .mvcMatchers("/actuator/health").permitAll()
//                // Action Types
//                .mvcMatchers(HttpMethod.GET, "/action-types/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label, IESIRole.TEST_ENGINEER.label, IESIRole.EXECUTOR.label, IESIRole.VIEWER.label)
//                // Connection Types
//                .mvcMatchers(HttpMethod.GET, "/connection-types/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label, IESIRole.TEST_ENGINEER.label, IESIRole.EXECUTOR.label, IESIRole.VIEWER.label)
//                // Components
//                .mvcMatchers(HttpMethod.GET, "/components/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label, IESIRole.TEST_ENGINEER.label, IESIRole.EXECUTOR.label, IESIRole.VIEWER.label)
//                .mvcMatchers(HttpMethod.POST, "/components").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TEST_ENGINEER.label)
//                .mvcMatchers(HttpMethod.PUT, "/components/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TEST_ENGINEER.label)
//                .mvcMatchers(HttpMethod.DELETE, "/components").hasAnyRole(IESIRole.ADMIN.label)
//                .mvcMatchers(HttpMethod.DELETE, "/components/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TEST_ENGINEER.label)
//                // Scripts
//                .mvcMatchers(HttpMethod.GET, "/scripts/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label, IESIRole.TEST_ENGINEER.label, IESIRole.EXECUTOR.label, IESIRole.VIEWER.label)
//                .mvcMatchers(HttpMethod.POST, "/scripts").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TEST_ENGINEER.label)
//                .mvcMatchers(HttpMethod.PUT, "/scripts/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TEST_ENGINEER.label)
//                //.mvcMatchers(HttpMethod.DELETE, "/scripts").hasAnyRole(IESIRole.ADMIN.label)
//                .mvcMatchers(HttpMethod.DELETE, "/scripts/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TEST_ENGINEER.label)
//                // Connections
//                .mvcMatchers(HttpMethod.GET, "/connections/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label, IESIRole.TEST_ENGINEER.label, IESIRole.EXECUTOR.label, IESIRole.VIEWER.label)
//                .mvcMatchers(HttpMethod.POST, "/connections").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label)
//                .mvcMatchers(HttpMethod.PUT, "/connections/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label)
//                .mvcMatchers(HttpMethod.DELETE, "/connections").hasAnyRole(IESIRole.ADMIN.label)
//                .mvcMatchers(HttpMethod.DELETE, "/connections/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label)
//                // Environments
//                .mvcMatchers(HttpMethod.GET, "/environments/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label, IESIRole.TEST_ENGINEER.label, IESIRole.EXECUTOR.label, IESIRole.VIEWER.label)
//                .mvcMatchers(HttpMethod.POST, "/environments").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label)
//                .mvcMatchers(HttpMethod.PUT, "/environments/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label)
//                .mvcMatchers(HttpMethod.DELETE, "/environments").hasAnyRole(IESIRole.ADMIN.label)
//                .mvcMatchers(HttpMethod.DELETE, "/environments/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label)
//                // Execution Requests
//                .mvcMatchers(HttpMethod.GET, "/execution-requests/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label, IESIRole.TEST_ENGINEER.label, IESIRole.EXECUTOR.label, IESIRole.VIEWER.label)
//                .mvcMatchers(HttpMethod.POST, "/execution-requests").hasAnyRole(IESIRole.ADMIN.label, IESIRole.EXECUTOR.label, IESIRole.TEST_ENGINEER.label)
//                .mvcMatchers(HttpMethod.PUT, "/execution-requests/**").hasAnyRole(IESIRole.ADMIN.label)
//                .mvcMatchers(HttpMethod.DELETE, "/execution-requests/**").hasAnyRole(IESIRole.ADMIN.label)
//                // Script Executions
//                .mvcMatchers(HttpMethod.GET, "/script-executions/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label, IESIRole.TEST_ENGINEER.label, IESIRole.EXECUTOR.label, IESIRole.VIEWER.label)
//                //.mvcMatchers(HttpMethod.POST, "/script-executions").hasAnyRole(IESIRole.ADMIN.label, IESIRole.EXECUTOR.label, IESIRole.TEST_ENGINEER.label)
//                //.mvcMatchers(HttpMethod.PUT, "/script-executions/**").hasAnyRole(IESIRole.ADMIN.label)
//                //.mvcMatchers(HttpMethod.DELETE, "/script-executions/**").hasAnyRole(IESIRole.ADMIN.label)
//                // Users
//                .mvcMatchers(HttpMethod.POST, "/users/login").permitAll()
//                .mvcMatchers(HttpMethod.POST, "/users/").hasAnyRole(IESIRole.ADMIN.label)
//                .mvcMatchers(HttpMethod.GET, "/users/**").hasAnyRole(IESIRole.ADMIN.label, IESIRole.TECHNICAL_ENGINEER.label, IESIRole.TEST_ENGINEER.label, IESIRole.EXECUTOR.label, IESIRole.VIEWER.label)

//                .anyRequest().hasAnyRole(IESIRole.ADMIN.label)
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

}
