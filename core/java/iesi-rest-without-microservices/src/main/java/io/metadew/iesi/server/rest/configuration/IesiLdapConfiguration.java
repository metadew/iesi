package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.server.rest.configuration.security.providers.ldap.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Data
@Configuration
@PropertySource(value = "classpath:application-ldap.yml", factory = YamlPropertySourceFactory.class)
public class IesiLdapConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "server")
    public LdapServer ldapServer() {
        return new LdapServer();
    }

    @Bean
    @ConfigurationProperties(prefix = "authentication")
    public LdapAuthentication ldapAuthentication() {
        return new LdapAuthentication();
    }

    @Bean
    @ConfigurationProperties(prefix = "groups-mapping")
    public LdapGroupMapping ldapGroupMapping() {
        return new LdapGroupMapping();
    }

    @Bean
    @ConfigurationProperties(prefix = "roles-mapping")
    public LdapRoleMapping ldapRoleMapping() { return new LdapRoleMapping(); }

    @Bean
    public LdapContextSource contextSource(LdapServer ldapServer, LdapAuthentication ldapAuthentication) {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapServer.getUrl());
        contextSource.setBase(ldapServer.getBase());
        contextSource.setUserDn(ldapAuthentication.getAdmin().getDn());
        contextSource.setPassword(ldapAuthentication.getAdmin().getPassword());
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(LdapServer ldapServer, LdapAuthentication ldapAuthentication) {
        return new LdapTemplate(contextSource(ldapServer, ldapAuthentication));
    }

}
