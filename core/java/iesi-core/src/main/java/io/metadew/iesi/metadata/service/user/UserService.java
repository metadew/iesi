package io.metadew.iesi.metadata.service.user;

import io.metadew.iesi.metadata.configuration.user.UserConfiguration;
import io.metadew.iesi.metadata.definition.user.*;

import java.util.List;
import java.util.Optional;

public class UserService {

    private static UserService INSTANCE;

    public synchronized static UserService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserService();
        }
        return INSTANCE;
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
        return UserConfiguration.getInstance().get(username);
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

    public List<Privilege> getAuthorities(UserKey userKey) {
        return UserConfiguration.getInstance().getAuthorities(userKey);
    }

    public List<Privilege> getAuthorities(String username) {
        return UserConfiguration.getInstance().getAuthorities(username);
    }

    public List<Team> getGroups(UserKey userKey) {
        return UserConfiguration.getInstance().getGroups(userKey);
    }

    public List<Team> getGroups(String username) {
        return UserConfiguration.getInstance().getGroups(username);
    }

    public void addAuthority(UserKey user, PrivilegeKey authority) {
        UserConfiguration.getInstance().addAuthority(user, authority);
    }

    public void addAuthority(String username, String authority) {
        UserConfiguration.getInstance().addAuthority(username, authority);
    }

    public void removeAuthority(User user, Privilege privilege) {
        UserConfiguration.getInstance().removeAuthority(user.getMetadataKey(), privilege.getMetadataKey());
    }

    public void removeAuthority(String username, String authority) {
        UserConfiguration.getInstance().removeAuthority(username, authority);
    }

}
