package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import io.metadew.iesi.metadata.definition.user.User;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public class TeamConfiguration extends Configuration<Team, TeamKey> {

    private static String fetchIdByNameQuery = "select teams.ID as team_id" +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams" +
            " WHERE NAME={0};";
    private static String fetchSingleQuery = "select teams.ID as team_id, teams.TEAM_NAME as team_name, " +
            "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "user_roles.user_id as user_role_user_id, " +
            "security_group_teams.security_group_id as security_group_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON teams.ID = roles.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON roles.ID = user_roles.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " WHERE teams.ID={0};";
    private static String fetchByNameQuery = "select teams.ID as team_id, teams.TEAM_NAME as team_name, " +
            "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "user_roles.user_id as user_role_user_id, " +
            "security_group_teams.security_group_id as security_group_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON teams.ID = roles.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON roles.ID = user_roles.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " WHERE teams.NAME={0};";
    private static String fetchAllQuery = "select teams.ID as team_id, teams.TEAM_NAME as team_name, " +
            "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "user_roles.user_id as user_role_user_id, " +
            "security_group_teams.security_group_id as security_group_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON teams.ID = roles.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON roles.ID = user_roles.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON teams.ID = security_group_teams.TEAM_ID;";

    private static String deleteSingleQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() +
            " WHERE ID={0};";
    private static String deleteSecurityGroupTeamsByTeamIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
            " WHERE TEAM_ID={0};";
    private static String deleteSecurityGroupTeamsBySecurityGroupIdAndTeamIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
            " WHERE TEAM_ID={0} AND SECURITY_GROUP_ID={1};";

    private static String insertQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() +
            " (ID, TEAM_NAME) VALUES ({0}, {1});";
    private static String insertSecurityGroupTeamsQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
            " (SECURITY_GROUP_ID, TEAM_ID) VALUES ({0}, {1});";

    private static String updateQuery = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() +
            " SET TEAM_NAME = {0} " +
            " WHERE ID = {1};";

    private static TeamConfiguration INSTANCE;

    public synchronized static TeamConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TeamConfiguration();
        }
        return INSTANCE;
    }

    private TeamConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository());
    }

    @Override
    public Optional<Team> get(TeamKey metadataKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery, SQLTools.GetStringForSQL(metadataKey.getUuid())),
                    "reader");
            return new TeamListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Team> getByName(String name) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchByNameQuery, SQLTools.GetStringForSQL(name)),
                    "reader");
            return new TeamListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String username) {
        return getByName(username).isPresent();
    }

    @Override
    public List<Team> getAll() {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchAllQuery, "reader");
            return new TeamListResultSetExtractor().extractData(cachedRowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(TeamKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey.toString()));
        String deleteStatement = MessageFormat.format(deleteSingleQuery, SQLTools.GetStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteStatement);
        String deleteRolesStatement = MessageFormat.format(deleteSecurityGroupTeamsByTeamIdQuery, SQLTools.GetStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteRolesStatement);
        // TODO delete roles
    }

    public void delete(String username) {
        log.trace(MessageFormat.format("Deleting {0}.", username));
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(MessageFormat.format(fetchIdByNameQuery, SQLTools.GetStringForSQL(username)), "reader");
        try {
            if (cachedRowSet.next()) {
                TeamKey teamKey = new TeamKey(UUID.fromString(cachedRowSet.getString("team_id")));
                delete(teamKey);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Team metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatement =
                MessageFormat.format(insertQuery,
                        SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid()),
                        SQLTools.GetStringForSQL(metadata.getTeamName()));
        getMetadataRepository().executeUpdate(insertStatement);
        for (SecurityGroupKey securityGroupKey : metadata.getSecurityGroupKeys()) {
            String insertSecurityGroupStatement =
                    MessageFormat.format(insertSecurityGroupTeamsQuery,
                            SQLTools.GetStringForSQL(securityGroupKey.getUuid()),
                            SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid()));
            getMetadataRepository().executeUpdate(insertSecurityGroupStatement);
        }

        for (Role role : metadata.getRoles()) {
            RoleConfiguration.getInstance().insert(role);
        }

    }

    @Override
    public void update(Team metadata) {
        getMetadataRepository().executeUpdate(MessageFormat.format(updateQuery,
                SQLTools.GetStringForSQL(metadata.getTeamName()),
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString())));

        String deleteSecurityGroupMembershipStatement = MessageFormat.format(deleteSecurityGroupTeamsByTeamIdQuery, SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid()));
        getMetadataRepository().executeUpdate(deleteSecurityGroupMembershipStatement);
        for (SecurityGroupKey securityGroupKey : metadata.getSecurityGroupKeys()) {
            String insertSecurityGroupStatement =
                    MessageFormat.format(insertSecurityGroupTeamsQuery,
                            SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.GetStringForSQL(securityGroupKey.getUuid()));
            getMetadataRepository().executeUpdate(insertSecurityGroupStatement);
        }

        for (Role role : metadata.getRoles()) {
            RoleConfiguration.getInstance().delete(role.getMetadataKey());
            RoleConfiguration.getInstance().insert(role);
        }
    }

    public List<SecurityGroup> getSecurityGroups(TeamKey userKey) {
        // TODO
        return null;
    }

    public List<User> getUsers(TeamKey TeamKey) {
        // TODO
        return null;
    }

    public void addSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(
                insertSecurityGroupTeamsQuery,
                SQLTools.GetStringForSQL(teamKey.getUuid()),
                SQLTools.GetStringForSQL(securityGroupKey.getUuid())
        ));
    }

    public void removeSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(
                deleteSecurityGroupTeamsBySecurityGroupIdAndTeamIdQuery,
                SQLTools.GetStringForSQL(teamKey.getUuid()),
                SQLTools.GetStringForSQL(securityGroupKey.getUuid())
        ));
    }

}