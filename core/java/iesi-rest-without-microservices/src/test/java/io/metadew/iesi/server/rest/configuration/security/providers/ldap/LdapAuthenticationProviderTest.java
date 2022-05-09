package io.metadew.iesi.server.rest.configuration.security.providers.ldap;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.user.Privilege;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.IESIGrantedAuthority;
import io.metadew.iesi.server.rest.user.team.TeamService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
@ContextConfiguration(classes = {TestConfiguration.class})
@ActiveProfiles("test")
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class LdapAuthenticationProviderTest {

    @Autowired
    private LdapAuthenticationProvider ldapAuthenticationProvider;

    @MockBean
    private TeamService teamService;


    @Test
    @WithMockUser(username = "admin", password = "admin")
    void authenticateSuccessfulWithOneSecurityGroupTest() {
        TeamKey teamKey = new TeamKey(UUID.randomUUID());
        Set<Privilege> privileges = Stream.of(
                new Privilege(null, "SCRIPTS_READ", null),
                new Privilege(null, "SCRIPTS_WRITE", null)
        ).collect(Collectors.toSet());
        Set<Role> roles = Stream.of(
                new Role(null, "admin", null, privileges, null)
        ).collect(Collectors.toSet());
        Set<SecurityGroup> securityGroups = Stream.of(
                new SecurityGroup(null, "PUBLIC", null, null)
        ).collect(Collectors.toSet());
        Team iesiTeam = new Team(teamKey, "iesi", securityGroups, roles);

        Mockito.when(teamService.getRawTeam("iesi")).thenReturn(Optional.of(iesiTeam));
        Mockito.when(teamService.getSecurityGroups(teamKey)).thenReturn(securityGroups);

        Authentication authentication = ldapAuthenticationProvider.authenticate(SecurityContextHolder.getContext().getAuthentication());

        assertThat(authentication.getName()).isEqualTo("admin");
        assertThat(authentication.getAuthorities()).isNotEmpty();
        assertThat((Collection<IESIGrantedAuthority>) authentication.getAuthorities()).containsExactlyInAnyOrder(
                new IESIGrantedAuthority("PUBLIC", "SCRIPTS_READ"),
                new IESIGrantedAuthority("PUBLIC", "SCRIPTS_WRITE")
        );
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void authenticateSuccessfulWithMultipleSecurityGroupsTest() {
        TeamKey teamKey = new TeamKey(UUID.randomUUID());
        Set<Privilege> privileges = Stream.of(
                new Privilege(null, "SCRIPTS_READ", null),
                new Privilege(null, "SCRIPTS_WRITE", null)
        ).collect(Collectors.toSet());
        Set<Role> roles = Stream.of(
                new Role(null, "admin", null, privileges, null)
        ).collect(Collectors.toSet());
        Set<SecurityGroup> securityGroups = Stream.of(
                new SecurityGroup(null, "PUBLIC", null, null),
                new SecurityGroup(null, "A", null, null)
        ).collect(Collectors.toSet());
        Team iesiTeam = new Team(teamKey, "iesi", securityGroups, roles);

        Mockito.when(teamService.getRawTeam("iesi")).thenReturn(Optional.of(iesiTeam));
        Mockito.when(teamService.getSecurityGroups(teamKey)).thenReturn(securityGroups);

        Authentication authentication = ldapAuthenticationProvider.authenticate(SecurityContextHolder.getContext().getAuthentication());

        assertThat(authentication.getName()).isEqualTo("admin");
        assertThat(authentication.getAuthorities()).isNotEmpty();
        assertThat((Collection<IESIGrantedAuthority>) authentication.getAuthorities()).containsExactlyInAnyOrder(
                new IESIGrantedAuthority("PUBLIC", "SCRIPTS_READ"),
                new IESIGrantedAuthority("PUBLIC", "SCRIPTS_WRITE"),
                new IESIGrantedAuthority("A", "SCRIPTS_READ"),
                new IESIGrantedAuthority("A", "SCRIPTS_WRITE")
        );
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void authenticateSuccessfulWithMultipleRolesTest() {
        TeamKey teamKey = new TeamKey(UUID.randomUUID());
        Set<Privilege> privileges = Stream.of(
                new Privilege(null, "SCRIPTS_READ", null),
                new Privilege(null, "SCRIPTS_WRITE", null),
                new Privilege(null, "ENVIRONMENTS_READ", null),
                new Privilege(null, "ENVIRONMENTS_WRITE", null),
                new Privilege(null, "USERS_READ", null),
                new Privilege(null, "CONNECTIONS_WRITE", null),
                new Privilege(null, "CONNECTIONS_READ", null)
                ).collect(Collectors.toSet());
        Set<Role> roles = Stream.of(
                new Role(null, "admin", null, privileges, null)
        ).collect(Collectors.toSet());
        Set<SecurityGroup> securityGroups = Stream.of(
                new SecurityGroup(null, "PUBLIC", null, null)
        ).collect(Collectors.toSet());
        Team iesiTeam = new Team(teamKey, "iesi", securityGroups, roles);

        Mockito.when(teamService.getRawTeam("iesi")).thenReturn(Optional.of(iesiTeam));
        Mockito.when(teamService.getSecurityGroups(teamKey)).thenReturn(securityGroups);

        Authentication authentication = ldapAuthenticationProvider.authenticate(SecurityContextHolder.getContext().getAuthentication());

        assertThat(authentication.getName()).isEqualTo("admin");
        assertThat(authentication.getAuthorities()).isNotEmpty();
        assertThat((Collection<IESIGrantedAuthority>) authentication.getAuthorities()).containsExactlyInAnyOrder(
                new IESIGrantedAuthority("PUBLIC", "SCRIPTS_READ"),
                new IESIGrantedAuthority("PUBLIC", "SCRIPTS_WRITE"),
                new IESIGrantedAuthority("PUBLIC", "ENVIRONMENTS_READ"),
                new IESIGrantedAuthority("PUBLIC", "ENVIRONMENTS_WRITE"),
                new IESIGrantedAuthority("PUBLIC", "USERS_READ"),
                new IESIGrantedAuthority("PUBLIC", "CONNECTIONS_WRITE"),
                new IESIGrantedAuthority("PUBLIC", "CONNECTIONS_READ")
        );
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void authenticateSuccessfulWithMultipleRolesAndSecurityGroupTest() {
        TeamKey teamKey = new TeamKey(UUID.randomUUID());
        Set<Privilege> privileges = Stream.of(
                new Privilege(null, "SCRIPTS_READ", null),
                new Privilege(null, "SCRIPTS_WRITE", null),
                new Privilege(null, "ENVIRONMENTS_READ", null),
                new Privilege(null, "ENVIRONMENTS_WRITE", null),
                new Privilege(null, "USERS_READ", null),
                new Privilege(null, "CONNECTIONS_WRITE", null),
                new Privilege(null, "CONNECTIONS_READ", null)
        ).collect(Collectors.toSet());
        Set<Role> roles = Stream.of(
                new Role(null, "admin", null, privileges, null)
        ).collect(Collectors.toSet());
        Set<SecurityGroup> securityGroups = Stream.of(
                new SecurityGroup(null, "PUBLIC", null, null),
                new SecurityGroup(null, "A", null, null),
                new SecurityGroup(null, "B", null, null),
                new SecurityGroup(null, "C", null, null)
        ).collect(Collectors.toSet());
        Team iesiTeam = new Team(teamKey, "iesi", securityGroups, roles);

        Mockito.when(teamService.getRawTeam("iesi")).thenReturn(Optional.of(iesiTeam));
        Mockito.when(teamService.getSecurityGroups(teamKey)).thenReturn(securityGroups);

        Authentication authentication = ldapAuthenticationProvider.authenticate(SecurityContextHolder.getContext().getAuthentication());

        assertThat(authentication.getName()).isEqualTo("admin");
        assertThat(authentication.getAuthorities()).isNotEmpty();
        assertThat((Collection<IESIGrantedAuthority>) authentication.getAuthorities()).containsExactlyInAnyOrder(
                new IESIGrantedAuthority("PUBLIC", "SCRIPTS_READ"),
                new IESIGrantedAuthority("PUBLIC", "SCRIPTS_WRITE"),
                new IESIGrantedAuthority("PUBLIC", "ENVIRONMENTS_READ"),
                new IESIGrantedAuthority("PUBLIC", "ENVIRONMENTS_WRITE"),
                new IESIGrantedAuthority("PUBLIC", "USERS_READ"),
                new IESIGrantedAuthority("PUBLIC", "CONNECTIONS_WRITE"),
                new IESIGrantedAuthority("PUBLIC", "CONNECTIONS_READ"),
                new IESIGrantedAuthority("A", "SCRIPTS_READ"),
                new IESIGrantedAuthority("A", "SCRIPTS_WRITE"),
                new IESIGrantedAuthority("A", "ENVIRONMENTS_READ"),
                new IESIGrantedAuthority("A", "ENVIRONMENTS_WRITE"),
                new IESIGrantedAuthority("A", "USERS_READ"),
                new IESIGrantedAuthority("A", "CONNECTIONS_WRITE"),
                new IESIGrantedAuthority("A", "CONNECTIONS_READ"),
                new IESIGrantedAuthority("B", "SCRIPTS_READ"),
                new IESIGrantedAuthority("B", "SCRIPTS_WRITE"),
                new IESIGrantedAuthority("B", "ENVIRONMENTS_READ"),
                new IESIGrantedAuthority("B", "ENVIRONMENTS_WRITE"),
                new IESIGrantedAuthority("B", "USERS_READ"),
                new IESIGrantedAuthority("B", "CONNECTIONS_WRITE"),
                new IESIGrantedAuthority("B", "CONNECTIONS_READ"),
                new IESIGrantedAuthority("C", "SCRIPTS_READ"),
                new IESIGrantedAuthority("C", "SCRIPTS_WRITE"),
                new IESIGrantedAuthority("C", "ENVIRONMENTS_READ"),
                new IESIGrantedAuthority("C", "ENVIRONMENTS_WRITE"),
                new IESIGrantedAuthority("C", "USERS_READ"),
                new IESIGrantedAuthority("C", "CONNECTIONS_WRITE"),
                new IESIGrantedAuthority("C", "CONNECTIONS_READ")
        );
    }

}
