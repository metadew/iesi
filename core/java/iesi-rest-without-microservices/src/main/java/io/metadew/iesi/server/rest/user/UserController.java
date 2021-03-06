package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import io.metadew.iesi.metadata.service.user.TeamService;
import io.metadew.iesi.metadata.service.user.UserService;
import io.metadew.iesi.server.rest.configuration.security.jwt.JwtService;
import io.metadew.iesi.server.rest.user.team.TeamsController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;


@RestController
@Tag(name = "users", description = "Everything about users")
@RequestMapping("/users")
@Log4j2
// the team controller should be created first as it needs to check if the 'iesi' team is already created.
@DependsOn("teamsController")
public class UserController {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TeamService teamService;
    private final UserService userService;
    private final UserDtoService userDtoService;
    private final UserDtoModelAssembler userDtoModelAssembler;
    private final PagedResourcesAssembler<UserDto> userDtoPagedResourcesAssembler;

    public UserController(AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder, TeamService teamService, UserService userService, UserDtoService userDtoService, UserDtoModelAssembler userDtoModelAssembler, PagedResourcesAssembler<UserDto> userDtoPagedResourcesAssembler) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.teamService = teamService;
        this.userService = userService;
        this.userDtoService = userDtoService;
        this.userDtoModelAssembler = userDtoModelAssembler;
        this.userDtoPagedResourcesAssembler = userDtoPagedResourcesAssembler;
    }

    @PostConstruct
    void checkIesiTeam() {
        if (!userService.exists(ADMIN_USERNAME)) {
            log.warn("Creating SYSADMIN user 'admin' with default password. Please change this password");
            User admin = User.builder()
                    .username(ADMIN_USERNAME)
                    .credentialsExpired(false)
                    .enabled(true)
                    .expired(false)
                    .locked(false)
                    .userKey(new UserKey(UUID.randomUUID()))
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .roleKeys(new HashSet<>())
                    .build();
            Optional<Team> iesiTeam = teamService.get(TeamsController.IESI_GROUP_NAME);
            if (iesiTeam.isPresent()) {
                addToRole(admin, iesiTeam.get(), "SYSADMIN");
                addToRole(admin, iesiTeam.get(), "ADMIN");
            } else {
                log.warn(String.format("Team %s does not exist. Unable to add 'admin' to the team.", TeamsController.IESI_GROUP_NAME));
            }
            userService.addUser(admin);
        }
    }

    private void addToRole(User admin, Team iesiTeam, String roleName) {
        Optional<Role> sysAdminRole = iesiTeam.getRoles().stream()
                .filter(role -> role.getName().equalsIgnoreCase(roleName))
                .findFirst();
        if (sysAdminRole.isPresent()) {
            log.info(String.format("adding user '%s' as a sysadmin to the %s team", ADMIN_USERNAME, TeamsController.IESI_GROUP_NAME));
            admin.getRoleKeys().add(sysAdminRole.get().getMetadataKey());
        } else {
            log.warn(String.format("Team %s does not contain a sysadmin role", TeamsController.IESI_GROUP_NAME));
        }
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRequest) {
        log.trace("authenticating " + authenticationRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        return jwtService.generateAuthenticationResponse(authentication);
    }

    @PostMapping("/create")
    @PreAuthorize("hasPrivilege('USERS_WRITE')")
    public ResponseEntity<Object> create(@RequestBody UserPostDto userPostDto) {
        if (userService.exists(userPostDto.getUsername())) {
            return ResponseEntity.badRequest().body("username is already taken");
        }
        User user = new User(
                new UserKey(UUID.randomUUID()),
                userPostDto.getUsername(),
                passwordEncoder.encode(userPostDto.getPassword()),
                true,
                false,
                false,
                false,
                new HashSet<>());
        userService.addUser(user);
        Optional<UserDto> userDto = userDtoService.get(user.getMetadataKey().getUuid());
        return userDto
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('USERS_READ')")
    public ResponseEntity<UserDto> fetch(@PathVariable UUID uuid) {
        return ResponseEntity
                .of(userDtoService.get(uuid));
    }

    @GetMapping("")
    @PreAuthorize("hasPrivilege('USERS_READ')")
    public PagedModel<UserDto> fetchAll(Pageable pageable,
                                 @RequestParam(required = false, name = "username") String username) {
        Page<UserDto> userDtoPage = userDtoService.getAll(pageable,
                new UserFiltersBuilder()
                        .username(username)
                        .build());
        if (userDtoPage.hasContent())
            return userDtoPagedResourcesAssembler.toModel(userDtoPage, userDtoModelAssembler::toModel);
        return (PagedModel<UserDto>) userDtoPagedResourcesAssembler.toEmptyModel(userDtoPage, UserDto.class);

    }



}
