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

    public List<User> getAll() throws Exception {
        return UserConfiguration.getInstance().getAll();
    }

    public boolean exists(UserKey userKey) throws Exception {
        return UserConfiguration.getInstance().exists(userKey);
    }

    public boolean exists(String username) throws Exception {
        return UserConfiguration.getInstance().exists(username);
    }

    public void addUser(User user) throws Exception {
        UserConfiguration.getInstance().insert(user);
    }

    public Optional<User> get(UserKey userKey) throws Exception {
        return UserConfiguration.getInstance().get(userKey);
    }

    public Optional<User> get(String username) throws Exception {
        return UserConfiguration.getInstance().get(username);
    }

    public void update(User user) throws Exception {
        UserConfiguration.getInstance().update(user);
    }

    public void delete(UserKey userKey) throws Exception {
        UserConfiguration.getInstance().delete(userKey);
    }

    public void delete(String username) throws Exception {
        UserConfiguration.getInstance().delete(username);
    }

    public List<Authority> getAuthorities(UserKey userKey) throws Exception {
        return UserConfiguration.getInstance().getAuthorities(userKey);
    }

    public List<Authority> getAuthorities(String username) throws Exception {
        return UserConfiguration.getInstance().getAuthorities(username);
    }

    public List<Group> getGroups(UserKey userKey) throws Exception {
        return UserConfiguration.getInstance().getGroups(userKey);
    }

    public List<Group> getGroups(String username) throws Exception {
        return UserConfiguration.getInstance().getGroups(username);
    }

    public void addAuthority(UserKey user, AuthorityKey authority) throws Exception {
        UserConfiguration.getInstance().addAuthority(user, authority);
    }

    public void addAuthority(String username, String authority) throws Exception {
        UserConfiguration.getInstance().addAuthority(username, authority);
    }

    public void removeAuthority(User user, Authority authority) throws Exception {
        UserConfiguration.getInstance().removeAuthority(user.getMetadataKey(), authority.getMetadataKey());
    }

    public void removeAuthority(String username, String authority) throws Exception {
        UserConfiguration.getInstance().removeAuthority(username, authority);
    }

}
