package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.ClockConfiguration;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
@DirtiesContext
class UserServiceCachingTest {

    @SpyBean
    private UserService userService;

    @MockBean
    private UserDtoRepository userDtoRepository;

    @MockBean
    private io.metadew.iesi.metadata.service.user.UserService rawUserService;

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


//
//    @Cacheable("users")
//    public Optional<UserDto> get(UUID uuid) {
//        return userDtoRepository.get(uuid);
//    }
//
//    public Page<UserDto> getAll(Pageable pageable, Set<UserFilter> userFilters) {
//        return userDtoRepository.getAll(pageable, userFilters);
//    }
//
//    @Override
//    public List<User> getAll() {
//        return rawUserService.getAll();
//    }
//
//    @Override
//    public boolean exists(UserKey userKey) {
//        return rawUserService.exists(userKey);
//    }
//
//    @Override
//    public boolean exists(String username) {
//        return rawUserService.exists(username);
//    }
//
//    @Override
//    public void addUser(User user) {
//        rawUserService.addUser(user);
//    }
//
//    @Override
//    public Optional<User> getRawUser(String username) {
//        return rawUserService.get(username);
//    }
//
//    @Override
//    @Caching(evict = {
//            @CacheEvict(value = "users", key = "#user.userKey.uuid"),
//            @CacheEvict(value = "users", key = "#user.username")})
//    public void update(User user) {
//        rawUserService.update(user);
//    }
//
//    @Override
//    @CacheEvict(value = "users", key = "#user.userKey.uuid")
//    public void delete(UserKey userKey) {
//        rawUserService.delete(userKey);
//    }
//
//    @Override
//    @CacheEvict(value = "users")
//    public void delete(String username) {
//        rawUserService.delete(username);
//    }
//
//    @Override
//    public Set<Privilege> getPrivileges(UserKey userKey) {
//        return rawUserService.getPrivileges(userKey);
//    }
//
//    @Override
//    public Set<Role> getRoles(UserKey userKey) {
//        return rawUserService.getRoles(userKey);
//    }
//
//    @Override
//    public Set<Team> getTeams(UserKey userKey) {
//        return rawUserService.getTeams(userKey);
//    }
//
//    @Override
//    @CacheEvict(value = "users", key = "#user.userKey.uuid")
//    public void addRole(UserKey user, Role role) {
//        rawUserService.addRole(user, role);
//    }
//
//    @Override
//    @CacheEvict(value = "users", key = "#user.userKey.uuid")
//    public void removeRole(User user, Role role) {
//        rawUserService.removeRole(user, role);
//    }

}
