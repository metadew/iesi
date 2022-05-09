package io.metadew.iesi.server.rest.configuration.security.providers.ldap;

import lombok.Data;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class LdapGroupMapping {
    private String groupSearchBaseDn;
    private String groupSearchAttribute;
    private String groupMemberAttribute;
    private List<MappingPair> mappingPairs;


    @Data
    public static class MappingPair {
        private String iesiName;
        private String adName;
    }
}
