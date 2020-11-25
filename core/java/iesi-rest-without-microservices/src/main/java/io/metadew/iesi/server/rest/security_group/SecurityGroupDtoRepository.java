package io.metadew.iesi.server.rest.security_group;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class SecurityGroupDtoRepository implements ISecurityGroupDtoRepository {

    private static final String FETCH_SINGLE_QUERY = "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
            "security_group_teams.team_id as security_group_teams_team_id, " +
            "teams.id as team_id, teams.TEAM_NAME as team_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " WHERE security_groups.ID={0};";

    private static final String FETCH_BY_NAME_QUERY = "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
            "security_group_teams.team_id as security_group_teams_team_id, " +
            "teams.id as team_id, teams.TEAM_NAME as team_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " WHERE security_groups.NAME={0};";

    private static final String FETCH_ALL_QUERY = "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
            "security_group_teams.team_id as security_group_teams_team_id, " +
            "teams.id as team_id, teams.TEAM_NAME as team_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
            " ON teams.ID = security_group_teams.TEAM_ID;";



    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    public SecurityGroupDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    public Optional<SecurityGroupDto> get(String teamName) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getControlMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_BY_NAME_QUERY, SQLTools.getStringForSQL(teamName)),
                    "reader");
            return new SecurityGroupDtoListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<SecurityGroupDto> get(UUID id){
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getControlMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_SINGLE_QUERY, SQLTools.getStringForSQL(id)),
                    "reader");
            return new SecurityGroupDtoListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<SecurityGroupDto> getAll(){
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getControlMetadataRepository().executeQuery(
                    FETCH_ALL_QUERY,
                    "reader");
            return new HashSet<>(new SecurityGroupDtoListResultSetExtractor().extractData(cachedRowSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
