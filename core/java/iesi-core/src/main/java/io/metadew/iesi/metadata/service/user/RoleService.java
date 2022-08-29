package io.metadew.iesi.metadata.service.user;


import io.metadew.iesi.metadata.configuration.user.RoleConfiguration;
import io.metadew.iesi.metadata.definition.user.*;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

public class RoleService {

    private static RoleService instance;

    public static synchronized RoleService getInstance() {
        if (instance == null) {
            instance = new RoleService();
        }
        return instance;
    }

    private RoleService() {
    }

    public List<Role> getAll() {
        return RoleConfiguration.getInstance().getAll();
    }
    public List<Role> getByTeamId(TeamKey teamKey) { return RoleConfiguration.getInstance().getByTeamId(teamKey);}

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

    public Role convertDefaultRole(IESIRole iesiRole, TeamKey teamKey) {
        RoleKey roleKey = new RoleKey(UUID.randomUUID());
        return Role.builder()
                .metadataKey(roleKey)
                .teamKey(teamKey)
                .users(new HashSet<>())
                .name(iesiRole.getName())
                .privileges(
                        iesiRole.getIesiPrivileges().stream().map(
                                iesiPrivilege -> Privilege.builder()
                                        .privilegeKey(new PrivilegeKey(UUID.randomUUID()))
                                        .roleKey(roleKey)
                                        .privilege(iesiPrivilege.getPrivilege())
                                        .build()
                        ).collect(Collectors.toSet())
                ).build();
    }

}
