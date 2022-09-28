package io.metadew.iesi.metadata.service.user;

import io.metadew.iesi.metadata.configuration.user.UserConfiguration;
import io.metadew.iesi.metadata.definition.user.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Log4j2
public class UserService {

    private final UserConfiguration userConfiguration;

    public UserService(UserConfiguration userConfiguration) {
        this.userConfiguration = userConfiguration;
    }

    public List<User> getAll() {
        return userConfiguration.getAll();
    }

    public boolean exists(UserKey userKey) {
        return userConfiguration.exists(userKey);
    }

    public boolean exists(String username) {
        return userConfiguration.exists(username);
    }

    public void addUser(User user) {
        userConfiguration.insert(user);
    }

    public Optional<User> get(UserKey userKey) {
        return userConfiguration.get(userKey);
    }

    public Optional<UUID> getUuidByName(String username) {
        return userConfiguration.getUuidByName(username);
    }

    public Optional<User> get(String username) {
        return userConfiguration.getByName(username);
    }

    public void update(User user) {
        userConfiguration.update(user);
    }

    public void updatePassword(String password, UserKey userKey) {
        userConfiguration.updatePassword(password, userKey);
    }

    public void delete(UserKey userKey) {
        userConfiguration.delete(userKey);
    }

    public void delete(String username) {
        userConfiguration.delete(username);
    }

    public Set<Privilege> getPrivileges(UserKey userKey) {
        return userConfiguration.getPrivileges(userKey);
    }

    public Set<Role> getRoles(UserKey userKey) {
        return userConfiguration.getRoles(userKey);
    }

    public Set<Team> getTeams(UserKey userKey) {
        return userConfiguration.getTeams(userKey);
    }

    public void addRole(UserKey user, Role role) {
        userConfiguration.addRole(user, role.getMetadataKey());
    }

    public void removeRole(User user, Role role) {
        userConfiguration.removeRole(user.getMetadataKey(), role.getMetadataKey());
    }

}
