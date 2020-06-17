//package io.metadew.iesi.server.rest.configuration.security.oauth;
//
//import io.metadew.iesi.server.rest.configuration.security.jwt.CustomUserDetailsManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//@Profile("oauth")
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
//public class OAuthWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//    private PasswordEncoder passwordEncoder;
//    private final CustomUserDetailsManager customUserDetailsManager;
//
//    public OAuthWebSecurityConfiguration(CustomUserDetailsManager customUserDetailsManager, PasswordEncoder passwordEncoder) {
//        this.customUserDetailsManager = customUserDetailsManager;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder);
//        provider.setUserDetailsService(customUserDetailsManager);
//        return provider;
//    }
//
//
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .formLogin().permitAll()
//                .and()
//                .authorizeRequests()
//                .mvcMatchers("/api/oauth/**").permitAll()
//                .mvcMatchers("/api/**").authenticated()
//                .anyRequest().authenticated();
//        //.and().exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint).accessDeniedHandler(new AccessDeniedHandler());
//    }
//
//}