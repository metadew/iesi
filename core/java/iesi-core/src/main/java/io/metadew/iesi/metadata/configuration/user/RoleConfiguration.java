package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.user.*;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Log4j2
public class RoleConfiguration extends Configuration<Role, RoleKey> {

    private static String fetchSingleQuery = "select roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "user_roles.user_id as user_role_user_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON roles.ID = user_roles.ROLE_ID " +
            " WHERE roles.ID={0};";
    private static String fetchByTeamIdQuery = "select roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "user_roles.user_id as user_role_user_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON roles.ID = user_roles.ROLE_ID " +
            " WHERE roles.team_id={0};";
    private static String fetchAllQuery = "select roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "user_roles.user_id as user_role_user_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON roles.ID = user_roles.ROLE_ID;";
    private static String fetchUsersByRoleIdQuery = "select users.ID as user_id, users.USERNAME as user_username, users.PASSWORD as user_password, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, user_roles.ROLE_ID as role_id" +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID " +
            " WHERE user_roles.ROLE_ID={0};";
    private static String deleteSingleQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() +
            " WHERE ID={0};";
    private static String deleteUserRolesByRoleIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() +
            " WHERE ROLE_ID={0};";
    private static String deleteUserRolesByUserIdAndRoleIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() +
            " WHERE USER_ID={0} AND ROLE_ID={1};";
    private static String deletePrivilegesByRoleIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() +
            " WHERE ROLE_ID={0};";
    private static String insertQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() +
            " (ID, TEAM_ID, ROLE_NAME) VALUES ({0}, {1}, {2});";
    private static String updateQuery = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() +
            " set TEAM_ID={1}, ROLE_NAME={2} " +
            "WHERE ID={0};";
    private static String insertPrivilegeQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() +
            " (ID, ROLE_ID, PRIVILEGE) VALUES ({0}, {1}, {2});";
    private static String insertUserRoleQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() +
            " (USER_ID, ROLE_ID) VALUES ({0}, {1});";

    private static RoleConfiguration instance;

    public static synchronized RoleConfiguration getInstance() {
        if (instance == null) {
            instance = new RoleConfiguration();
        }
        return instance;
    }

    private RoleConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository());
    }

    @Override
    public Optional<Role> get(RoleKey metadataKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery, SQLTools.getStringForSQL(metadataKey.getUuid())),
                    "reader");
            return new RoleListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Role> getAll() {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchAllQuery, "reader");
            return new RoleListResultSetExtractor().extractData(cachedRowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Role> getByTeamId(TeamKey teamKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchByTeamIdQuery, SQLTools.getStringForSQL(teamKey.getUuid())),
                    "reader");
            return new RoleListResultSetExtractor().extractData(cachedRowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(RoleKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey));
        String deleteStatement = MessageFormat.format(deleteSingleQuery, SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteStatement);
        String deletePrivilegesStatement = MessageFormat.format(deletePrivilegesByRoleIdQuery, SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deletePrivilegesStatement);
        String deleteUserRolesStatement = MessageFormat.format(deleteUserRolesByRoleIdQuery, SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteUserRolesStatement);
    }

    public void deleteByTeamKey(TeamKey teamKey) {
        getByTeamId(teamKey)
                .forEach(role -> delete(role.getMetadataKey()));
    }

    @Override
    public void insert(Role metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata));
        String insertStatement =
                MessageFormat.format(insertQuery,
                        SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                        SQLTools.getStringForSQL(metadata.getTeamKey().getUuid()),
                        SQLTools.getStringForSQL(metadata.getName()));
        getMetadataRepository().executeUpdate(insertStatement);
        for (UserKey userKey : metadata.getUserKeys()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertUserRoleQuery,
                            SQLTools.getStringForSQL(userKey.getUuid()),
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()))
            );
        }

        for (Privilege privilege : metadata.getPrivileges()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertPrivilegeQuery,
                            SQLTools.getStringForSQL(privilege.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(privilege.getRoleKey().getUuid()),
                            SQLTools.getStringForSQL(privilege.getPrivilege()))
            );
        }
    }

    @Override
    public void update(Role metadata) {
        log.trace(MessageFormat.format("updating {0}.", metadata));
        String insertStatement =
                MessageFormat.format(updateQuery,
                        SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                        SQLTools.getStringForSQL(metadata.getTeamKey().getUuid()),
                        SQLTools.getStringForSQL(metadata.getName()));
        getMetadataRepository().executeUpdate(insertStatement);


        String deleteUserRolesStatement = MessageFormat.format(deleteUserRolesByRoleIdQuery, SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()));
        getMetadataRepository().executeUpdate(deleteUserRolesStatement);
        for (UserKey userKey : metadata.getUserKeys()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertUserRoleQuery,
                            SQLTools.getStringForSQL(userKey.getUuid()),
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()))
            );
        }

        String deletePrivilegesStatement = MessageFormat.format(deletePrivilegesByRoleIdQuery, SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()));
        getMetadataRepository().executeUpdate(deletePrivilegesStatement);
        for (Privilege privilege : metadata.getPrivileges()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertPrivilegeQuery,
                            SQLTools.getStringForSQL(privilege.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(privilege.getRoleKey().getUuid()),
                            SQLTools.getStringForSQL(privilege.getPrivilege()))
            );
        }
    }

    public List<User> getUsers(RoleKey roleKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchUsersByRoleIdQuery, SQLTools.getStringForSQL(roleKey.getUuid())),
                    "reader");
            return new UserListResultSetExtractor().extractData(cachedRowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser(RoleKey roleKey, UserKey userKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(
                insertUserRoleQuery,
                SQLTools.getStringForSQL(userKey.getUuid()),
                SQLTools.getStringForSQL(roleKey.getUuid())
        ));
    }

    public void removeUser(RoleKey roleKey, UserKey userKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(
                deleteUserRolesByUserIdAndRoleIdQuery,
                SQLTools.getStringForSQL(userKey.getUuid()),
                SQLTools.getStringForSQL(roleKey.getUuid())
        ));
    }

}
