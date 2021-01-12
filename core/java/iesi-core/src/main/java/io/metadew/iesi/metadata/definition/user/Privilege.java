package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Privilege extends Metadata<PrivilegeKey> {

    private String privilege;
    private final RoleKey roleKey;

    @Builder
    public Privilege(PrivilegeKey privilegeKey, String privilege, RoleKey roleKey) {
        super(privilegeKey);
        this.privilege = privilege;
        this.roleKey = roleKey;
    }

}