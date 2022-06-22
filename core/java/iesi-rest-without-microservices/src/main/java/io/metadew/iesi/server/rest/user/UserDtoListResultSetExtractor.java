package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.server.rest.user.role.PrivilegeDto;
import io.metadew.iesi.server.rest.user.role.RoleTeamDto;
import io.metadew.iesi.server.rest.user.team.TeamSecurityGroupDto;
import lombok.Data;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class UserDtoListResultSetExtractor {

    //             "users.ID as user_id, users.USERNAME as user_username, " +
    //            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
    //            "roles.ID as role_id, roles.role_name as role_role_name, " +
    //            "privileges.ID as privilege_id, privileges.privilege as privilege_privilege, " +
    //            "teams.ID as team_id, teams.NAME as team_name " +

    public List<UserDto> extractData(CachedRowSet rs) throws SQLException {
        Map<UUID, UserDtoBuilder> userMap = new HashMap<>();
        while (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString("user_id"));
            UserDtoBuilder userDtoBuilder = userMap.computeIfAbsent(
                    uuid,
                    k -> mapRow(rs)
            );
            addRole(userDtoBuilder, rs);
        }
        return userMap.values().stream().map(UserDtoBuilder::build).collect(Collectors.toList());
    }

    private UserDtoBuilder mapRow(CachedRowSet cachedRowSet) {
        try {
            return new UserDtoBuilder(
                    UUID.fromString(cachedRowSet.getString("user_id")),
                    cachedRowSet.getString("user_username"),
                    SQLTools.getBooleanFromSql(cachedRowSet.getString("user_enabled")),
                    SQLTools.getBooleanFromSql(cachedRowSet.getString("user_expired")),
                    SQLTools.getBooleanFromSql(cachedRowSet.getString("user_credentials_expired")),
                    SQLTools.getBooleanFromSql(cachedRowSet.getString("user_locked")),
                    new HashMap<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addRole(UserDtoBuilder userDtoBuilder, CachedRowSet cachedRowSet) throws SQLException {;
        if (cachedRowSet.getString("role_id") != null) {
            UserRoleDtoBuilder userRoleDtoBuilder = userDtoBuilder.getRoles().computeIfAbsent(
                    UUID.fromString(cachedRowSet.getString("role_id")),
                    k -> mapRowToUserRoleDtoBuilder(cachedRowSet)
            );
            addPrivilege(userRoleDtoBuilder, cachedRowSet);
            addTeamSecurityGroup(userRoleDtoBuilder, cachedRowSet);
        }
    }

    private void addTeamSecurityGroup(UserRoleDtoBuilder userRoleDtoBuilder, CachedRowSet cachedRowSet) throws SQLException {
        if (cachedRowSet.getString("security_group_id") != null) {
            userRoleDtoBuilder.getTeam().getSecurityGroups().add(
                    new TeamSecurityGroupDto(
                            UUID.fromString(cachedRowSet.getString("security_group_id")),
                            cachedRowSet.getString("security_group_name")
                    )
            );
        }
    }

    private void addPrivilege(UserRoleDtoBuilder userRoleDtoBuilder, CachedRowSet cachedRowSet) throws SQLException {
        if (cachedRowSet.getString("privilege_id") != null) {
            userRoleDtoBuilder.privileges.add(
                    new PrivilegeDto(
                            UUID.fromString(cachedRowSet.getString("privilege_id")),
                            cachedRowSet.getString("privilege_privilege")
                    )
            );
        }
    }

    private UserRoleDtoBuilder mapRowToUserRoleDtoBuilder(CachedRowSet cachedRowSet) {
        try {

            return new UserRoleDtoBuilder(
                    UUID.fromString(cachedRowSet.getString("role_id")),
                    cachedRowSet.getString("role_role_name"),
                    new RoleTeamDto(
                            UUID.fromString(cachedRowSet.getString("team_id")),
                            cachedRowSet.getString("team_name"),
                            new HashSet<>()
                    ),
                    new HashSet<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    private static class UserDtoBuilder {

        private final UUID id;
        private final String userName;
        private final boolean enabled;
        private final boolean expired;
        private final boolean credentialsExpired;
        private final boolean locked;
        private Map<UUID, UserRoleDtoBuilder> roles;

        private UserDtoBuilder(UUID id, String userName, boolean enabled, boolean expired, boolean credentialsExpired, boolean locked, Map<UUID, UserRoleDtoBuilder> roles) {
            this.id = id;
            this.userName = userName;
            this.enabled = enabled;
            this.expired = expired;
            this.credentialsExpired = credentialsExpired;
            this.locked = locked;
            this.roles = roles;
        }

        public UserDto build() {
            return new UserDto(
                    id,
                    userName,
                    enabled,
                    expired,
                    credentialsExpired,
                    locked,
                    roles.values().stream().map(UserRoleDtoBuilder::build).collect(Collectors.toSet())
            );
        }

    }

    @Data
    private static class UserRoleDtoBuilder {

        private final UUID id;
        private final String name;
        private final RoleTeamDto team;
        private Set<PrivilegeDto> privileges;

        private UserRoleDtoBuilder(UUID id, String name, RoleTeamDto team, Set<PrivilegeDto> privileges) {
            this.id = id;
            this.name = name;
            this.team = team;
            this.privileges = privileges;
        }


        public UserRoleDto build() {
            return new UserRoleDto(
                    id,
                    name,
                    team,
                    privileges
            );
        }
    }

}
