package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.user.*;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import io.metadew.iesi.metadata.service.user.IESIPrivilege;
import io.metadew.iesi.metadata.service.user.IESIRole;
import io.metadew.iesi.metadata.service.user.RoleService;
import io.metadew.iesi.metadata.service.user.UserService;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
import io.metadew.iesi.server.rest.security_group.SecurityGroupController;
import io.metadew.iesi.server.rest.user.role.RolePostDto;
import io.metadew.iesi.server.rest.user.role.RolePutDto;
import io.metadew.iesi.server.rest.user.role.RoleUserPutDto;
import io.metadew.iesi.server.rest.user.team.dto.TeamDtoResourceAssembler;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/teams")
@CrossOrigin
@Log4j2
@DependsOn("securityGroupController")
@ConditionalOnWebApplication
public class TeamsController {

    private final ITeamService teamService;
    private final ITeamPutDtoService teamPutDtoService;
    private final TeamDtoResourceAssembler teamDtoResourceAssembler;
    private final PagedResourcesAssembler<TeamDto> teamDtoPagedResourcesAssembler;
    private final RoleService roleService;
    private final UserService userService;
    private final SecurityGroupService securityGroupService;
    private final IesiSecurityChecker iesiSecurityChecker;

    public static final String IESI_GROUP_NAME = "iesi";
    private static final Set<String> SYS_ADMIN_ONLY_PRIVILEGES = Stream.of(
            IESIPrivilege.SECURITY_GROUP_MODIFY.getPrivilege()
    ).collect(Collectors.toSet());

    public TeamsController(
            ITeamService teamService,
            ITeamPutDtoService teamPutDtoService,
            TeamDtoResourceAssembler teamDtoResourceAssembler,
            PagedResourcesAssembler<TeamDto> teamDtoPagedResourcesAssembler,
            RoleService roleService,
            UserService userService,
            SecurityGroupService securityGroupService,
            IesiSecurityChecker iesiSecurityChecker) {
        this.teamService = teamService;
        this.teamPutDtoService = teamPutDtoService;
        this.teamDtoResourceAssembler = teamDtoResourceAssembler;
        this.teamDtoPagedResourcesAssembler = teamDtoPagedResourcesAssembler;
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
                    .securityGroups(new HashSet<>())
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
                team.getSecurityGroups().add(publicSecurityGroup.get());
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
        Team team = teamService.convertToEntity(teamPostDto);
        teamService.addTeam(team);
        return ResponseEntity.of(teamService.get(team.getMetadataKey().getUuid()));
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
    public ResponseEntity<TeamDto> get(@PathVariable UUID uuid) {
        return ResponseEntity
                .of(teamService.get(uuid));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('TEAMS_WRITE')")
    public ResponseEntity<TeamDto> update(@PathVariable UUID uuid, @RequestBody TeamPutDto teamPutDto) {
        if (!teamPutDto.getRoles().stream().allMatch(this::checkNoSysadminPrivileges)) {
            ResponseEntity.badRequest().body("Cannot add sys admin privileges to a role");
        }

        Team team = teamPutDtoService.convertToEntity(teamPutDto);
        teamService.update(team);
        return ResponseEntity
                .of(teamService.get(uuid));
    }

    @GetMapping("")
    @PreAuthorize("hasPrivilege('TEAMS_READ')")
    public PagedModel<TeamDto> getAll(Pageable pageable, @RequestParam(required = false, name = "name") String name) {
        List<TeamFilter> teamFilters = extractTeamFilterOptions(name);
        Page<TeamDto> teamDtoPage = teamService.getAll(pageable, teamFilters);

        if (teamDtoPage.hasContent()) {
            return teamDtoPagedResourcesAssembler.toModel(teamDtoPage, teamDtoResourceAssembler::toModel);
        }
        return (PagedModel<TeamDto>) teamDtoPagedResourcesAssembler.toEmptyModel(teamDtoPage, TeamDto.class);
    }

    private List<TeamFilter> extractTeamFilterOptions(String name) {
        List<TeamFilter> teamFilters = new ArrayList<>();
        if (name != null) {
            teamFilters.add(new TeamFilter(TeamFilterOption.NAME, name, false));
        }
        return teamFilters;
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
        if (!checkNoSysadminPrivileges(rolePostDto)) {
            ResponseEntity.badRequest().body("Cannot add role with sys admin privileges");
        }
        RoleKey roleKey = new RoleKey(UUID.randomUUID());
        teamService.addRole(new TeamKey(uuid), Role.builder()
                .metadataKey(roleKey)
                .teamKey(new TeamKey(uuid))
                .name(rolePostDto.getName())
                .users(new HashSet<>())
                .privileges(rolePostDto.getPrivileges().stream()
                        .map(privilegePostDto -> Privilege.builder()
                                .privilegeKey(new PrivilegeKey(UUID.randomUUID()))
                                .roleKey(roleKey)
                                .privilege(privilegePostDto.getPrivilege())
                                .build())
                        .collect(Collectors.toSet()))
                .build());
        return teamService.get(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{team-uuid}/roles/{role-uuid}")
    @PreAuthorize("hasPrivilege('TEAMS_WRITE')")
    public ResponseEntity<Object> deleteRole(@PathVariable("team-uuid") UUID teamUuid, @PathVariable("role-uuid") UUID roleUuid) {
        Optional<Team> team = teamService.getRawTeam(new TeamKey(teamUuid));
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
        Team team = teamService.getRawTeam(new TeamKey(teamUuid))
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
            teamService.addUserToRole(team.getMetadataKey(), new RoleKey(roleUuid), new UserKey(rolePostDto.getId()));
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
        Team team = teamService.getRawTeam(new TeamKey(teamUuid))
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
            teamService.removeUserFromRole(team.getMetadataKey(), new RoleKey(roleUuid), new UserKey(userUuid));
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
