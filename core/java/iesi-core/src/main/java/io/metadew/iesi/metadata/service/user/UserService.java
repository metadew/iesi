package io.metadew.iesi.metadata.service.user;

import io.metadew.iesi.metadata.configuration.user.UserConfiguration;
import io.metadew.iesi.metadata.definition.user.Authority;
import io.metadew.iesi.metadata.definition.user.Group;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;

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

    public void addUser(User user) {
        UserConfiguration.getInstance().insert(user);
    }

    public Optional<User> get(UserKey userKey) {
        return UserConfiguration.getInstance().get(userKey);
    }

    public void update(User user) {
        UserConfiguration.getInstance().update(user);
    }

    public void delete(User user) {
        UserConfiguration.getInstance().delete(user.getMetadataKey());
    }

    public List<Authority> getAuthorities(User user) {
        return UserConfiguration.getInstance().getAuthorities(user.getMetadataKey());
    }

    public List<Group> getGroups(User user) {
        return UserConfiguration.getInstance().getGroups(user.getMetadataKey());
    }

    public void addAuthority(User user, Authority authority) {
        UserConfiguration.getInstance().addAuthority(user.getMetadataKey(), authority.getMetadataKey());
    }

    public void removeAuthority(User user, Authority authority) {
        UserConfiguration.getInstance().removeAuthority(user.getMetadataKey(), authority.getMetadataKey());
    }

}
