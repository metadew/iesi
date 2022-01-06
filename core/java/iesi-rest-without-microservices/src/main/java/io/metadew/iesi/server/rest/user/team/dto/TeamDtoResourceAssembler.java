package io.metadew.iesi.server.rest.user.team.dto;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.user.Privilege;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.server.rest.user.role.PrivilegeDto;
import io.metadew.iesi.server.rest.user.role.RoleDto;
import io.metadew.iesi.server.rest.user.role.RoleUserDto;
import io.metadew.iesi.server.rest.user.team.TeamDto;
import io.metadew.iesi.server.rest.user.team.TeamSecurityGroupDto;
import io.metadew.iesi.server.rest.user.team.TeamsController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TeamDtoResourceAssembler extends RepresentationModelAssemblerSupport<Team, TeamDto> {

    public TeamDtoResourceAssembler() {
        super(TeamsController.class, TeamDto.class);
    }

    @Override
    public TeamDto toModel(Team entity) {
        TeamDto teamDto = convertToDto(entity);
        Link selfLink = linkTo(methodOn(TeamsController.class).get(teamDto.getTeamName()))
                .withRel("team:" + teamDto.getId());
        teamDto.add(selfLink);
        return teamDto;
    }

    private TeamDto convertToDto(Team team) {
        return new TeamDto(
                team.getMetadataKey().getUuid(),
                team.getTeamName(),
                team.getSecurityGroups().stream().map(this::convertToDto).collect(Collectors.toSet()),
                team.getRoles().stream().map(this::convertToDto).collect(Collectors.toSet())
        );
    }

    private TeamSecurityGroupDto convertToDto(SecurityGroup securityGroup) {
        return new TeamSecurityGroupDto(
                securityGroup.getMetadataKey().getUuid(),
                securityGroup.getName()
        );
    }

    private RoleDto convertToDto(Role role) {
        return new RoleDto(
                role.getMetadataKey().getUuid(),
                role.getName(),
                role.getPrivileges().stream().map(this::convertToDto).collect(Collectors.toSet()),
                role.getUsers().stream().map(this::convertToDto).collect(Collectors.toSet())
        );
    }

    private PrivilegeDto convertToDto(Privilege privilege) {
        return new PrivilegeDto(
                privilege.getMetadataKey().getUuid(),
                privilege.getPrivilege()
        );
    }

    private RoleUserDto convertToDto(User user) {
        return new RoleUserDto(
                user.getMetadataKey().getUuid(),
                user.getUsername(),
                user.isEnabled(),
                user.isExpired(),
                user.isCredentialsExpired(),
                user.isLocked()
        );
    }

    public TeamDto toModel(TeamDto teamDto) {
        return teamDto;
    }


}
