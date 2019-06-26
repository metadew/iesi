package io.metadew.iesi.server.rest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Profile({"HTTPwithoutOauth", "HTTPSwithoutOauth"})
public class WebSecurityConfigurationWithoutOauth extends WebSecurityConfigurerAdapter {


    @Bean
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests().anyRequest().permitAll().and().httpBasic();

    }
      @Bean
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
           .ignoring()
               .antMatchers("/**");
    }

}
