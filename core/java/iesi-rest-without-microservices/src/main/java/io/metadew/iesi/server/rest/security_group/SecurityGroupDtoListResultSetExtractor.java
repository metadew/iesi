package io.metadew.iesi.server.rest.security_group;

import io.metadew.iesi.metadata.definition.user.TeamKey;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;

public class SecurityGroupDtoListResultSetExtractor {

    public List<SecurityGroupDto> extractData(CachedRowSet rs) throws SQLException {
        Map<UUID, SecurityGroupDto> securityGroupMap = new HashMap<>();
        SecurityGroupDto securityGroup;
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

    private SecurityGroupDto mapRow(CachedRowSet cachedRowSet) throws SQLException {
        // security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
        //            "security_group_teams.team_id as security_group_teams_team_id, " +
        //            "teams.id as team_id, teams.TEAM_NAME as team_name " +
        return SecurityGroupDto.builder()
                .id(UUID.fromString(cachedRowSet.getString("security_groups_id")))
                .name(cachedRowSet.getString("security_groups_name"))
                .securityObjects(new HashSet<>())
                .teams(new HashSet<>())
                .build();
    }

    private void addTeam(SecurityGroupDto securityGroupDto, CachedRowSet cachedRowSet) throws SQLException {
        // security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
        //            "security_group_teams.team_id as security_group_teams_team_id, " +
        //            "teams.id as team_id, teams.TEAM_NAME as team_name " +
        if (cachedRowSet.getString("team_id") != null) {
            securityGroupDto.getTeams().add(SecurityGroupTeamDto.builder()
                    .id(UUID.fromString(cachedRowSet.getString("team_id")))
                    .teamName(cachedRowSet.getString("team_name"))
                    .build());
        }
    }

}
