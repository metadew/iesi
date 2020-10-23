package io.metadew.iesi.server.rest.user.team;

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
public class TeamDtoRepository implements ITeamDtoRepository {

    private static final String FETCH_SINGLE_QUERY = "select " +
            "teams.ID as team_id, teams.TEAM_NAME as team_name, " +
            "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "users.ID as user_id, users.USERNAME as user_username, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
            "security_groups.ID as security_group_id, security_groups.NAME as security_group_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON teams.ID = roles.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON roles.ID = user_roles.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
            " ON users.ID = user_roles.USER_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " WHERE teams.ID={0};";

    private static final String FETCH_SINGLE_QUERY_BY_NAME = "select " +
            "teams.ID as team_id, teams.TEAM_NAME as team_name, " +
            "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "users.ID as user_id, users.USERNAME as user_username, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
            "security_groups.ID as security_group_id, security_groups.NAME as security_group_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON teams.ID = roles.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON roles.ID = user_roles.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
            " ON users.ID = user_roles.USER_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " WHERE teams.TEAM_NAME={0};";

    private static final String FETCH_ALL_QUERY = "select " +
            "teams.ID as team_id, teams.TEAM_NAME as team_name, " +
            "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "users.ID as user_id, users.USERNAME as user_username, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
            "security_groups.ID as security_group_id, security_groups.NAME as security_group_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON teams.ID = roles.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON roles.ID = user_roles.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
            " ON users.ID = user_roles.USER_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID;";

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    public TeamDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    public Optional<TeamDto> get(String teamName) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_SINGLE_QUERY_BY_NAME, SQLTools.GetStringForSQL(teamName)),
                    "reader");
            return new TeamDtoListResultSetExtractor()
                    .extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Optional<TeamDto> get(UUID uuid) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_SINGLE_QUERY, SQLTools.GetStringForSQL(uuid)),
                    "reader");
            return new TeamDtoListResultSetExtractor()
                    .extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<TeamDto> getAll() {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    FETCH_ALL_QUERY,
                    "reader");
            return new HashSet<>(
                    new TeamDtoListResultSetExtractor().extractData(cachedRowSet)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
