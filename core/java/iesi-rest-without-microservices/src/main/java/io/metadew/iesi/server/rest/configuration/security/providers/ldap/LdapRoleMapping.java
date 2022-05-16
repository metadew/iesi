package io.metadew.iesi.server.rest.configuration.security.providers.ldap;

import lombok.Data;

import java.util.List;

@Data
public class LdapRoleMapping {
    private List<MappingPair> mappingPairs;
}
