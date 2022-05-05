package io.metadew.iesi.server.rest.user.ldap;

import io.metadew.iesi.server.rest.configuration.security.providers.ldap.LdapUserGroupPopulator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LdapUser {
    public final String dn;
    public List<LdapGroup> groups;

    public LdapUser(String dn, LdapUserGroupPopulator ldapUserGroupPopulator) {
        this.dn = dn;
        this.groups = ldapUserGroupPopulator.populate(dn);
    }

    @Override
    public String toString() {
        return "${dn:" + dn + "}" + "${groups:" + groups + "}";
    }
}
