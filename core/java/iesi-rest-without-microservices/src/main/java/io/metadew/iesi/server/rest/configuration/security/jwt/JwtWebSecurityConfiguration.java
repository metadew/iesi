package io.metadew.iesi.server.rest.configuration.security.jwt;

<<<<<<< HEAD
import io.metadew.iesi.server.rest.user.CustomUserDetailsManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
=======
import io.metadew.iesi.server.rest.configuration.security.IesiUserDetailsManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
>>>>>>> master
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
<<<<<<< HEAD
@Profile("security")
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class JwtWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private CustomUserDetailsManager customUserDetailsManager;
    private PasswordEncoder passwordEncoder;
    private JWTAuthenticationFilter jwtAuthenticationFilter;
=======
// @Profile("security")
@Log4j2
public class JwtWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private IesiUserDetailsManager iesiUserDetailsManager;
    private PasswordEncoder passwordEncoder;
    private JWTAuthenticationFilter jwtAuthenticationFilter;
    @Value("${iesi.security.enabled:false}")
    private boolean enableSecurity;
>>>>>>> master

    @Autowired
    public void setJwtAuthenticationFilter(JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Autowired
<<<<<<< HEAD
    public void setCustomUserDetailsManager(CustomUserDetailsManager customUserDetailsManager) {
        this.customUserDetailsManager = customUserDetailsManager;
=======
    public void setCustomUserDetailsManager(IesiUserDetailsManager iesiUserDetailsManager) {
        this.iesiUserDetailsManager = iesiUserDetailsManager;
>>>>>>> master
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
<<<<<<< HEAD
                .userDetailsService(customUserDetailsManager)
=======
                .userDetailsService(iesiUserDetailsManager)
>>>>>>> master
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
<<<<<<< HEAD
        log.info("IESI REST endpoint security enabled");
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .mvcMatchers("/users/login").permitAll()
                .mvcMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
=======
        if (enableSecurity) {
            log.info("IESI REST endpoint security enabled");
            http
                    .cors().and()
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .mvcMatchers("/actuator/health").permitAll()
                    .mvcMatchers("/users/login").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
        } else {
            log.info("IESI REST endpoint security disabled");
            http
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .cors().and()
                    .csrf().disable()
                    .authorizeRequests().anyRequest().permitAll();
        }
>>>>>>> master
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
<<<<<<< HEAD
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
=======
        CorsConfiguration corsConfiguration = new CorsConfiguration(); //.applyPermitDefaultValues();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setMaxAge(1800L);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod(HttpMethod.GET);
        corsConfiguration.addAllowedMethod(HttpMethod.HEAD);
        corsConfiguration.addAllowedMethod(HttpMethod.POST);
        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
        source.registerCorsConfiguration("/**", corsConfiguration);
>>>>>>> master
        return source;
    }

}
