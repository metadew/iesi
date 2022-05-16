package io.metadew.iesi.server.rest.configuration.security.providers.ldap;

import io.metadew.iesi.server.rest.user.ldap.LdapGroup;

import java.util.List;

public interface LdapUserGroupPopulator {
    public List<LdapGroup> populate(String userDn);
}
