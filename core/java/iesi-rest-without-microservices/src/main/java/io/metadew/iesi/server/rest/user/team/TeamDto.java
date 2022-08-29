package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.server.rest.user.role.RoleDto;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Relation(value = "team", collectionRelation = "teams")
public class TeamDto extends RepresentationModel<TeamDto> {

    private UUID id;
    private String teamName;
    private Set<TeamSecurityGroupDto> securityGroups;
    private Set<RoleDto> roles;

}
