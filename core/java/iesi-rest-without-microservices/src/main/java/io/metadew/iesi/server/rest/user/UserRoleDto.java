package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.server.rest.user.role.PrivilegeDto;
import io.metadew.iesi.server.rest.user.role.RoleTeamDto;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleDto {

    private UUID id;
    private String name;
    private RoleTeamDto team;
    private Set<PrivilegeDto> privileges;

}
