package io.metadew.iesi.server.rest.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IesiSecurityChecker {
    @Value("${iesi.security.enabled:false}")
    private boolean enableSecurity;

    public boolean hasPrivilege(Authentication authentication, String privilege) {
        return new IesiMethodSecurityExpressionRoot(authentication, enableSecurity)
                .hasPrivilege(privilege);
    }


    public boolean hasPrivileges(Authentication authentication, List<String> privileges) {
        return new IesiMethodSecurityExpressionRoot(authentication, enableSecurity)
                .hasPrivileges(privileges);
    }

    public boolean hasPrivilege(Authentication authentication, String privilege, String securityGroup) {
        return new IesiMethodSecurityExpressionRoot(authentication, enableSecurity)
                .hasPrivilege(privilege, securityGroup);
    }

    public boolean hasPrivilege(Authentication authentication, String privilege, List<String> securityGroups) {
        return new IesiMethodSecurityExpressionRoot(authentication, enableSecurity)
                .hasPrivilege(privilege, securityGroups);
    }

    public boolean isMember(Authentication authentication, String securityGroup) {
        return new IesiMethodSecurityExpressionRoot(authentication, enableSecurity)
                .isMember(securityGroup);
    }

    public boolean hasPrivilege(Authentication authentication, Object securedObject, String privilege) {
        return new IesiMethodSecurityExpressionRoot(authentication, enableSecurity)
                .hasPrivilege(authentication, securedObject, privilege);
    }

}
