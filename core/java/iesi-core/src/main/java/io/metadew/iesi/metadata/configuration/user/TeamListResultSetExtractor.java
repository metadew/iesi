package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TeamListResultSetExtractor {

    private final SecurityGroupConfiguration securityGroupConfiguration;
    private final UserConfiguration userConfiguration;

    public TeamListResultSetExtractor(SecurityGroupConfiguration securityGroupConfiguration, UserConfiguration userConfiguration) {
        this.securityGroupConfiguration = securityGroupConfiguration;
        this.userConfiguration = userConfiguration;
    }

    public List<Team> extractData(CachedRowSet rs) throws SQLException {
        Map<UUID, TeamBuilder> teamBuilderMap = new HashMap<>();
        TeamBuilder teamBuilder;
        while (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString("team_id"));
            teamBuilder = teamBuilderMap.get(uuid);
            if (teamBuilder == null) {
                teamBuilder = mapRow(rs);
                teamBuilderMap.put(uuid, teamBuilder);
            }
            addRole(teamBuilder, rs);
            addSecurityGroup(teamBuilder, rs);
        }
        return teamBuilderMap.values().stream()
                .map(TeamBuilder::build)
                .collect(Collectors.toList());
    }

    private TeamBuilder mapRow(CachedRowSet cachedRowSet) throws SQLException {
        return new TeamBuilder(UUID.fromString(cachedRowSet.getString("team_id")),
                cachedRowSet.getString("team_name"),
                new HashSet<>(),
                new HashMap<>());
    }

    private void addSecurityGroup(TeamBuilder teamBuilder, CachedRowSet cachedRowSet) throws SQLException {
        String securityGroupId = cachedRowSet.getString("security_group_id");
        if (securityGroupId != null) {
            SecurityGroup securityGroup = securityGroupConfiguration.get(new SecurityGroupKey(UUID.fromString(securityGroupId)))
                            .orElseThrow(() -> new MetadataDoesNotExistException(new SecurityGroupKey(UUID.fromString(securityGroupId))));
            teamBuilder.getSecurityGroups().add(securityGroup);
        }
    }

    private void addRole(TeamBuilder teamBuilder, CachedRowSet cachedRowSet) throws SQLException {
        if (cachedRowSet.getString("role_id") != null) {
            RoleListResultSetExtractor.RoleBuilder roleBuilder = teamBuilder.getRoleBuilders().get(cachedRowSet.getString("role_id"));
            if (roleBuilder == null) {
                roleBuilder = new RoleListResultSetExtractor.RoleBuilder(
                        UUID.fromString(cachedRowSet.getString("role_id")),
                        UUID.fromString(cachedRowSet.getString("role_team_id")),
                        cachedRowSet.getString("role_role_name"),
                        new HashSet<>(),
                        new HashMap<>()
                );
                teamBuilder.getRoleBuilders().put(
                        cachedRowSet.getString("role_id"),
                        roleBuilder
                );
            }
            addUserId(roleBuilder, cachedRowSet);
            addPrivilege(roleBuilder, cachedRowSet);
        }
    }

    private void addPrivilege(RoleListResultSetExtractor.RoleBuilder roleBuilder, CachedRowSet cachedRowSet) throws SQLException {
        if (cachedRowSet.getString("privilege_id") != null) {
            RoleListResultSetExtractor.PrivilegeBuilder privilegeBuilder = roleBuilder.getPrivilegeMap().get(cachedRowSet.getString("privilege_id"));
            if (privilegeBuilder == null) {
                privilegeBuilder = new RoleListResultSetExtractor.PrivilegeBuilder(
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

    private void addUserId(RoleListResultSetExtractor.RoleBuilder roleBuilder, CachedRowSet cachedRowSet) throws SQLException {
        String userId = cachedRowSet.getString("user_role_user_id");
        if (userId != null) {
            User user = userConfiguration.get(new UserKey(UUID.fromString(userId)))
                    .orElseThrow(() -> new MetadataDoesNotExistException(new UserKey(UUID.fromString(userId))));
            roleBuilder.getUsers().add(user);
        }
    }

    @AllArgsConstructor
    @Getter
    public class TeamBuilder {

        private final UUID teamId;
        private final String name;
        private final Set<SecurityGroup> securityGroups;
        private final Map<String, RoleListResultSetExtractor.RoleBuilder> roleBuilders;

        public Team build() {
            return new Team(new TeamKey(teamId), name, securityGroups,
                    roleBuilders.values().stream()
                            .map(RoleListResultSetExtractor.RoleBuilder::build)
                            .collect(Collectors.toSet()));
        }
    }

}
