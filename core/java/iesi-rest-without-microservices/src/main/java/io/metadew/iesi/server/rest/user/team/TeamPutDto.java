package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.server.rest.user.role.RoleDto;
import io.metadew.iesi.server.rest.user.role.RolePutDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class TeamPutDto {

    private UUID id;
    private String teamName;
    private Set<UUID> securityGroupIds;
    private Set<RolePutDto> roles;

}
