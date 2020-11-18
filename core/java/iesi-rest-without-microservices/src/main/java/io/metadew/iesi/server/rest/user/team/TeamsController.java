package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.*;
import io.metadew.iesi.metadata.service.user.IESIRole;
import io.metadew.iesi.metadata.service.user.RoleService;
import io.metadew.iesi.metadata.service.user.TeamService;
import io.metadew.iesi.server.rest.user.role.RolePostDto;
import io.metadew.iesi.server.rest.user.role.RoleUserPutDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Profile("security")
@Tag(name = "teams", description = "Everything about teams")
@RequestMapping("/teams")
@CrossOrigin
@Log4j2
public class TeamsController {

    private final TeamService teamService;
    private final ITeamDtoService teamDtoService;
    private final RoleService roleService;

    public TeamsController(TeamService teamService, ITeamDtoService teamDtoService, RoleService roleService) {
        this.teamService = teamService;
        this.teamDtoService = teamDtoService;
        this.roleService = roleService;
    }

    @PostMapping("")
    public ResponseEntity<TeamDto> create(@RequestBody TeamPostDto userPostDto) {
        TeamKey teamKey = new TeamKey(UUID.randomUUID());
        Team team = Team.builder()
                .teamKey(teamKey)
                .teamName(userPostDto.getTeamName())
                .securityGroupKeys(new HashSet<>())
                .roles(
                        Stream.of(
                                convertDefaultRole(IESIRole.ADMIN, teamKey),
                                convertDefaultRole(IESIRole.TECHNICAL_ENGINEER, teamKey),
                                convertDefaultRole(IESIRole.TEST_ENGINEER, teamKey),
                                convertDefaultRole(IESIRole.EXECUTOR, teamKey),
                                convertDefaultRole(IESIRole.VIEWER, teamKey)
                        ).collect(Collectors.toSet()))
                .build();
        teamService.addTeam(team);
        return ResponseEntity.of(teamDtoService.get(team.getMetadataKey().getUuid()));
    }

    private Role convertDefaultRole(IESIRole iesiRole, TeamKey teamKey) {
        RoleKey roleKey = new RoleKey(UUID.randomUUID());
        return Role.builder()
                .metadataKey(roleKey)
                .teamKey(teamKey)
                .userKeys(new HashSet<>())
                .name(iesiRole.getName())
                .privileges(
                        iesiRole.getIesiPrivileges().stream().map(
                                iesiPrivilege -> Privilege.builder()
                                        .privilegeKey(new PrivilegeKey(UUID.randomUUID()))
                                        .roleKey(roleKey)
                                        .privilege(iesiPrivilege.getPrivilege())
                                        .build()
                        ).collect(Collectors.toSet())
                ).build();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<TeamDto> fetch(@PathVariable UUID uuid) {
        return ResponseEntity
                .of(teamDtoService.get(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<TeamDto> update(@PathVariable UUID uuid, @RequestBody TeamPutDto teamPutDto) {
        Team team = Team.builder()
                .teamKey(new TeamKey(teamPutDto.getId()))
                .teamName(teamPutDto.getTeamName())
                .securityGroupKeys(
                        teamPutDto.getSecurityGroupIds().stream()
                                .map(SecurityGroupKey::new)
                                .collect(Collectors.toSet())
                )
                .roles(
                        teamPutDto.getRoles().stream()
                                .map(role -> Role.builder()
                                        .metadataKey(new RoleKey(role.getId()))
                                        .name(role.getName())
                                        .teamKey(new TeamKey(teamPutDto.getId()))
                                        .privileges(
                                                role.getPrivileges().stream()
                                                        .map(privilege -> Privilege.builder()
                                                                .privilegeKey(new PrivilegeKey(privilege.getUuid()))
                                                                .roleKey(new RoleKey(role.getId()))
                                                                .privilege(privilege.getPrivilege())
                                                                .build()
                                                        ).collect(Collectors.toSet()))
                                        .userKeys(
                                                role.getUsers().stream()
                                                        .map(UserKey::new)
                                                        .collect(Collectors.toSet())
                                        )
                                        .build()
                                ).collect(Collectors.toSet()))
                .build();
        teamService.update(team);
        return ResponseEntity
                .of(teamDtoService.get(uuid));
    }

    @GetMapping("")
    public Set<TeamDto> fetchAll() {
        return teamDtoService.getAll();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Object> deleteById(@PathVariable UUID uuid) {
        teamService.delete(new TeamKey(uuid));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/roles")
    public ResponseEntity<TeamDto> addRole(@PathVariable UUID uuid, @RequestBody RolePostDto rolePostDto) {
        if (teamService.exists(new TeamKey(uuid))) {
            RoleKey roleKey = new RoleKey(UUID.randomUUID());
            teamService.addRole(new TeamKey(uuid), Role.builder()
                    .metadataKey(roleKey)
                    .teamKey(new TeamKey(uuid))
                    .name(rolePostDto.getName())
                    .userKeys(new HashSet<>())
                    .privileges(rolePostDto.getPrivileges().stream()
                            .map(privilegePostDto -> Privilege.builder()
                                    .privilegeKey(new PrivilegeKey(UUID.randomUUID()))
                                    .roleKey(roleKey)
                                    .privilege(privilegePostDto.getPrivilege())
                                    .build())
                            .collect(Collectors.toSet()))
                    .build());
            return ResponseEntity.of(teamDtoService.get(uuid));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{team-uuid}/roles/{role-uuid}")
    public ResponseEntity<Object> deleteRole(@PathVariable("team-uuid") UUID teamUuid, @PathVariable("role-uuid") UUID roleUuid) {
        // TODO: check role-team key and team key
        if (teamService.exists(new TeamKey(teamUuid))) {
            teamService.deleteRole(new TeamKey(teamUuid), new RoleKey(roleUuid));
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{team-uuid}/roles/{role-uuid}/users")
    public ResponseEntity<Object> addUserToRole(@PathVariable("team-uuid") UUID teamUuid,
                                           @PathVariable("role-uuid") UUID roleUuid,
                                           @RequestBody RoleUserPutDto rolePostDto) {
        if (teamService.exists(new TeamKey(teamUuid)) && roleService.exists(new RoleKey(roleUuid))) {
            roleService.addUser(new RoleKey(roleUuid), new UserKey(rolePostDto.getId()));
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{team-uuid}/roles/{role-uuid}/users/{user-uuid}")
    public ResponseEntity<Object> deleteUserFromRole(@PathVariable("team-uuid") UUID teamUuid,
                                                     @PathVariable("role-uuid") UUID roleUuid,
                                                     @PathVariable("user-uuid") UUID userUuid) {
        if (teamService.exists(new TeamKey(teamUuid)) && roleService.exists(new RoleKey(roleUuid))) {
            roleService.removeUser(new RoleKey(roleUuid), new UserKey(userUuid));
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
