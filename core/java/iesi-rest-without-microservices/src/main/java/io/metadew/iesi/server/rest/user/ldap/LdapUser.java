package io.metadew.iesi.server.rest.user.ldap;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LdapUser {
    public final String dn;
    public List<LdapGroup> groups;

    public LdapUser(String dn) {
        this.dn = dn;
    }

    @Override
    public String toString() {
        return "${dn:" + dn + "}" + "${groups:" + groups + "}";
    }
}
