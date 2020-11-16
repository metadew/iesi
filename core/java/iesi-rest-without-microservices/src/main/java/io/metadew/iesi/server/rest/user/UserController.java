package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import io.metadew.iesi.metadata.service.user.UserService;
import io.metadew.iesi.server.rest.configuration.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@RestController
@Profile("security")
@Tag(name = "users", description = "Everything about users")
@RequestMapping("/users")
@Log4j2
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserDtoService userDtoService;

    public UserController(AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder, UserService userService, UserDtoService userDtoService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userDtoService = userDtoService;
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRequest) {
        log.trace("authenticating " + authenticationRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            return jwtService.generateAuthenticationResponse(authentication);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info(stackTrace.toString());
            throw e;
        }
    }

    @PostMapping("/create")
    //@PreAuthorize("hasPrivilege('USERS_WRITE')")
    public ResponseEntity<?> create(@RequestBody UserPostDto userPostDto) {
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
        if (userDto.isPresent()) {
            return ResponseEntity.ok(userDto.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserDto> fetch(@PathVariable UUID uuid) {
        return ResponseEntity
                .of(userDtoService.get(uuid));
    }

    @GetMapping("")
    public Set<UserDto> fetchAll() {
        return userDtoService.getAll();
    }

}
