package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.*;
import io.metadew.iesi.metadata.service.user.IESIRole;
import io.metadew.iesi.metadata.service.user.TeamService;
import io.metadew.iesi.server.rest.user.UserDto;
import io.metadew.iesi.server.rest.user.UserPostDto;
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

    public TeamsController(TeamService teamService, ITeamDtoService teamDtoService) {
        this.teamService = teamService;
        this.teamDtoService = teamDtoService;
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

}
