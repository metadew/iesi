package io.metadew.iesi.server.rest.configuration.security.providers.ldap;

import lombok.Data;

@Data
public class LdapServer {
    private String url;
    private String base;
}
