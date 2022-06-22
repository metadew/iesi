package io.metadew.iesi.metadata.configuration.security;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Log4j2
public class SecurityGroupConfiguration extends Configuration<SecurityGroup, SecurityGroupKey> {

    private static String fetchSingleQuery = "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
            "security_group_teams.team_id as security_group_teams_team_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " WHERE security_groups.ID={0};";

    private static String fetchByNameQuery = "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
            "security_group_teams.team_id as security_group_teams_team_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " WHERE security_groups.NAME={0};";

    private static String fetchAllQuery = "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
            "security_group_teams.team_id as security_group_teams_team_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID;";

    private static String deleteSingleQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() +
            " WHERE ID={0};";

    private static String deleteTeamMembershipsBySecurityGroupIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
            " WHERE SECURITY_GROUP_ID={0};";

    private static String deleteTeamMembershipsBySecurityGroupIdAndTeamIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
            " WHERE SECURITY_GROUP_ID={0} and TEAM_ID={1};";

    private static String insertQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() +
            " (ID, NAME) VALUES ({0}, {1});";

    private static String insertSecurityGroupTeamMembershipQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
            " (SECURITY_GROUP_ID, TEAM_ID) VALUES ({0}, {1});";

    private static String updateQuery = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() +
            " SET NAME = {0} " +
            " WHERE ID = {1};";

    private static SecurityGroupConfiguration INSTANCE;

    public static synchronized SecurityGroupConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SecurityGroupConfiguration();
        }
        return INSTANCE;
    }

    private SecurityGroupConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository());
    }

    @Override
    public Optional<SecurityGroup> get(SecurityGroupKey metadataKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery, SQLTools.getStringForSQL(metadataKey.getUuid())),
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
                    MessageFormat.format(fetchByNameQuery, SQLTools.getStringForSQL(name)),
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
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchAllQuery, "reader");
            return new SecurityGroupListResultSetExtractor().extractData(cachedRowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(SecurityGroupKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey.toString()));
        String deleteStatement = MessageFormat.format(deleteSingleQuery, SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteStatement);
        String deleteRolesStatement = MessageFormat.format(deleteTeamMembershipsBySecurityGroupIdQuery, SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteRolesStatement);
    }

    @Override
    public void insert(SecurityGroup metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatement =
                MessageFormat.format(insertQuery,
                        SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                        SQLTools.getStringForSQL(metadata.getName()));
        getMetadataRepository().executeUpdate(insertStatement);
        for (TeamKey teamKey : metadata.getTeamKeys()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertSecurityGroupTeamMembershipQuery,
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(teamKey.getUuid()))
            );
        }

    }

    @Override
    public void update(SecurityGroup metadata) {
        getMetadataRepository().executeUpdate(MessageFormat.format(updateQuery,
                SQLTools.getStringForSQL(metadata.getName()),
                SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid().toString())));

        String deleteRolesStatement = MessageFormat.format(deleteTeamMembershipsBySecurityGroupIdQuery, SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()));
        getMetadataRepository().executeUpdate(deleteRolesStatement);
        for (TeamKey teamKey : metadata.getTeamKeys()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertSecurityGroupTeamMembershipQuery,
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(teamKey.getUuid()))
            );
        }

    }

    public void addTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(insertSecurityGroupTeamMembershipQuery,
                SQLTools.getStringForSQL(securityGroupKey.getUuid()),
                SQLTools.getStringForSQL(teamKey.getUuid())));
    }

    public void deleteTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteTeamMembershipsBySecurityGroupIdAndTeamIdQuery,
                SQLTools.getStringForSQL(securityGroupKey.getUuid()),
                SQLTools.getStringForSQL(teamKey.getUuid())));
    }

}
