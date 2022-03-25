package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.server.rest.user.role.PrivilegeDto;
import io.metadew.iesi.server.rest.user.role.RoleDto;
import io.metadew.iesi.server.rest.user.role.RoleUserDto;
import lombok.Data;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class TeamDtoListResultSetExtractor {

    //             "teams.ID as team_id, teams.TEAM_NAME as team_name, " +
    //            "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
    //            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
    //            "users.ID as user_id, users.USERNAME as user_username, " +
    //            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
    //            "security_groups.ID as security_group_id, security_groups.NAME as security_group_name, " +

    public List<TeamDto> extractData(CachedRowSet rs) throws SQLException {
        Map<UUID, TeamDtoBuilder> teamsMap = new LinkedHashMap<>();
        while (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString("team_id"));
            TeamDtoBuilder teamDtoBuilder = teamsMap.computeIfAbsent(
                    uuid,
                    k -> mapRow(rs)
            );
            addRole(teamDtoBuilder, rs);
            addSecurityGroups(teamDtoBuilder, rs);
        }
        return teamsMap.values().stream().map(TeamDtoBuilder::build).collect(Collectors.toList());
    }

    private TeamDtoBuilder mapRow(CachedRowSet cachedRowSet) {
        try {
            return new TeamDtoBuilder(
                    UUID.fromString(cachedRowSet.getString("team_id")),
                    cachedRowSet.getString("team_name"),
                    new HashSet<>(),
                    new HashMap<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addSecurityGroups(TeamDtoBuilder teamDtoBuilder, CachedRowSet cachedRowSet) throws SQLException {
        if (cachedRowSet.getString("security_group_id") != null) {
            teamDtoBuilder.getSecurityGroups().add(
                    new TeamSecurityGroupDto(
                            UUID.fromString(cachedRowSet.getString("security_group_id")),
                            cachedRowSet.getString("security_group_name")
                    )
            );
        }
    }

    private void addRole(TeamDtoBuilder teamDtoBuilder, CachedRowSet cachedRowSet) throws SQLException {
        if (cachedRowSet.getString("role_id") != null) {
            RoleDtoBuilder roleDtoBuilder = teamDtoBuilder.getRoles().computeIfAbsent(
                    UUID.fromString(cachedRowSet.getString("role_id")),
                    k -> mapRowToRoleDtoBuilder(cachedRowSet)
            );
            addPrivilege(roleDtoBuilder, cachedRowSet);
            addUser(roleDtoBuilder, cachedRowSet);
        }
    }

    private void addUser(RoleDtoBuilder roleDtoBuilder, CachedRowSet cachedRowSet) throws SQLException {
        if (cachedRowSet.getString("user_id") != null) {
            roleDtoBuilder.getUsers().add(
                    new RoleUserDto(
                            UUID.fromString(cachedRowSet.getString("user_id")),
                            cachedRowSet.getString("user_username"),
                            SQLTools.getBooleanFromSql(cachedRowSet.getString("user_enabled")),
                            SQLTools.getBooleanFromSql(cachedRowSet.getString("user_expired")),
                            SQLTools.getBooleanFromSql(cachedRowSet.getString("user_credentials_expired")),
                            SQLTools.getBooleanFromSql(cachedRowSet.getString("user_locked"))
                    )
            );
        }

    }

    private void addPrivilege(RoleDtoBuilder roleDtoBuilder, CachedRowSet cachedRowSet) throws SQLException {
        if (cachedRowSet.getString("privilege_id") != null) {
            roleDtoBuilder.privileges.add(
                    new PrivilegeDto(
                            UUID.fromString(cachedRowSet.getString("privilege_id")),
                            cachedRowSet.getString("privilege_privilege")
                    )
            );
        }
    }

    private RoleDtoBuilder mapRowToRoleDtoBuilder(CachedRowSet cachedRowSet) {
        try {
            return new RoleDtoBuilder(
                    UUID.fromString(cachedRowSet.getString("role_id")),
                    cachedRowSet.getString("role_role_name"),
                    new HashSet<>(),
                    new HashSet<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    private static class TeamDtoBuilder {

        private final UUID id;
        private final String teamName;
        private Set<TeamSecurityGroupDto> securityGroups;
        private Map<UUID, RoleDtoBuilder> roles;

        private TeamDtoBuilder(UUID id, String teamName, Set<TeamSecurityGroupDto> securityGroups, Map<UUID, RoleDtoBuilder> roles) {
            this.id = id;
            this.teamName = teamName;
            this.securityGroups = securityGroups;
            this.roles = roles;
        }

        public TeamDto build() {
            return new TeamDto(
                    id,
                    teamName,
                    securityGroups,
                    roles.values().stream().map(RoleDtoBuilder::build).collect(Collectors.toSet())
            );
        }

    }

    @Data
    private static class RoleDtoBuilder {

        private final UUID id;
        private final String name;
        private Set<PrivilegeDto> privileges;
        private Set<RoleUserDto> users;

        private RoleDtoBuilder(UUID id, String name, Set<PrivilegeDto> privileges, Set<RoleUserDto> users) {
            this.id = id;
            this.name = name;
            this.privileges = privileges;
            this.users = users;
        }

        public RoleDto build() {
            return new RoleDto(
                    id,
                    name,
                    privileges,
                    users
            );
        }
    }

}
