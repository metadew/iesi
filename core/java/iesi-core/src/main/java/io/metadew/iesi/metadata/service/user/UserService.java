package io.metadew.iesi.metadata.service.user;

import io.metadew.iesi.metadata.configuration.user.UserConfiguration;
import io.metadew.iesi.metadata.definition.user.*;
<<<<<<< HEAD

import java.util.List;
import java.util.Optional;

public class UserService {

    private static UserService INSTANCE;

    public synchronized static UserService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserService();
        }
        return INSTANCE;
=======
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
public class UserService {

    private static UserService instance;

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
>>>>>>> master
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
<<<<<<< HEAD
        return UserConfiguration.getInstance().get(username);
=======
        return UserConfiguration.getInstance().getByName(username);
>>>>>>> master
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

<<<<<<< HEAD
    public List<Authority> getAuthorities(UserKey userKey) {
        return UserConfiguration.getInstance().getAuthorities(userKey);
    }

    public List<Authority> getAuthorities(String username) {
        return UserConfiguration.getInstance().getAuthorities(username);
    }

    public List<Group> getGroups(UserKey userKey) {
        return UserConfiguration.getInstance().getGroups(userKey);
    }

    public List<Group> getGroups(String username) {
        return UserConfiguration.getInstance().getGroups(username);
    }

    public void addAuthority(UserKey user, AuthorityKey authority) {
        UserConfiguration.getInstance().addAuthority(user, authority);
    }

    public void addAuthority(String username, String authority) {
        UserConfiguration.getInstance().addAuthority(username, authority);
    }

    public void removeAuthority(User user, Authority authority) {
        UserConfiguration.getInstance().removeAuthority(user.getMetadataKey(), authority.getMetadataKey());
    }

    public void removeAuthority(String username, String authority) {
        UserConfiguration.getInstance().removeAuthority(username, authority);
=======
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
>>>>>>> master
    }

}
