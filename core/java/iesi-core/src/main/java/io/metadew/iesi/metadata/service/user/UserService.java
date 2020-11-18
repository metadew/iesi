package io.metadew.iesi.metadata.service.user;

import io.metadew.iesi.metadata.configuration.user.UserConfiguration;
import io.metadew.iesi.metadata.definition.user.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserService {

    private static UserService instance;

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private UserService() {
    }

    public List<User> getAll() {
        return UserConfiguration.getInstance().getAll();
    }

    public boolean exists(UserKey userKey) {
        return UserConfiguration.getInstance().exists(userKey);
    }

    public boolean exists(String username) {
        return UserConfiguration.getInstance().exists(username);
    }

    public void addUser(User user) {
        UserConfiguration.getInstance().insert(user);
    }

    public Optional<User> get(UserKey userKey) {
        return UserConfiguration.getInstance().get(userKey);
    }

    public Optional<User> get(String username) {
        return UserConfiguration.getInstance().getByName(username);
    }

    public void update(User user) {
        UserConfiguration.getInstance().update(user);
    }

    public void delete(UserKey userKey) {
        UserConfiguration.getInstance().delete(userKey);
    }

    public void delete(String username) {
        UserConfiguration.getInstance().delete(username);
    }

    public Set<Privilege> getPrivileges(UserKey userKey) {
        return UserConfiguration.getInstance().getPrivileges(userKey);
    }

    public Set<Role> getRoles(UserKey userKey) {
        return UserConfiguration.getInstance().getRoles(userKey);
    }

    public Set<Team> getTeams(UserKey userKey) {
        return UserConfiguration.getInstance().getTeams(userKey);
    }

    public void addRole(UserKey user, Role role) {
        UserConfiguration.getInstance().addRole(user, role.getMetadataKey());
    }

    public void removeRole(User user, Role role) {
        UserConfiguration.getInstance().removeRole(user.getMetadataKey(), role.getMetadataKey());
    }

}
