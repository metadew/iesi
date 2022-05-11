package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.IESIGrantedAuthority;
import io.metadew.iesi.server.rest.configuration.security.jwt.IesiUserAuthenticationConverter;
import io.metadew.iesi.server.rest.security_group.SecurityGroupService;
import io.metadew.iesi.server.rest.user.team.TeamPostDto;
import io.metadew.iesi.server.rest.user.team.TeamService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {Application.class, TestConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"http", "test"})
@AutoConfigureMockMvc
public class AuthenticationTest {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private IesiUserAuthenticationConverter iesiUserAuthenticationConverter;

    @Autowired
    private DefaultTokenServices authorizationServerTokenServices;

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private SecurityGroupService securityGroupService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void beforeEach() {
        SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.nameUUIDFromBytes("TRAINING".getBytes(StandardCharsets.UTF_8)));
        SecurityGroupKey securityGroupKey1 = new SecurityGroupKey(UUID.nameUUIDFromBytes("PRIVATE".getBytes(StandardCharsets.UTF_8)));

        securityGroupService.addSecurityGroup(new SecurityGroup(
                securityGroupKey,
                "TRAINING",
                new HashSet<>(),
                new HashSet<>()
        ));

        securityGroupService.addSecurityGroup(new SecurityGroup(
                securityGroupKey1,
                "PRIVATE",
                new HashSet<>(),
                new HashSet<>()
        ));

        createTeam("training", securityGroupKey);
        createTeam("private", securityGroupKey1);
    }

    @AfterEach
    void AfterEach() {
        teamService.delete("training");
        teamService.delete("private");
        securityGroupService.delete(new SecurityGroupKey(UUID.nameUUIDFromBytes("TRAINING".getBytes(StandardCharsets.UTF_8))));
        securityGroupService.delete(new SecurityGroupKey(UUID.nameUUIDFromBytes("PRIVATE".getBytes(StandardCharsets.UTF_8))));
    }


    @Test
    void getSuccessfulTokenWithIesiUser() throws Exception {


        OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("admin", "admin")
        ).getBody();

        assertThat(accessToken).isNotNull();
        assertThat(accessToken.getValue()).isNotNull();
        assertThat(accessToken.getRefreshToken()).isNotNull();
        assertThat(accessToken.getTokenType()).isNotNull();
        assertThat(accessToken.getTokenType()).isEqualTo("bearer");
        assertThat(accessToken.getExpiresIn()).isGreaterThan(0);
        assertThat(accessToken.getScope()).isNotNull();
        assertThat(accessToken.getScope()).containsOnly("read-write");
        assertThat(accessToken.getAdditionalInformation().get("jti")).isNotNull();
    }

    @Test
    void getSuccessfulTokenWithADUser() throws Exception {
        OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("adminAD", "admin")
        ).getBody();

        assertThat(accessToken).isNotNull();
        assertThat(accessToken.getValue()).isNotNull();
        assertThat(accessToken.getRefreshToken()).isNotNull();
        assertThat(accessToken.getTokenType()).isNotNull();
        assertThat(accessToken.getTokenType()).isEqualTo("bearer");
        assertThat(accessToken.getExpiresIn()).isGreaterThan(0);
        assertThat(accessToken.getScope()).isNotNull();
        assertThat(accessToken.getScope()).containsOnly("read-write");
        assertThat(accessToken.getAdditionalInformation().get("jti")).isNotNull();
    }

    @Test
    void getUnsuccessfulTokenIfWrongClientId() {
        assertThatThrownBy(() -> tokenEndpoint.postAccessToken(
                generatePrincipal("teste", "test"),
                getPasswordAuthenticationParams("admin", "admin")
        ))
                .isInstanceOf(NoSuchClientException.class)
                .hasMessage("No client with requested id: teste");
    }

    @Test
    void getUnsuccessfulTokenIfWrongIESIPassword() {
        assertThatThrownBy(() -> tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("admin", "admine")
        ))
                .isInstanceOf(InvalidGrantException.class)
                .hasMessage("Bad credentials");
    }

    @Test
    void getUnsuccessfulTokenIfWrondADPassword() {
        assertThatThrownBy(() -> tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("adminAD", "admine")
        ))
                .isInstanceOf(InvalidGrantException.class)
                .hasMessage("Bad credentials");
    }

    @Test
    void checkSameIESIAndADAdminRole() throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken accessTokenIESI = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("admin", "admin")
        ).getBody();

        OAuth2AccessToken accessTokenAD = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("adminAD", "admin")
        ).getBody();

        Authentication authenticationIESI = authorizationServerTokenServices.loadAuthentication(accessTokenIESI.getValue());
        Authentication authenticationAD = authorizationServerTokenServices.loadAuthentication(accessTokenAD.getValue());

        Collection<IESIGrantedAuthority> authoritiesIESI = (Collection<IESIGrantedAuthority>) authenticationIESI.getAuthorities();
        Collection<IESIGrantedAuthority> authoritiesAD = (Collection<IESIGrantedAuthority>) authenticationAD.getAuthorities();

        assertThat(authoritiesAD)
                .hasSameSizeAs(authoritiesIESI)
                .containsExactlyInAnyOrderElementsOf(authoritiesIESI);
    }

    @Test
    void checkSameIESIAndADReaderRole() throws HttpRequestMethodNotSupportedException {
        createUserWithRole("reader", "VIEWER", "iesi");


        OAuth2AccessToken accessTokenIESI = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("reader", "reader")
        ).getBody();

        OAuth2AccessToken accessTokenAD = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("readerAD", "reader")
        ).getBody();

        Authentication authenticationIESI = authorizationServerTokenServices.loadAuthentication(accessTokenIESI.getValue());
        Authentication authenticationAD = authorizationServerTokenServices.loadAuthentication(accessTokenAD.getValue());

        Collection<IESIGrantedAuthority> authoritiesIESI = (Collection<IESIGrantedAuthority>) authenticationIESI.getAuthorities();
        Collection<IESIGrantedAuthority> authoritiesAD = (Collection<IESIGrantedAuthority>) authenticationAD.getAuthorities();

        assertThat(authoritiesAD)
                .hasSameSizeAs(authoritiesIESI)
                .containsExactlyInAnyOrderElementsOf(authoritiesIESI);
        userService.delete("reader");
    }

    @Test
    void checkSameIESIAndADExecutorRole() throws HttpRequestMethodNotSupportedException {
        createUserWithRole("executor", "EXECUTOR", "iesi");


        OAuth2AccessToken accessTokenIESI = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("executor", "executor")
        ).getBody();

        OAuth2AccessToken accessTokenAD = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("executorAD", "executor")
        ).getBody();

        Authentication authenticationIESI = authorizationServerTokenServices.loadAuthentication(accessTokenIESI.getValue());
        Authentication authenticationAD = authorizationServerTokenServices.loadAuthentication(accessTokenAD.getValue());

        Collection<IESIGrantedAuthority> authoritiesIESI = (Collection<IESIGrantedAuthority>) authenticationIESI.getAuthorities();
        Collection<IESIGrantedAuthority> authoritiesAD = (Collection<IESIGrantedAuthority>) authenticationAD.getAuthorities();

        assertThat(authoritiesAD)
                .hasSameSizeAs(authoritiesIESI)
                .containsExactlyInAnyOrderElementsOf(authoritiesIESI);
        userService.delete("executor");
    }

    @Test
    void checkSameIESIAndADTechnicalEngineerRole() throws HttpRequestMethodNotSupportedException {
        createUserWithRole("technical_engineer", "TECHNICAL_ENGINEER", "iesi");


        OAuth2AccessToken accessTokenIESI = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("technical_engineer", "technical_engineer")
        ).getBody();

        OAuth2AccessToken accessTokenAD = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("technical_engineerAD", "technical_engineer")
        ).getBody();

        Authentication authenticationIESI = authorizationServerTokenServices.loadAuthentication(accessTokenIESI.getValue());
        Authentication authenticationAD = authorizationServerTokenServices.loadAuthentication(accessTokenAD.getValue());

        Collection<IESIGrantedAuthority> authoritiesIESI = (Collection<IESIGrantedAuthority>) authenticationIESI.getAuthorities();
        Collection<IESIGrantedAuthority> authoritiesAD = (Collection<IESIGrantedAuthority>) authenticationAD.getAuthorities();

        assertThat(authoritiesAD)
                .hasSameSizeAs(authoritiesIESI)
                .containsExactlyInAnyOrderElementsOf(authoritiesIESI);
        userService.delete("technical_engineer");
    }

    @Test
    void checkSameIESIAndADTestEngineerRole() throws HttpRequestMethodNotSupportedException {
        createUserWithRole("test_engineer", "TEST_ENGINEER", "iesi");


        OAuth2AccessToken accessTokenIESI = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("test_engineer", "test_engineer")
        ).getBody();

        OAuth2AccessToken accessTokenAD = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("test_engineerAD", "test_engineer")
        ).getBody();

        Authentication authenticationIESI = authorizationServerTokenServices.loadAuthentication(accessTokenIESI.getValue());
        Authentication authenticationAD = authorizationServerTokenServices.loadAuthentication(accessTokenAD.getValue());

        Collection<IESIGrantedAuthority> authoritiesIESI = (Collection<IESIGrantedAuthority>) authenticationIESI.getAuthorities();
        Collection<IESIGrantedAuthority> authoritiesAD = (Collection<IESIGrantedAuthority>) authenticationAD.getAuthorities();

        assertThat(authoritiesAD)
                .hasSameSizeAs(authoritiesIESI)
                .containsExactlyInAnyOrderElementsOf(authoritiesIESI);
        userService.delete("test_engineer");
    }

    @Test
    void checkSameIESIAndADWithMultipleTeamsAndRole() throws HttpRequestMethodNotSupportedException {
        UserKey userKey = createUserWithRole("trainer", "ADMIN", "training");
        addRoleToUser(userKey, "TEST_ENGINEER", "private");


        OAuth2AccessToken accessTokenIESI = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("trainer", "trainer")
        ).getBody();

        OAuth2AccessToken accessTokenAD = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("trainerAD", "trainer")
        ).getBody();

        Authentication authenticationIESI = authorizationServerTokenServices.loadAuthentication(accessTokenIESI.getValue());
        Authentication authenticationAD = authorizationServerTokenServices.loadAuthentication(accessTokenAD.getValue());

        Collection<IESIGrantedAuthority> authoritiesIESI = (Collection<IESIGrantedAuthority>) authenticationIESI.getAuthorities();
        Collection<IESIGrantedAuthority> authoritiesAD = (Collection<IESIGrantedAuthority>) authenticationAD.getAuthorities();

        assertThat(authoritiesAD)
                .hasSameSizeAs(authoritiesIESI)
                .containsExactlyInAnyOrderElementsOf(authoritiesIESI);
        userService.delete("trainer");
    }

    @Test
    void checkSameIESIAndADWithMultipleSecurityGroups() throws HttpRequestMethodNotSupportedException {
        createUserWithRole("trainer", "ADMIN", "training");
        Team team = teamService.getRawTeam("training")
                .orElseThrow(() -> new RuntimeException("Team training not found"));


        teamService.addSecurityGroup(team.getMetadataKey(), new SecurityGroupKey(UUID.nameUUIDFromBytes("PRIVATE".getBytes(StandardCharsets.UTF_8))));


        OAuth2AccessToken accessTokenIESI = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("trainer", "trainer")
        ).getBody();

        OAuth2AccessToken accessTokenAD = tokenEndpoint.postAccessToken(
                generatePrincipal("test", "test"),
                getPasswordAuthenticationParams("trainerAD", "trainer")
        ).getBody();

        Authentication authenticationIESI = authorizationServerTokenServices.loadAuthentication(accessTokenIESI.getValue());
        Authentication authenticationAD = authorizationServerTokenServices.loadAuthentication(accessTokenAD.getValue());

        Collection<IESIGrantedAuthority> authoritiesIESI = (Collection<IESIGrantedAuthority>) authenticationIESI.getAuthorities();
        Collection<IESIGrantedAuthority> authoritiesAD = (Collection<IESIGrantedAuthority>) authenticationAD.getAuthorities();

        assertThat(authoritiesAD)
                .hasSameSizeAs(authoritiesIESI)
                .containsExactlyInAnyOrderElementsOf(authoritiesIESI);

        teamService.removeSecurityGroup(team.getMetadataKey(), new SecurityGroupKey(UUID.nameUUIDFromBytes("PRIVATE".getBytes(StandardCharsets.UTF_8))));
        userService.delete("trainer");
    }


    private Principal generatePrincipal(String clientName, String clientPassword) {
        return new UsernamePasswordAuthenticationToken(clientName, clientPassword, Stream.of(new SimpleGrantedAuthority("CLIENT")).collect(Collectors.toSet()));
    }


    private Map<String, String> getPasswordAuthenticationParams(String username, String password) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", "test");
        parameters.put("client_secret", "test");
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("grant_type", "password");

        return parameters;
    }

    private UserKey createUserWithRole(String username, String role, String teamName) {
        UserKey userKey = new UserKey(UUID.randomUUID());
        userService.addUser(new User(
                userKey,
                username,
                bCryptPasswordEncoder.encode(username),
                true,
                false,
                false,
                false,
                new HashSet<>()
        ));

        addRoleToUser(userKey, role, teamName);

        return userKey;
    }

    private void addRoleToUser(UserKey userKey, String role, String teamName) {

        Team iesiTeam = teamService.getRawTeam(teamName)
                .orElseThrow(() -> new RuntimeException("Team iesi not found"));

        Role readerRole = iesiTeam.getRoles()
                .stream()
                .filter(r -> r.getName().equals(role))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role VIEWER does not exist in the team IESI"));

        teamService.addUserToRole(iesiTeam.getMetadataKey(), readerRole.getMetadataKey(), userKey);
    }

    private void createTeam(String teamName, SecurityGroupKey securityGroupKey) {
        Team team = teamService.convertToEntity(new TeamPostDto(teamName));
        teamService.addTeam(team);

        teamService.addSecurityGroup(team.getMetadataKey(), securityGroupKey);
    }
}
