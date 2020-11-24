package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.*;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import io.metadew.iesi.metadata.service.user.IESIPrivilege;
import io.metadew.iesi.metadata.service.user.IESIRole;
import io.metadew.iesi.metadata.service.user.RoleService;
import io.metadew.iesi.metadata.service.user.TeamService;
import io.metadew.iesi.server.rest.user.role.RolePostDto;
import io.metadew.iesi.server.rest.user.role.RolePutDto;
import io.metadew.iesi.server.rest.user.role.RoleUserPutDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Optional;
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
// SecurityController first needs to initialize the PUBLIC security group
@DependsOn("securityGroupController")
public class TeamsController {

    private final TeamService teamService;
    private final ITeamDtoService teamDtoService;
    private final RoleService roleService;
    private final SecurityGroupService securityGroupService;

    private static final Set<String> SYS_ADMIN_ONLY_PRIVILEGES = Stream.of(
            IESIPrivilege.SECURITY_GROUP_DELETE.getPrivilege()
    ).collect(Collectors.toSet());

    public TeamsController(TeamService teamService, ITeamDtoService teamDtoService, RoleService roleService, SecurityGroupService securityGroupService) {
        this.teamService = teamService;
        this.teamDtoService = teamDtoService;
        this.roleService = roleService;
        this.securityGroupService = securityGroupService;
    }

    @PostConstruct
    void initIESITeam() {
        if (!teamService.exists("iesi")) {
            log.warn("Creating iesi team with default roles: SYSADMIN, ADMIN, TECHNICAL_ENGINEER, TEST_ENGINEER, EXECUTOR and VIEWER");
            TeamKey teamKey = new TeamKey(UUID.randomUUID());
            Team team = Team.builder()
                    .teamKey(teamKey)
                    .teamName("iesi")
                    .securityGroupKeys(new HashSet<>())
                    .roles(
                            Stream.of(
                                    roleService.convertDefaultRole(IESIRole.SYS_ADMIN, teamKey),
                                    roleService.convertDefaultRole(IESIRole.ADMIN, teamKey),
                                    roleService.convertDefaultRole(IESIRole.TECHNICAL_ENGINEER, teamKey),
                                    roleService.convertDefaultRole(IESIRole.TEST_ENGINEER, teamKey),
                                    roleService.convertDefaultRole(IESIRole.EXECUTOR, teamKey),
                                    roleService.convertDefaultRole(IESIRole.VIEWER, teamKey)
                            ).collect(Collectors.toSet()))
                    .build();
            Optional<SecurityGroup> publicSecurityGroup = securityGroupService.get("PUBLIC");
            if (publicSecurityGroup.isPresent()) {
                team.getSecurityGroupKeys().add(publicSecurityGroup.get().getMetadataKey());
                teamService.addTeam(team);
            } else {
                log.warn("unable to find security group 'PUBLIC'. The 'iesi' team will not be created");
            }
        }
    }

    @PostMapping("")
    @PreAuthorize("hasPrivilege('TEAMS_WRITE')")
    public ResponseEntity<TeamDto> create(@RequestBody TeamPostDto teamPostDto) {
        TeamKey teamKey = new TeamKey(UUID.randomUUID());
        Team team = Team.builder()
                .teamKey(teamKey)
                .teamName(teamPostDto.getTeamName())
                .securityGroupKeys(new HashSet<>())
                .roles(
                        Stream.of(
                                roleService.convertDefaultRole(IESIRole.ADMIN, teamKey),
                                roleService.convertDefaultRole(IESIRole.TECHNICAL_ENGINEER, teamKey),
                                roleService.convertDefaultRole(IESIRole.TEST_ENGINEER, teamKey),
                                roleService.convertDefaultRole(IESIRole.EXECUTOR, teamKey),
                                roleService.convertDefaultRole(IESIRole.VIEWER, teamKey)
                        ).collect(Collectors.toSet()))
                .build();
        teamService.addTeam(team);
        return ResponseEntity.of(teamDtoService.get(team.getMetadataKey().getUuid()));
    }

    private boolean checkNoSysadminPrivileges(RolePutDto rolePutDto) {
        return rolePutDto.getPrivileges().stream()
                .noneMatch(privilegeDto -> SYS_ADMIN_ONLY_PRIVILEGES.contains(privilegeDto.getPrivilege()));
    }

    private boolean checkNoSysadminPrivileges(RolePostDto rolePostDto) {
        return rolePostDto.getPrivileges().stream()
                .noneMatch(privilegeDto -> SYS_ADMIN_ONLY_PRIVILEGES.contains(privilegeDto.getPrivilege()));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('TEAMS_READ')")
    public ResponseEntity<TeamDto> fetch(@PathVariable UUID uuid) {
        return ResponseEntity
                .of(teamDtoService.get(uuid));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('TEAMS_WRITE')")
    public ResponseEntity<TeamDto> update(@PathVariable UUID uuid, @RequestBody TeamPutDto teamPutDto) {
        if (!teamPutDto.getRoles().stream().allMatch(this::checkNoSysadminPrivileges)) {
            ResponseEntity.badRequest().body("Cannot add sys admin privileges to a role");
        }

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
    @PreAuthorize("hasPrivilege('TEAMS_READ')")
    public Set<TeamDto> fetchAll() {
        return teamDtoService.getAll();
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('TEAMS_DELETE')")
    public ResponseEntity<Object> deleteById(@PathVariable UUID uuid) {
        teamService.delete(new TeamKey(uuid));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/roles")
    @PreAuthorize("hasPrivilege('ROLES_WRITE')")
    public ResponseEntity<TeamDto> addRole(@PathVariable UUID uuid, @RequestBody RolePostDto rolePostDto) {
        if (!checkNoSysadminPrivileges(rolePostDto)) {
            ResponseEntity.badRequest().body("Cannot add role with sys admin privileges");
        }
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
    @PreAuthorize("hasPrivilege('ROLES_DELETE')")
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
    @PreAuthorize("hasPrivilege('ROLES_WRITE')")
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
    @PreAuthorize("hasPrivilege('ROLES_DELETE')")
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
