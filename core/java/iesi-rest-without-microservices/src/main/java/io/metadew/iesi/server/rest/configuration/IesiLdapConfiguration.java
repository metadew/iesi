package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.server.rest.configuration.ldap.LdapAuthentication;
import io.metadew.iesi.server.rest.configuration.ldap.LdapGroupMapping;
import io.metadew.iesi.server.rest.configuration.ldap.LdapServer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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


}
