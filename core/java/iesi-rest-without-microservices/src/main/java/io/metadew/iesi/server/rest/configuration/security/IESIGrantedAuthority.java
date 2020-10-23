package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.user.Privilege;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
public class IESIGrantedAuthority implements GrantedAuthority {

    private final String securityGroupName;
    private final String privilegeName;

    public IESIGrantedAuthority(SecurityGroup securityGroup, Privilege privilege) {
        this.securityGroupName = securityGroup.getName();
        this.privilegeName = privilege.getPrivilege();
    }

    public IESIGrantedAuthority(String securityGroupName, String privilegeName) {
        this.securityGroupName = securityGroupName;
        this.privilegeName = privilegeName;
    }

    @Override
    public String getAuthority() {
        return securityGroupName + "_" + privilegeName;
    }
}
