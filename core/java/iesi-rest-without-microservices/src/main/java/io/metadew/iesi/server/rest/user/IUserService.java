package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IUserService {

    Optional<UserDto> get(String username);

    Optional<UserDto> get(UUID uuid);

    Optional<UUID> getUuidByName(String username);

    Page<UserDto> getAll(Pageable pageable, Set<UserFilter> userFilters);

    List<User> getAll();

    boolean exists(UserKey userKey);

    boolean exists(String username);

    void addUser(User user);

    Optional<User> getRawUser(String username);
    Optional<User> getRawUser(UserKey userKey);

    void update(User user);

    void delete(UserKey userKey);

    void delete(String username);

    Set<Privilege> getPrivileges(UserKey userKey);

    Set<Role> getRoles(UserKey userKey);

    Set<Team> getTeams(UserKey userKey);

    void addRole(UserKey user, Role role);

    void removeRole(User user, Role role);

}
