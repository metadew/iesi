package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service("restUserService")
public class UserService implements IUserService {

    private final UserDtoRepository userDtoRepository;

    private final io.metadew.iesi.metadata.service.user.UserService rawUserService;

    @Autowired
    public UserService(UserDtoRepository userDtoRepository, io.metadew.iesi.metadata.service.user.UserService rawUserService) {
        this.userDtoRepository = userDtoRepository;
        this.rawUserService = rawUserService;
    }

    public Optional<UUID> getUuidByName(String username) {
        return rawUserService.getUuidByName(username);
    }

    @Cacheable("users")
    public Optional<UserDto> get(String username) {
        return userDtoRepository.get(username);
    }

    @Cacheable("users")
    public Optional<UserDto> get(UUID uuid) {
        return userDtoRepository.get(uuid);
    }

    public Page<UserDto> getAll(Pageable pageable, Set<UserFilter> userFilters) {
        Page<UserDto> page = userDtoRepository.getAll(pageable, userFilters);
        return page;
    }

    @Override
    public List<User> getAll() {
        return rawUserService.getAll();
    }

    @Override
    public boolean exists(UserKey userKey) {
        return rawUserService.exists(userKey);
    }

    @Override
    public boolean exists(String username) {
        return rawUserService.exists(username);
    }

    @Override
    public void addUser(User user) {
        rawUserService.addUser(user);
    }

    @Override
    public Optional<User> getRawUser(String username) {
        return rawUserService.get(username);
    }

    // If the name of a user is modified, the wrong record is evicted and the old, stale record
    // stays in the cache
    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#user.metadataKey.uuid"),
            @CacheEvict(value = "users", key = "#user.username")})
    public void update(User user) {
        rawUserService.update(user);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void delete(UserKey userKey) {
        rawUserService.delete(userKey);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void delete(String username) {
        rawUserService.delete(username);
    }

    @Override
    public Set<Privilege> getPrivileges(UserKey userKey) {
        return rawUserService.getPrivileges(userKey);
    }

    @Override
    public Set<Role> getRoles(UserKey userKey) {
        return rawUserService.getRoles(userKey);
    }

    @Override
    public Set<Team> getTeams(UserKey userKey) {
        return rawUserService.getTeams(userKey);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void addRole(UserKey user, Role role) {
        rawUserService.addRole(user, role);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#user.metadataKey.uuid"),
            @CacheEvict(value = "users", key = "#user.username")})
    public void removeRole(User user, Role role) {
        rawUserService.removeRole(user, role);
    }

}
