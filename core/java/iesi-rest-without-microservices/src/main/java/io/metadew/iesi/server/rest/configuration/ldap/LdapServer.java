package io.metadew.iesi.server.rest.configuration.ldap;

import lombok.Data;

@Data
public class LdapServer {
    private String url;
    private String base;
}
