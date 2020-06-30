package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.server.rest.configuration.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Collectors;

@RestController
@Profile("security")
@Tag(name = "users", description = "Everything about users")
@RequestMapping("/users")
@CrossOrigin
@Log4j2
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public UserController(AuthenticationManager authenticationManager, JwtService jwtService, CustomUserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRequest) {
        log.info("authenticating " + authenticationRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            log.info("authenticated " + authentication.getPrincipal());
            return jwtService.generateAuthenticationResponse(authentication);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            log.info(StackTrace.toString());
            throw e;
        }
    }

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody UserPostDto userPostDto) {
        if (userDetailsManager.userExists(userPostDto.getUsername())) {
            return ResponseEntity.badRequest().body("username is already taken");
        }
        User user = new User(userPostDto.getUsername(),
                passwordEncoder.encode(userPostDto.getPassword()),
                userPostDto.getAuthorities().stream()
                        .map(authorityDto -> new SimpleGrantedAuthority(authorityDto.getAuthority()))
                        .collect(Collectors.toList()));
        userDetailsManager.createUser(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> fetch(@PathVariable String username) {
        if (!userDetailsManager.userExists(username)) {
            return ResponseEntity.notFound().build();
        }
        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
        UserDto user = new UserDto(userDetails.getUsername(),
                userDetails.isEnabled(),
                !userDetails.isAccountNonExpired(),
                !userDetails.isCredentialsNonExpired(),
                !userDetails.isAccountNonLocked(),
                userDetails.getAuthorities().stream()
                        .map(authority -> new AuthorityDto(authority.getAuthority()))
                        .collect(Collectors.toList()));
        return ResponseEntity.ok(user);
    }

}
