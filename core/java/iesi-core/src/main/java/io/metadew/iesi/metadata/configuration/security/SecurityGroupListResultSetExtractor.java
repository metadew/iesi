package io.metadew.iesi.metadata.configuration.security;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;

public class SecurityGroupListResultSetExtractor {

    public List<SecurityGroup> extractData(CachedRowSet rs) throws SQLException {
        Map<UUID, SecurityGroup> securityGroupMap = new HashMap<>();
        SecurityGroup securityGroup;
        while (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString("security_groups_id"));
            securityGroup = securityGroupMap.get(uuid);
            if (securityGroup == null) {
                securityGroup = mapRow(rs);
                securityGroupMap.put(uuid, securityGroup);
            }
            addTeam(securityGroup, rs);
        }
        return new ArrayList<>(securityGroupMap.values());
    }

    private SecurityGroup mapRow(CachedRowSet cachedRowSet) throws SQLException {
        //security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
        return new SecurityGroup(new SecurityGroupKey(UUID.fromString(cachedRowSet.getString("security_groups_id"))),
                cachedRowSet.getString("security_groups_name"),
                new HashSet<>(),
                new HashSet<>());
    }

    private void addTeam(SecurityGroup securityGroup, CachedRowSet cachedRowSet) throws SQLException {
        if (cachedRowSet.getString("security_group_teams_team_id") != null) {
            securityGroup.getTeamKeys().add(new TeamKey(UUID.fromString(cachedRowSet.getString("security_group_teams_team_id"))));
        }
    }

}
