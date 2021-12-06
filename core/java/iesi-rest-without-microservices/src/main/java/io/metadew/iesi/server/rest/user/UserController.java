package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import io.metadew.iesi.metadata.service.user.TeamService;
import io.metadew.iesi.server.rest.configuration.security.jwt.JwtService;
import io.metadew.iesi.server.rest.configuration.security.ldap.LdapAuthenticationProvider;
import io.metadew.iesi.server.rest.user.team.TeamsController;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/users")
@Log4j2
@DependsOn("teamsController")
@ConditionalOnWebApplication
public class UserController {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TeamService teamService;
    private final IUserService userService;
    private final UserDtoModelAssembler userDtoModelAssembler;
    private final PagedResourcesAssembler<UserDto> userDtoPagedResourcesAssembler;
    private final LdapAuthenticationProvider ldapAuthenticationProvider;

    public UserController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder,
                          TeamService teamService,
                          IUserService userService,
                          UserDtoModelAssembler userDtoModelAssembler,
                          PagedResourcesAssembler<UserDto> userDtoPagedResourcesAssembler,
                          LdapAuthenticationProvider ldapAuthenticationProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.teamService = teamService;
        this.userService = userService;
        this.userDtoModelAssembler = userDtoModelAssembler;
        this.userDtoPagedResourcesAssembler = userDtoPagedResourcesAssembler;
        this.ldapAuthenticationProvider = ldapAuthenticationProvider;
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
        // authenticationManager will load the user details (containing the encrypted password) using the IesiUserDetailManager
        // and will match the password based on the provided password
        Authentication authentication = null;
        try {
            authentication = ldapAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            if (authentication == null) {
                authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            }
        } catch (AuthenticationException e) {
            log.error("authentication error" + e);
        }
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
        Optional<UserDto> userDto = userService.get(user.getMetadataKey().getUuid());
        return userDto
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('USERS_READ')")
    public ResponseEntity<UserDto> fetch(@PathVariable UUID uuid) {
        return ResponseEntity
                .of(userService.get(uuid));
    }

    @GetMapping("")
    @PreAuthorize("hasPrivilege('USERS_READ')")
    public PagedModel<UserDto> fetchAll(Pageable pageable,
                                        @RequestParam(required = false, name = "username") String username) {
        Page<UserDto> userDtoPage = userService.getAll(pageable,
                new UserFiltersBuilder()
                        .username(username)
                        .build());
        if (userDtoPage.hasContent())
            return userDtoPagedResourcesAssembler.toModel(userDtoPage, userDtoModelAssembler::toModel);
        return (PagedModel<UserDto>) userDtoPagedResourcesAssembler.toEmptyModel(userDtoPage, UserDto.class);

    }


}
