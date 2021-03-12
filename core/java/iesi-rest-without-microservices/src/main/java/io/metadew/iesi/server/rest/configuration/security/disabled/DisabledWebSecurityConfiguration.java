<<<<<<< HEAD
package io.metadew.iesi.server.rest.configuration.security.disabled;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
@Profile("!security")
@EnableAutoConfiguration(exclude = {
        //SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class})
@Log4j2
public class DisabledWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        log.info("IESI REST endpoint security disabled");
        http
                .cors().and()
                .csrf().disable()
                .authorizeRequests().anyRequest().permitAll();
    }

    //    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        log.info("set CORS bean");
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
//        return source;
//    }

}
=======
//package io.metadew.iesi.server.rest.configuration.security.disabled;
//
//import lombok.extern.log4j.Log4j2;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.Collections;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@EnableWebSecurity
//@Configuration
//@Profile("!security")
//@EnableAutoConfiguration(exclude = {UserDetailsServiceAutoConfiguration.class})
//@Log4j2
//public class DisabledWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().mvcMatchers("/**");
//    }
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        log.info("IESI REST endpoint security disabled");
//        http
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .cors().and()
//                .csrf().disable()
//                .authorizeRequests().anyRequest().permitAll();
//    }
//
//    @Bean
//    public CorsFilter corsFilter() {
//            log.info("setting CORS configuration");
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        final CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Stream.of("*").collect(Collectors.toList()));
//        configuration.setAllowedMethods(Stream.of(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.OPTIONS.name(), HttpMethod.DELETE.name())
//                .collect(Collectors.toList()));
//        configuration.setAllowedHeaders(Collections.singletonList("*"));
//        configuration.setAllowCredentials(true);
//        source.registerCorsConfiguration("/**", configuration);
//        return new CorsFilter(source);
//    }
//
//}
>>>>>>> master
