package io.metadew.iesi.metadata.service.user;


import io.metadew.iesi.metadata.configuration.user.RoleConfiguration;
import io.metadew.iesi.metadata.definition.user.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleConfiguration roleConfiguration;

    public RoleService(RoleConfiguration roleConfiguration) {
        this.roleConfiguration = roleConfiguration;
    }

    public List<Role> getAll() {
        return roleConfiguration.getAll();
    }
    public List<Role> getByTeamId(TeamKey teamKey) { return roleConfiguration.getByTeamId(teamKey);}

    public boolean exists(RoleKey roleKey) {
        return roleConfiguration.exists(roleKey);
    }

    public void addRole(Role role) {
        roleConfiguration.insert(role);
    }

    public Optional<Role> get(RoleKey roleKey) {
        return roleConfiguration.get(roleKey);
    }

    public void update(Role role) {
        roleConfiguration.update(role);
    }

    public void delete(RoleKey roleKey) {
        roleConfiguration.delete(roleKey);
    }

    public Set<User> getUsers(RoleKey roleKey) {
        return new HashSet<>(roleConfiguration.getUsers(roleKey));
    }

    public void addUser(RoleKey roleKey, UserKey userKey) {
        roleConfiguration.addUser(roleKey, userKey);
    }

    public void removeUser(RoleKey roleKey, UserKey userKey) {
        roleConfiguration.removeUser(roleKey, userKey);
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
