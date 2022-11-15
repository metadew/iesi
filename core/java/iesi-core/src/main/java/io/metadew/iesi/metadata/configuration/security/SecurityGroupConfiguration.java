package io.metadew.iesi.metadata.configuration.security;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Log4j2
@Component
public class SecurityGroupConfiguration extends Configuration<SecurityGroup, SecurityGroupKey> {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;

    private String fetchSingleQuery() {
        return "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
                "security_group_teams.team_id as security_group_teams_team_id, " +
                "teams.team_name as team_name " +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
                " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID" +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Teams").getName() + " teams " +
                "ON security_group_teams.TEAM_ID = teams.ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Roles").getName() + " roles " +
                "ON roles.TEAM_ID = teams.ID " +
                " WHERE security_groups.ID={0};";
    }

    private String fetchByNameQuery() {
        return "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
                "security_group_teams.team_id as security_group_teams_team_id, " +
                "teams.team_name as team_name " +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
                " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID" +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Teams").getName() + " teams " +
                "ON security_group_teams.TEAM_ID = teams.ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Roles").getName() + " roles " +
                "ON roles.TEAM_ID = teams.ID " +
                " WHERE security_groups.NAME={0};";
    }

    private String fetchAllQuery() {
        return "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
                "security_group_teams.team_id as security_group_teams_team_id, " +
                "teams.team_name as team_name " +

                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
                " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID" +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Teams").getName() + " teams " +
                "ON security_group_teams.TEAM_ID = teams.ID" +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Roles").getName() + " roles " +
                "ON roles.TEAM_ID = teams.ID;";
    }

    private String deleteSingleQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroups").getName() +
                " WHERE ID={0};";
    }

    private String deleteTeamMembershipsBySecurityGroupIdQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
                " WHERE SECURITY_GROUP_ID={0};";
    }

    private String deleteTeamMembershipsBySecurityGroupIdAndTeamIdQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
                " WHERE SECURITY_GROUP_ID={0} and TEAM_ID={1};";

    }

    private String insertQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroups").getName() +
                " (ID, NAME) VALUES ({0}, {1});";
    }

    private String insertSecurityGroupTeamMembershipQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
                " (SECURITY_GROUP_ID, TEAM_ID) VALUES ({0}, {1});";
    }

    private String updateQuery() {
        return "UPDATE " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroups").getName() +
                " SET NAME = {0} " +
                " WHERE ID = {1};";
    }

    public SecurityGroupConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration, MetadataTablesConfiguration metadataTablesConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
    }


    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getControlMetadataRepository());
    }

    @Override
    public Optional<SecurityGroup> get(SecurityGroupKey metadataKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery(), SQLTools.getStringForSQL(metadataKey.getUuid())),
                    "reader");
            return new SecurityGroupListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<SecurityGroup> getByName(String name) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchByNameQuery(), SQLTools.getStringForSQL(name)),
                    "reader");
            return new SecurityGroupListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SecurityGroup> getAll() {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchAllQuery(), "reader");
            return new SecurityGroupListResultSetExtractor().extractData(cachedRowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(SecurityGroupKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey.toString()));
        String deleteStatement = MessageFormat.format(deleteSingleQuery(), SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteStatement);
        String deleteRolesStatement = MessageFormat.format(deleteTeamMembershipsBySecurityGroupIdQuery(), SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteRolesStatement);
    }

    @Override
    public void insert(SecurityGroup metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatement =
                MessageFormat.format(insertQuery(),
                        SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                        SQLTools.getStringForSQL(metadata.getName()));
        getMetadataRepository().executeUpdate(insertStatement);
        for (TeamKey teamKey : metadata.getTeamKeys()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertSecurityGroupTeamMembershipQuery(),
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(teamKey.getUuid()))
            );
        }

    }

    @Override
    public void update(SecurityGroup metadata) {
        getMetadataRepository().executeUpdate(MessageFormat.format(updateQuery(),
                SQLTools.getStringForSQL(metadata.getName()),
                SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid().toString())));

        String deleteRolesStatement = MessageFormat.format(deleteTeamMembershipsBySecurityGroupIdQuery(), SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()));
        getMetadataRepository().executeUpdate(deleteRolesStatement);
        for (TeamKey teamKey : metadata.getTeamKeys()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertSecurityGroupTeamMembershipQuery(),
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(teamKey.getUuid()))
            );
        }

    }

    public void addTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(insertSecurityGroupTeamMembershipQuery(),
                SQLTools.getStringForSQL(securityGroupKey.getUuid()),
                SQLTools.getStringForSQL(teamKey.getUuid())));
    }

    public void deleteTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteTeamMembershipsBySecurityGroupIdAndTeamIdQuery(),
                SQLTools.getStringForSQL(securityGroupKey.getUuid()),
                SQLTools.getStringForSQL(teamKey.getUuid())));
    }

}
