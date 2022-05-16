package io.metadew.iesi.server.rest.configuration.security.providers.ldap;

import lombok.Data;

import java.util.List;

@Data
public class LdapGroupMapping {
    private String groupSearchBaseDn;
    private String groupSearchAttribute;
    private String groupMemberAttribute;
    private List<MappingPair> mappingPairs;
}
