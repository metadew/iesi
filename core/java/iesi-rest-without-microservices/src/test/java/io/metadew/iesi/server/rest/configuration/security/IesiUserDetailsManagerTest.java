package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.Privilege;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.ClockConfiguration;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.user.*;
import io.metadew.iesi.server.rest.user.role.PrivilegeDto;
import io.metadew.iesi.server.rest.user.role.RoleTeamDto;
import io.metadew.iesi.server.rest.user.team.TeamSecurityGroupDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class, ClockConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"test"})
@DirtiesContext
class IesiUserDetailsManagerTest {

    @Autowired
    private IesiUserDetailsManager iesiUserDetailsManager;

    @MockBean
    private IUserService userService;

    @Test
    void loadUserByUsernameTest() {
        Set<SecurityGroup> securityGroups = Stream.of(
                new SecurityGroup(new SecurityGroupKey(UUID.randomUUID()), "group1", new HashSet<>(), new HashSet<>()),
                new SecurityGroup(new SecurityGroupKey(UUID.randomUUID()), "group2", new HashSet<>(), new HashSet<>())
        ).collect(Collectors.toSet());
        Map<String, Object> teamInfo = TeamBuilder.generateTeam(1, 1, 2, securityGroups);
        Team team = (Team) teamInfo.get("team");
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", team.getRoles(), team.getTeamName(), securityGroups);

        when(userService.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get("userDto")));
        when(userService.getRawUser("user1"))
                .thenReturn(Optional.of((User) user1Info.get("user")));
        assertThat(iesiUserDetailsManager.loadUserByUsername("user1"))
        .isEqualTo(new IesiUserDetails(
                (User) user1Info.get("user"),
                Stream.of(
                        new IESIGrantedAuthority("group1", "authority0"),
                        new IESIGrantedAuthority("group1", "authority1"),
                        new IESIGrantedAuthority("group2", "authority1"),
                        new IESIGrantedAuthority("group2", "authority0")
                ).collect(Collectors.toSet())
        ));
    }

    @Test
    void getGrantedAuthorities() {
        Set<SecurityGroup> securityGroups = Stream.of(
                new SecurityGroup(new SecurityGroupKey(UUID.randomUUID()), "group1", new HashSet<>(), new HashSet<>()),
                new SecurityGroup(new SecurityGroupKey(UUID.randomUUID()), "group2", new HashSet<>(), new HashSet<>())
        ).collect(Collectors.toSet());
        Map<String, Object> teamInfo = TeamBuilder.generateTeam(1, 2, 2, securityGroups);
        Team team = (Team) teamInfo.get("team");
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", team.getRoles(), team.getTeamName(), securityGroups);
        when(userService.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get("userDto")));

        assertThat(iesiUserDetailsManager.getGrantedAuthorities("user1"))
                .isEqualTo(Stream.of(
                        new IESIGrantedAuthority("group1", "authority0"),
                        new IESIGrantedAuthority("group1", "authority1"),
                        new IESIGrantedAuthority("group2", "authority1"),
                        new IESIGrantedAuthority("group2", "authority0")
                ).collect(Collectors.toSet()));
    }

    @Test
    void getGrantedAuthoritiesDuplicates() {
        Set<SecurityGroup> securityGroups = Stream.of(
                new SecurityGroup(new SecurityGroupKey(UUID.randomUUID()), "group1", new HashSet<>(), new HashSet<>()),
                new SecurityGroup(new SecurityGroupKey(UUID.randomUUID()), "group2", new HashSet<>(), new HashSet<>())
        ).collect(Collectors.toSet());
        Map<String, Object> teamInfo = TeamBuilder.generateTeam(1, 2, 2, securityGroups);
        Team team = (Team) teamInfo.get("team");
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", team.getRoles(), team.getTeamName(), securityGroups);
        UserDto userDto = (UserDto) user1Info.get("userDto");
        userDto.getRoles().add(new UserRoleDto(
                UUID.randomUUID(),
                "test",
                new RoleTeamDto(
                        UUID.randomUUID(),
                        "role3",
                        Stream.of(
                                new TeamSecurityGroupDto(
                                        UUID.randomUUID(),
                                        "group2"
                                ),
                                new TeamSecurityGroupDto(
                                        UUID.randomUUID(),
                                        "group3"
                                )
                        ).collect(Collectors.toSet())
                ),
                Stream.of(
                        new PrivilegeDto(
                                UUID.randomUUID(),
                                "authority0"
                        )
                ).collect(Collectors.toSet())

        ));
        when(userService.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get("userDto")));

        assertThat(iesiUserDetailsManager.getGrantedAuthorities("user1"))
                .isEqualTo(Stream.of(
                        new IESIGrantedAuthority("group1", "authority0"),
                        new IESIGrantedAuthority("group1", "authority1"),
                        new IESIGrantedAuthority("group2", "authority1"),
                        new IESIGrantedAuthority("group2", "authority0"),
                        new IESIGrantedAuthority("group3", "authority0")
                ).collect(Collectors.toSet()));
    }

}
