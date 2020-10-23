package io.metadew.iesi.metadata.service.user;


import io.metadew.iesi.metadata.configuration.user.RoleConfiguration;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.RoleKey;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RoleService {

    private static RoleService INSTANCE;

    public static synchronized RoleService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RoleService();
        }
        return INSTANCE;
    }

    private RoleService() {
    }

    public List<Role> getAll() {
        return RoleConfiguration.getInstance().getAll();
    }

    public boolean exists(RoleKey roleKey) {
        return RoleConfiguration.getInstance().exists(roleKey);
    }

    public void addRole(Role role) {
        RoleConfiguration.getInstance().insert(role);
    }

    public Optional<Role> get(RoleKey roleKey) {
        return RoleConfiguration.getInstance().get(roleKey);
    }

    public void update(Role role) {
        RoleConfiguration.getInstance().update(role);
    }

    public void delete(RoleKey roleKey) {
        RoleConfiguration.getInstance().delete(roleKey);
    }

    public Set<User> getUsers(RoleKey roleKey) {
        return new HashSet<>(RoleConfiguration.getInstance().getUsers(roleKey));
    }

    public void addUser(RoleKey roleKey, UserKey userKey) {
        RoleConfiguration.getInstance().addUser(roleKey, userKey);
    }

    public void removeUser(RoleKey roleKey, UserKey userKey) {
        RoleConfiguration.getInstance().removeUser(roleKey, userKey);
    }

}
