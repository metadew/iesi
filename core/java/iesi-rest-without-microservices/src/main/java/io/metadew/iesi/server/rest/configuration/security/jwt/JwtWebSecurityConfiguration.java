package io.metadew.iesi.server.rest.configuration.security.jwt;

import io.metadew.iesi.server.rest.configuration.security.IesiUserDetailsManager;
import io.metadew.iesi.server.rest.configuration.security.providers.IesiProviderManager;
import io.metadew.iesi.server.rest.configuration.security.providers.ldap.LdapAuthenticationProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
// @Profile("security")
@Log4j2
@ConditionalOnWebApplication
public class JwtWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final IesiUserDetailsManager iesiUserDetailsManager;
    private final LdapAuthenticationProvider ldapAuthenticationProvider;

    @Autowired
    public JwtWebSecurityConfiguration(IesiUserDetailsManager iesiUserDetailsManager, LdapAuthenticationProvider ldapAuthenticationProvider) {
        this.iesiUserDetailsManager = iesiUserDetailsManager;
        this.ldapAuthenticationProvider = ldapAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        daoAuthenticationProvider.setUserDetailsService(iesiUserDetailsManager);
        daoAuthenticationProvider.setPasswordEncoder(bcryptPasswordEncoder());

        return new IesiProviderManager(Arrays.asList(daoAuthenticationProvider, ldapAuthenticationProvider));
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .mvcMatchers("/oauth/authorize", "/oauth/token", "/error**").permitAll()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();
    }
}
