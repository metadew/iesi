package io.metadew.iesi.server.rest.user.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

    private UUID id;
    private String name;
    private Set<PrivilegeDto> privileges;
    private Set<RoleUserDto> users;

}
