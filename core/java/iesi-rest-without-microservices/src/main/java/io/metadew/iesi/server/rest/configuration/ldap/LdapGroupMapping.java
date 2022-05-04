package io.metadew.iesi.server.rest.configuration.ldap;

import lombok.Data;

import java.util.HashSet;

@Data
public class LdapGroupMapping {
    private String groupSearchBaseDn;
    private String groupSearchAttribute;
    private String groupMemberAttribute;
    private HashSet<MappingPair> mappingPairs;


    @Data
    public static class MappingPair {
        private String iesiName;
        private String adName;
    }
}
