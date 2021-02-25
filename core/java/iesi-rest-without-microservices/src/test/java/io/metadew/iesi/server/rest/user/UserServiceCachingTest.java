package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.user.*;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.ClockConfiguration;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.user.team.ITeamDtoRepository;
import io.metadew.iesi.server.rest.user.team.ITeamService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class, ClockConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"test"})
class UserServiceCachingTest {

    @SpyBean
    private IUserService userService;

    @Autowired
    private ITeamService teamService;

    @MockBean
    private UserDtoRepository userDtoRepository;

    @MockBean
    private io.metadew.iesi.metadata.service.user.UserService rawUserService;

    @MockBean
    private io.metadew.iesi.metadata.service.user.TeamService rawTeamService;

    @MockBean
    private ITeamDtoRepository teamDtoRepository;

    @Autowired
    private CacheManager cacheManager;

    @AfterEach
    void cleanUpCache() {
        cacheManager.getCache("users").clear();
    }

    @Test
    void getByNameSimpleDoubleInvocation() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));

        userService.get("user1");
        userService.get("user1");
        verify(userService, times(1)).get("user1");
    }

    @Test
    void getByUuidSimpleDoubleInvocation() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        UUID userUuid = (UUID) user1Info.get("userUUID");
        when(userDtoRepository.get(userUuid))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));

        userService.get(userUuid);
        userService.get(userUuid);
        verify(userService, times(1)).get(userUuid);
    }

    @Test
    void getByUuidAndNameMixedInvocation() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        UUID userUuid = (UUID) user1Info.get("userUUID");
        when(userDtoRepository.get(userUuid))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));

        userService.get(userUuid);
        userService.get("user1");
        userService.get(userUuid);
        userService.get("user1");
        verify(userService, times(1)).get(userUuid);
        verify(userService, times(1)).get("user1");
    }

    @Test
    void getByUuidEvictAfterUpdate() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        UUID userUuid = (UUID) user1Info.get("userUUID");
        User user = (User) user1Info.get(("user"));
        when(userDtoRepository.get(userUuid))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        user.setEnabled(false);
        userService.get(userUuid);
        userService.get(userUuid);
        userService.update(user);
        userService.get(userUuid);
        verify(userService, times(2)).get(userUuid);
    }

    @Test
    void getByNameEvictAfterUpdate() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        User user = (User) user1Info.get(("user"));
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        user.setEnabled(false);

        userService.get("user1");
        userService.get("user1");
        userService.update(user);
        userService.get("user1");
        verify(userService, times(2)).get("user1");
    }

    @Test
    void getByNameAndUuidEvictAfterUpdate() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        User user = (User) user1Info.get(("user"));
        UUID userUuid = (UUID) user1Info.get("userUUID");
        when(userDtoRepository.get(userUuid))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        user.setEnabled(false);

        userService.get("user1");
        userService.get(userUuid);
        userService.get("user1");
        userService.get(userUuid);
        userService.update(user);
        userService.get("user1");
        userService.get(userUuid);
        verify(userService, times(2)).get("user1");
        verify(userService, times(2)).get(userUuid);
    }

    @Test
    void getEvictAfterDeleteByName() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        User user1 = (User) user1Info.get(("user"));
        UUID userUuid1 = (UUID) user1Info.get("userUUID");
        Map<String, Object> user2Info = UserBuilder.generateUser("user2", new HashSet<>(), "team", new HashSet<>());
        UUID userUuid2 = (UUID) user2Info.get("userUUID");
        when(userDtoRepository.get(userUuid1))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get(userUuid2))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get("user2"))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));

        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        userService.get("user2");
        userService.get(userUuid2);
        userService.delete(user1.getUsername());
        when(userDtoRepository.get(userUuid1))
                .thenReturn(Optional.empty());
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.empty());
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        verify(userService, times(2)).get("user1");
        verify(userService, times(2)).get(userUuid1);
        verify(userService, times(2)).get("user2");
        verify(userService, times(2)).get(userUuid2);
    }

    @Test
    void getEvictAfterDeleteByUuid() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        User user1 = (User) user1Info.get(("user"));
        UUID userUuid1 = (UUID) user1Info.get("userUUID");
        Map<String, Object> user2Info = UserBuilder.generateUser("user2", new HashSet<>(), "team", new HashSet<>());
        UUID userUuid2 = (UUID) user2Info.get("userUUID");
        when(userDtoRepository.get(userUuid1))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get(userUuid2))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get("user2"))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));

        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        userService.get("user2");
        userService.get(userUuid2);
        userService.delete(user1.getMetadataKey());
        when(userDtoRepository.get(userUuid1))
                .thenReturn(Optional.empty());
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.empty());
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        verify(userService, times(2)).get("user1");
        verify(userService, times(2)).get(userUuid1);
        verify(userService, times(2)).get("user2");
        verify(userService, times(2)).get(userUuid2);
    }

    @Test
    void getEvictAfterAddRole() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        User user1 = (User) user1Info.get(("user"));
        UUID userUuid1 = (UUID) user1Info.get("userUUID");
        Map<String, Object> user2Info = UserBuilder.generateUser("user2", new HashSet<>(), "team", new HashSet<>());
        UUID userUuid2 = (UUID) user2Info.get("userUUID");
        when(userDtoRepository.get(userUuid1))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get(userUuid2))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get("user2"))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));

        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        userService.get("user2");
        userService.get(userUuid2);
        userService.addRole(user1.getMetadataKey(), Role.builder()
                .userKeys(new HashSet<>())
                .privileges(new HashSet<>())
                .metadataKey(new RoleKey(UUID.randomUUID()))
                .teamKey(new TeamKey(UUID.randomUUID()))
                .name("role")
                .build());
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        verify(userService, times(2)).get("user1");
        verify(userService, times(2)).get(userUuid1);
        verify(userService, times(2)).get("user2");
        verify(userService, times(2)).get(userUuid2);
    }

    @Test
    void getEvictAfterRemoveRole() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        User user1 = (User) user1Info.get(("user"));
        UUID userUuid1 = (UUID) user1Info.get("userUUID");
        Map<String, Object> user2Info = UserBuilder.generateUser("user2", new HashSet<>(), "team", new HashSet<>());
        UUID userUuid2 = (UUID) user2Info.get("userUUID");
        when(userDtoRepository.get(userUuid1))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get(userUuid2))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get("user2"))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));

        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        userService.get("user2");
        userService.get(userUuid2);
        userService.removeRole(user1, Role.builder()
                .userKeys(new HashSet<>())
                .privileges(new HashSet<>())
                .metadataKey(new RoleKey(UUID.randomUUID()))
                .teamKey(new TeamKey(UUID.randomUUID()))
                .name("role")
                .build());
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        verify(userService, times(2)).get("user1");
        verify(userService, times(2)).get(userUuid1);
        verify(userService, times(1)).get("user2");
        verify(userService, times(1)).get(userUuid2);
    }

    @Test
    void getEvictAfterTeamUpdate() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        User user1 = (User) user1Info.get(("user"));
        UUID userUuid1 = (UUID) user1Info.get("userUUID");
        Map<String, Object> user2Info = UserBuilder.generateUser("user2", new HashSet<>(), "team", new HashSet<>());
        UUID userUuid2 = (UUID) user2Info.get("userUUID");
        when(userDtoRepository.get(userUuid1))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get(userUuid2))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get("user2"))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));

        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        userService.get("user2");
        userService.get(userUuid2);
        teamService.update(Team.builder()
                .teamKey(new TeamKey(UUID.randomUUID()))
                .teamName("team")
                .roles(new HashSet<>())
                .securityGroupKeys(new HashSet<>())
                .build());
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        verify(userService, times(2)).get("user1");
        verify(userService, times(2)).get(userUuid1);
        verify(userService, times(2)).get("user2");
        verify(userService, times(2)).get(userUuid2);
    }


    @Test
    void getEvictAfterTeamDeleteByKey() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        User user1 = (User) user1Info.get(("user"));
        UUID userUuid1 = (UUID) user1Info.get("userUUID");
        Map<String, Object> user2Info = UserBuilder.generateUser("user2", new HashSet<>(), "team", new HashSet<>());
        UUID userUuid2 = (UUID) user2Info.get("userUUID");
        when(userDtoRepository.get(userUuid1))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get(userUuid2))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get("user2"))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));

        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        userService.get("user2");
        userService.get(userUuid2);
        teamService.delete(new TeamKey(UUID.randomUUID()));
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        verify(userService, times(2)).get("user1");
        verify(userService, times(2)).get(userUuid1);
        verify(userService, times(2)).get("user2");
        verify(userService, times(2)).get(userUuid2);
    }


    @Test
    void getEvictAfterTeamDeleteByName() {
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", new HashSet<>(), "team", new HashSet<>());
        User user1 = (User) user1Info.get(("user"));
        UUID userUuid1 = (UUID) user1Info.get("userUUID");
        Map<String, Object> user2Info = UserBuilder.generateUser("user2", new HashSet<>(), "team", new HashSet<>());
        UUID userUuid2 = (UUID) user2Info.get("userUUID");
        when(userDtoRepository.get(userUuid1))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get(userUuid2))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));
        when(userDtoRepository.get("user1"))
                .thenReturn(Optional.of((UserDto) user1Info.get(("userDto"))));
        when(userDtoRepository.get("user2"))
                .thenReturn(Optional.of((UserDto) user2Info.get(("userDto"))));

        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        userService.get("user2");
        userService.get(userUuid2);
        teamService.delete("team");
        userService.get("user1");
        userService.get(userUuid1);
        userService.get("user2");
        userService.get(userUuid2);
        verify(userService, times(2)).get("user1");
        verify(userService, times(2)).get(userUuid1);
        verify(userService, times(2)).get("user2");
        verify(userService, times(2)).get(userUuid2);
    }

//    @Override
//    @CacheEvict(value = "users", allEntries = true)
//    public void addRole(TeamKey teamKey, Role role) {
//        rawTeamService.addRole(teamKey, role);
//    }
//
//    @Override
//    @CacheEvict(value = "users", allEntries = true)
//    public void deleteRole(TeamKey teamKey, RoleKey roleKey) {
//        rawTeamService.deleteRole(teamKey, roleKey);
//    }
//
//    @Override
//    @CacheEvict(value = "users", allEntries = true)
//    public void addSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
//        rawTeamService.addSecurityGroup(teamKey, securityGroupKey);
//    }
//
//    @Override
//    @CacheEvict(value = "users", allEntries = true)
//    public void removeSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
//        rawTeamService.removeSecurityGroup(teamKey, securityGroupKey);
//    }
//
//    @Override
//    @CacheEvict(value = "users", allEntries = true)
//    public void update(SecurityGroup securityGroup) {
//        rawSecurityGroupService.update(securityGroup);
//    }
//
//    @Override
//    @CacheEvict(value = "users", allEntries = true)
//    public void delete(SecurityGroupKey securityGroupKey) {
//        rawSecurityGroupService.delete(securityGroupKey);
//    }
//
//    @Override
//    @CacheEvict(value = "users", allEntries = true)
//    public void addTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
//        rawSecurityGroupService.addTeam(securityGroupKey, teamKey);
//    }
//
//    @Override
//    @CacheEvict(value = "users", allEntries = true)
//    public void deleteTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
//        rawSecurityGroupService.deleteTeam(securityGroupKey, teamKey);
//    }

}
