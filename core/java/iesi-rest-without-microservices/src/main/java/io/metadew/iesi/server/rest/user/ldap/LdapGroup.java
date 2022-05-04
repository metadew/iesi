package io.metadew.iesi.server.rest.user.ldap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LdapGroup {
    private final String dn;
    private final String cn;

    @Override
    public String toString() {
        return "${dn:" + dn + "}" + "${cn:" + cn + "}";
    }
}
