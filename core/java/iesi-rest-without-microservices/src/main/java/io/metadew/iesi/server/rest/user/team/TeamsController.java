package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.*;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import io.metadew.iesi.metadata.service.user.*;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
import io.metadew.iesi.server.rest.security_group.SecurityGroupController;
import io.metadew.iesi.server.rest.user.role.RolePostDto;
import io.metadew.iesi.server.rest.user.role.RolePutDto;
import io.metadew.iesi.server.rest.user.role.RoleUserPutDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
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
    private final UserService userService;
    private final SecurityGroupService securityGroupService;
    private final IesiSecurityChecker iesiSecurityChecker;

    public static final String IESI_GROUP_NAME = "iesi";
    private static final Set<String> SYS_ADMIN_ONLY_PRIVILEGES = Stream.of(
            IESIPrivilege.SECURITY_GROUP_MODIFY.getPrivilege()
    ).collect(Collectors.toSet());

    public TeamsController(TeamService teamService, ITeamDtoService teamDtoService, RoleService roleService, UserService userService, SecurityGroupService securityGroupService, IesiSecurityChecker iesiSecurityChecker) {
        this.teamService = teamService;
        this.teamDtoService = teamDtoService;
        this.roleService = roleService;
        this.userService = userService;
        this.securityGroupService = securityGroupService;
        this.iesiSecurityChecker = iesiSecurityChecker;
    }

    @PostConstruct
    void initIESITeam() {
        if (!teamService.exists(IESI_GROUP_NAME)) {
            log.warn(String.format("Creating %s team with default roles: SYSADMIN, ADMIN, TECHNICAL_ENGINEER, TEST_ENGINEER, EXECUTOR and VIEWER", IESI_GROUP_NAME));
            TeamKey teamKey = new TeamKey(UUID.randomUUID());
            Team team = Team.builder()
                    .teamKey(teamKey)
                    .teamName(IESI_GROUP_NAME)
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
            Optional<SecurityGroup> publicSecurityGroup = securityGroupService.get(SecurityGroupController.PUBLIC_GROUP_NAME);
            if (publicSecurityGroup.isPresent()) {
                team.getSecurityGroupKeys().add(publicSecurityGroup.get().getMetadataKey());
                teamService.addTeam(team);
            } else {
                log.warn(String.format("Unable to find security group %s. The team %s will not be created", SecurityGroupController.PUBLIC_GROUP_NAME, IESI_GROUP_NAME));
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
    @PreAuthorize("hasPrivilege('TEAMS_WRITE')")
    public ResponseEntity<Object> deleteById(@PathVariable UUID uuid) {
        teamService.delete(new TeamKey(uuid));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/roles")
    @PreAuthorize("hasPrivilege('TEAMS_WRITE')")
    public HttpEntity<? extends Object> addRole(@PathVariable UUID uuid, @RequestBody RolePostDto rolePostDto) {
        //Optional<User> user = userService.get(SecurityContextHolder.getContext().getAuthentication().getName());
        //Optional<Team> team = teamService.get(new TeamKey(uuid));
//        if (!team.isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
        // TODO: if a team is created, there will be no admin of that team. thus no way to add a user to a team, thus no way to dd an admin ...
        // TODO: check if it is possible to link this ROLES_WRITE privilige to a specific team with Spring Security authority
//        if (user.isPresent() && !checkHasPrivilegeAtTeam(user.get(), team.get(), IESIPrivilege.ROLES_MODIFY)) {
//            return ResponseEntity
//                    .status(HttpStatus.FORBIDDEN)
//                    .body(String.format("User %s cannot create a role for team %s", user.get().getUsername(), team.get().getTeamName()));
//        } else {
//            ResponseEntity.badRequest().body(String.format("cannot find user by username %s", SecurityContextHolder.getContext().getAuthentication().getName()));
//        }

        if (!checkNoSysadminPrivileges(rolePostDto)) {
            ResponseEntity.badRequest().body("Cannot add role with sys admin privileges");
        }
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
        return teamDtoService.get(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{team-uuid}/roles/{role-uuid}")
    @PreAuthorize("hasPrivilege('TEAMS_WRITE')")
    public ResponseEntity<Object> deleteRole(@PathVariable("team-uuid") UUID teamUuid, @PathVariable("role-uuid") UUID roleUuid) {
        // Optional<User> user = userService.get(SecurityContextHolder.getContext().getAuthentication().getName());
        Optional<Team> team = teamService.get(new TeamKey(teamUuid));
//        if (!team.isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        // TODO: if a team is created, there will be no admin of that team. thus no way to add a user to a team, thus no way to dd an admin ...
//        // TODO: check if it is possible to link this ROLES_WRITE privilige to a specific team with Spring Security authority
//        if (user.isPresent() && !checkHasPrivilegeAtTeam(user.get(), team.get(), IESIPrivilege.ROLES_MODIFY)) {
//            return ResponseEntity
//                    .status(HttpStatus.FORBIDDEN)
//                    .body(String.format("User %s cannot remove a role from team %s", user.get().getUsername(), team.get().getTeamName()));
//        } else {
//            ResponseEntity.badRequest().body(String.format("cannot find user by username %s", SecurityContextHolder.getContext().getAuthentication().getName()));
//        }

        // check role-team key and team key
        if (checkRoleMembership(team.get(), roleUuid)) {
            teamService.deleteRole(new TeamKey(teamUuid), new RoleKey(roleUuid));
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean checkRoleMembership(Team team, UUID roleUuid) {
        return team.getRoles().stream()
                .anyMatch(role -> role.getMetadataKey().getUuid().equals(roleUuid));
    }

    private boolean checkHasPrivilegeAtTeam(User user, Team team, IESIPrivilege iesiPrivilege) {
        return team.getRoles().stream()
                // for each role of a team check if it contains the provided privilege.
                .filter(role -> role.getPrivileges().stream()
                        .map(Privilege::getPrivilege)
                        .collect(Collectors.toSet())
                        .contains(iesiPrivilege.getPrivilege()))
                // for each role containing the provided privilege, check if the user is linked to that role.
                .anyMatch(role -> user.getRoleKeys().contains(role.getMetadataKey()));
    }

    @PostMapping("/{team-uuid}/roles/{role-uuid}/users")
    @PreAuthorize("hasPrivilege('ROLES_WRITE') or hasPrivilege('TEAMS_WRITE')")
    public ResponseEntity<Object> addUserToRole(@PathVariable("team-uuid") UUID teamUuid,
                                                @PathVariable("role-uuid") UUID roleUuid,
                                                @RequestBody RoleUserPutDto rolePostDto) {
        Team team = teamService.get(new TeamKey(teamUuid))
                .orElseThrow(RuntimeException::new);
        User user = userService.get(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(RuntimeException::new);
        if (!iesiSecurityChecker.hasPrivilege(
                SecurityContextHolder.getContext().getAuthentication(),
                IESIPrivilege.TEAMS_MODIFY.getPrivilege())
                && !checkHasPrivilegeAtTeam(user, team, IESIPrivilege.ROLES_MODIFY)) {
            throw new AccessDeniedException(String.format("User %s cannot add a user to the team %s", user.getUsername(), team.getTeamName()));
        }

        if (checkRoleMembership(team, roleUuid)) {
            roleService.addUser(new RoleKey(roleUuid), new UserKey(rolePostDto.getId()));
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @DeleteMapping("/{team-uuid}/roles/{role-uuid}/users/{user-uuid}")
    @PreAuthorize("hasPrivilege('ROLES_WRITE')")
    public ResponseEntity<Object> deleteUserFromRole(@PathVariable("team-uuid") UUID teamUuid,
                                                     @PathVariable("role-uuid") UUID roleUuid,
                                                     @PathVariable("user-uuid") UUID userUuid) {
        Team team = teamService.get(new TeamKey(teamUuid))
                .orElseThrow(RuntimeException::new);
        User user = userService.get(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(RuntimeException::new);
        if (!iesiSecurityChecker.hasPrivilege(
                SecurityContextHolder.getContext().getAuthentication(),
                IESIPrivilege.TEAMS_MODIFY.getPrivilege())
                && !checkHasPrivilegeAtTeam(user, team, IESIPrivilege.ROLES_MODIFY)) {
            throw new AccessDeniedException(String.format("User %s cannot add a user to the team %s", user.getUsername(), team.getTeamName()));
        }
        if (checkRoleMembership(team, roleUuid)) {
            roleService.removeUser(new RoleKey(roleUuid), new UserKey(userUuid));
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
