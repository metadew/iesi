package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.metadata.service.user.IESIPrivilege;

import java.util.Arrays;

public class SecurityUtils {

    public static String[] getAllPrivilegesExcept(String group, String excludedPrivilege) {
        return Arrays.stream(IESIPrivilege.values())
                .filter(iesiPrivilege -> !iesiPrivilege.getPrivilege().equals(excludedPrivilege))
                .map(iesiPrivilege -> iesiPrivilege.getPrivilege() + "@" + group)
                .toArray(String[]::new);
    }

}
