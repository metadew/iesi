package io.metadew.iesi.server.rest.configuration.security.providers.ldap;

import lombok.Data;

@Data
public class LdapAuthentication {
    private LdapUsers users;
    private LdapAdmin admin;

    @Data
    public static class LdapUsers {
        private String userAuthenticateAttribute;
        private String userSearchAttribute;
        private String userSearchBaseDn;
    }

    @Data
    public static class LdapAdmin {
        private String dn;
        private String password;
    }
}
