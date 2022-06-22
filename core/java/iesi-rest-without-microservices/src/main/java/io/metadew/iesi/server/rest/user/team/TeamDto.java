package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.server.rest.user.role.RoleDto;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDto {

    private UUID id;
    private String teamName;
    private Set<TeamSecurityGroupDto> securityGroups;
    private Set<RoleDto> roles;

}
