package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import io.metadew.iesi.metadata.service.user.TeamService;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.error.PasswordsMisMatchException;
import io.metadew.iesi.server.rest.user.team.TeamsController;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.websocket.server.PathParam;
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

    private final PasswordEncoder passwordEncoder;
    private final TeamService teamService;
    private final IUserService userService;
    private final UserDtoModelAssembler userDtoModelAssembler;
    private final PagedResourcesAssembler<UserDto> userDtoPagedResourcesAssembler;

    public UserController(PasswordEncoder passwordEncoder,
                          TeamService teamService,
                          IUserService userService,
                          UserDtoModelAssembler userDtoModelAssembler,
                          PagedResourcesAssembler<UserDto> userDtoPagedResourcesAssembler) {
        this.passwordEncoder = passwordEncoder;
        this.teamService = teamService;
        this.userService = userService;
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

    @PostMapping("/create")
    @PreAuthorize("hasPrivilege('USERS_WRITE')")
    public ResponseEntity<UserDto> create(@RequestBody UserPostDto userPostDto) {
        if (!userPostDto.getPassword().getPassword().equals(userPostDto.getPassword().getRepeatedPassword())) {
            throw new PasswordsMisMatchException();
        }
        if (userService.exists(userPostDto.getUsername())) {
            throw new MetadataAlreadyExistsException("Username " + userPostDto.getUsername() + " is already taken");
        }
        User user = new User(
                new UserKey(UUID.randomUUID()),
                userPostDto.getUsername(),
                passwordEncoder.encode(userPostDto.getPassword().getPassword()),
                true,
                false,
                false,
                false,
                new HashSet<>());
        userService.addUser(user);
        Optional<UserDto> userDto = userService.get(user.getMetadataKey().getUuid());

        return ResponseEntity.of(userDto);
    }

    @PutMapping("/{uuid}/password")
    public ResponseEntity<UserDto> updatePassword(@PathVariable UUID uuid, @RequestBody PasswordPostDto passwordPostDto) {
        if (!passwordPostDto.getPassword().equals(passwordPostDto.getRepeatedPassword())) {
            throw new PasswordsMisMatchException();
        }
        if (!userService.exists(new UserKey(uuid))) {
            throw new MetadataDoesNotExistException("The user with the id \"" + uuid + "\" does not exist");
        }

        userService.updatePassword(passwordEncoder.encode(passwordPostDto.getPassword()), uuid);

        return ResponseEntity.of(userService.get(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UserDto> update(@PathVariable UUID uuid, @RequestBody UserPostDto userPostDto) {
        User user = userService.getRawUser(new UserKey(uuid))
                .orElseThrow(() -> new MetadataDoesNotExistException("The user with the id \"" + uuid + "\" does not exist"));

        if (userService.exists(userPostDto.getUsername())) {
            throw new MetadataAlreadyExistsException(userPostDto.getUsername());
        }


        User updatedUser = new User(
                user.getMetadataKey(),
                userPostDto.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isExpired(),
                user.isCredentialsExpired(),
                user.isLocked(),
                user.getRoleKeys()
        );

        userService.update(updatedUser);
        Optional<UserDto> userDto = userService.get(updatedUser.getMetadataKey().getUuid());

        return ResponseEntity.of(userDto);
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasPrivilege('USERS_READ')")
    public ResponseEntity<UserDto> fetch(@PathVariable String name) {
        return ResponseEntity
                .of(userService.get(name));
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
