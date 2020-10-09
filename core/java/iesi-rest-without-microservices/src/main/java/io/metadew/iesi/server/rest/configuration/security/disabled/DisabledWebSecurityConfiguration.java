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

}
