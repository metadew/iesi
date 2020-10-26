package io.metadew.iesi.server.rest.user;

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
public class UserDtoRepository implements IUserDtoRepository {

    private static final String fetchSingleQuery = "select " +
            "users.ID as user_id, users.USERNAME as user_username, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
            "roles.ID as role_id, roles.role_name as role_role_name, " +
            "privileges.ID as privilege_id, privileges.privilege as privilege_privilege, " +
            "teams.ID as team_id, teams.TEAM_NAME as team_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON user_roles.ROLE_ID = roles.ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON privileges.ROLE_ID = roles.ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
            " ON teams.ID = roles.TEAM_ID " +
            " WHERE users.ID={0};";

    private static final String fetchAllQuery = "select " +
            "users.ID as user_id, users.USERNAME as user_username, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
            "roles.ID as role_id, roles.role_name as role_role_name, " +
            "privileges.ID as privilege_id, privileges.privilege as privilege_privilege, " +
            "teams.ID as team_id, teams.TEAM_NAME as team_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON user_roles.ROLE_ID = roles.ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON privileges.ROLE_ID = roles.ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
            " ON teams.ID = roles.TEAM_ID;";

    private static final String fetchSingleByNameQuery = "select " +
            "users.ID as user_id, users.USERNAME as user_username, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
            "roles.ID as role_id, roles.role_name as role_role_name, " +
            "privileges.ID as privilege_id, privileges.privilege as privilege_privilege, " +
            "teams.ID as team_id, teams.TEAM_NAME as team_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON user_roles.ROLE_ID = roles.ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON privileges.ROLE_ID = roles.ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
            " ON teams.ID = roles.TEAM_ID " +
            " WHERE users.NAME={0};";

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    public UserDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    public Optional<UserDto> get(String username) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleByNameQuery, SQLTools.GetStringForSQL(username)),
                    "reader");
            return new UserDtoListResultSetExtractor()
                    .extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<UserDto> get(UUID id) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery, SQLTools.GetStringForSQL(id)),
                    "reader");
            return new UserDtoListResultSetExtractor()
                    .extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<UserDto> getAll() {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    fetchAllQuery,
                    "reader");
            return new HashSet<>(
                    new UserDtoListResultSetExtractor()
                            .extractData(cachedRowSet)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
