package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.user.RoleConfiguration;
import io.metadew.iesi.metadata.configuration.user.TeamConfiguration;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.server.rest.user.role.PrivilegeDto;
import io.metadew.iesi.server.rest.user.role.RoleTeamDto;
import io.metadew.iesi.server.rest.user.team.TeamSecurityGroupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@ConditionalOnWebApplication
public class UserDtoModelAssembler extends RepresentationModelAssemblerSupport<User, UserDto> {


    @Autowired
    public UserDtoModelAssembler() {
        super(UserController.class, UserDto.class);
    }

    @Override
    public UserDto toModel(User user) {
        return new UserDto(
                user.getMetadataKey().getUuid(),
                user.getUsername(),
                user.isEnabled(),
                user.isExpired(),
                user.isCredentialsExpired(),
                user.isLocked(),
                user.getRoleKeys().stream()
                        .map(roleKey -> {
                            Role role = RoleConfiguration.getInstance().get(roleKey)
                                    .orElseThrow(() -> new MetadataDoesNotExistException(roleKey));
                            Team team = TeamConfiguration.getInstance().get(role.getTeamKey())
                                    .orElseThrow(() -> new MetadataDoesNotExistException(role.getTeamKey()));
                            return new UserRoleDto(
                                    roleKey.getUuid(),
                                    role.getName(),
                                    new RoleTeamDto(
                                            team.getMetadataKey().getUuid(),
                                            team.getTeamName(),
                                            team.getSecurityGroups().stream()
                                                    .map(securityGroup -> new TeamSecurityGroupDto(
                                                            securityGroup.getMetadataKey().getUuid(),
                                                            securityGroup.getName()
                                                    )).collect(Collectors.toSet())

                                    ),
                                    role.getPrivileges().stream()
                                            .map(privilege -> new PrivilegeDto(
                                                    privilege.getMetadataKey().getUuid(),
                                                    privilege.getPrivilege()
                                            )).collect(Collectors.toSet())
                            );
                        }).collect(Collectors.toSet())

        );
    }

    public UserDto toModel(UserDto userDto) {
        return userDto;
    }

}