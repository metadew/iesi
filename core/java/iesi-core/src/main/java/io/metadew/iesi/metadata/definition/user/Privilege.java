package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Privilege extends Metadata<PrivilegeKey> {

    private String authority;
    private final RoleKey roleKey;

    @Builder
    public Privilege(PrivilegeKey privilegeKey, String authority, RoleKey roleKey) {
        super(privilegeKey);
        this.authority = authority;
        this.roleKey = roleKey;
    }

}