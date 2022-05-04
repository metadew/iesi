package io.metadew.iesi.server.rest.configuration.security.providers;

import io.metadew.iesi.server.rest.user.ldap.LdapGroup;
import io.metadew.iesi.server.rest.user.ldap.LdapUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.support.LdapContextSource;

import javax.naming.Context;
import javax.naming.NamingException;

public class IesiLdapContextMappers {

    @Data
    @AllArgsConstructor
    public static class GroupMapper implements ContextMapper<LdapGroup> {
        private final String groupSearchAttribute;
        private final String baseLdap;

        @Override
        public LdapGroup mapFromContext(Object o) throws NamingException {
            DirContextAdapter context = (DirContextAdapter) o;
            return new LdapGroup(
                    context.getDn().toString().concat("," + baseLdap),
                    context.getStringAttribute(groupSearchAttribute)
            );
        }
    }

    @Data
    @AllArgsConstructor
    public static class UserMapper implements ContextMapper<LdapUser> {
        private String baseLdap;

        @Override
        public LdapUser mapFromContext(Object o) throws NamingException {
            DirContextAdapter context = (DirContextAdapter) o;
            return new LdapUser(
                    context.getDn().toString().concat("," + baseLdap)
            );
        }
    }

}
