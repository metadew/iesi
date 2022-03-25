package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.user.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class RoleListResultSetExtractor {

    public List<Role> extractData(CachedRowSet cachedRowSet) throws SQLException {
        Map<String, RoleBuilder> userMap = new HashMap<>();
        while (cachedRowSet.next()) {
            RoleBuilder roleBuilder = userMap.get(cachedRowSet.getString("role_id"));
            if (roleBuilder == null) {
                roleBuilder = new RoleBuilder(
                        UUID.fromString(cachedRowSet.getString("role_id")),
                        UUID.fromString(cachedRowSet.getString("role_team_id")),
                        cachedRowSet.getString("role_role_name"),
                        new HashSet<>(),
                        new HashMap<>()
                );
                userMap.put(
                        cachedRowSet.getString("role_id"),
                        roleBuilder
                );
            }
            addUserId(roleBuilder, cachedRowSet);
            addPrivilege(roleBuilder, cachedRowSet);
        }
        return userMap.values().stream().map(RoleBuilder::build).collect(Collectors.toList());
    }

    private void addUserId(RoleBuilder roleBuilder, CachedRowSet cachedRowSet) throws SQLException {
        String userId = cachedRowSet.getString("user_role_user_id");
        if (userId != null) {
            User user = UserConfiguration.getInstance().get(new UserKey(UUID.fromString(userId)))
                            .orElseThrow(() -> new MetadataDoesNotExistException(new UserKey(UUID.fromString(userId))));
            roleBuilder.getUsers().add(user);
        }
    }


    private void addPrivilege(RoleBuilder roleBuilder, CachedRowSet cachedRowSet) throws SQLException {
        // privileges.id as privilege_id, privileges.role_id as privilege_role_id, privilege.privilege as privilege_privilege,
        if (cachedRowSet.getString("privilege_id") != null) {
            PrivilegeBuilder privilegeBuilder = roleBuilder.getPrivilegeMap().get(cachedRowSet.getString("privilege_id"));
            if (privilegeBuilder == null) {
                privilegeBuilder = new PrivilegeBuilder(
                        UUID.fromString(cachedRowSet.getString("privilege_id")),
                        UUID.fromString(cachedRowSet.getString("privilege_role_id")),
                        cachedRowSet.getString("privilege_privilege")
                );
                roleBuilder.getPrivilegeMap().put(
                        cachedRowSet.getString("privilege_id"),
                        privilegeBuilder
                );
            }
        }
    }

    @AllArgsConstructor
    @Getter
    public static class RoleBuilder {

        private UUID roleId;
        private UUID teamId;
        private String name;
        private final Set<User> users;
        private final Map<String, PrivilegeBuilder> privilegeMap;

        public Role build() {
            return new Role(
                    new RoleKey(roleId),
                    name,
                    new TeamKey(teamId),
                    privilegeMap.values().stream()
                            .map(PrivilegeBuilder::build)
                            .collect(Collectors.toSet()),
                    users);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class PrivilegeBuilder {


        private UUID privilegeId;
        private UUID roleId;
        private String privilege;

        public Privilege build() {
            return new Privilege(new PrivilegeKey(privilegeId), privilege, new RoleKey(roleId));
        }
    }

}
