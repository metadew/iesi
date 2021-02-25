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
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class UserConfiguration extends Configuration<User, UserKey> {

    private static final String FETCH_ID_BY_NAME_QUERY = "select users.ID as user_id" +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " WHERE USERNAME={0};";
    private static final String FETCH_BY_NAME_QUERY = "select users.ID as user_id, users.USERNAME as user_username, users.PASSWORD as user_password, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, user_roles.ROLE_ID as role_id" +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID " +
            " WHERE USERNAME={0};";
    private static final String FETCH_SINGLE_QUERY = "select users.ID as user_id, users.USERNAME as user_username, users.PASSWORD as user_password, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, user_roles.ROLE_ID as role_id" +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID " +
            " WHERE ID={0};";
    private static final String FETCH_UUID_BY_NAME_QUERY = "select users.ID as user_id " + "" +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
            "WHERE USERNAME={0};";
    private static final String FETCH_ALL_QUERY = "select users.ID as user_id, users.USERNAME as user_username, users.PASSWORD as user_password, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, user_roles.ROLE_ID as role_id" +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID;";
    private static final String DELETE_SINGLE_QUERY = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
            " WHERE ID={0};";
    private static final String DELETE_USER_ROLES_BY_USER_ID_QUERY = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() +
            " WHERE USER_ID={0};";
    private static final String DELETE_USER_ROLES_BY_USER_ID_AND_ROLE_ID_QUERY = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() +
            " WHERE USER_ID={0} AND ROLE_ID={1};";
    private static final String DELETE_BY_NAME_QUERY = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
            " WHERE USERNAME={0};";
    private static final String INSERT_QUERY = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
            " (ID, USERNAME, PASSWORD, ENABLED, EXPIRED, CREDENTIALS_EXPIRED, LOCKED) VALUES ({0}, {1}, {2}, {3}, {4}, {5}, {6});";
    private static final String INSERT_USER_ROLE_QUERY = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() +
            " (USER_ID, ROLE_ID) VALUES ({0}, {1});";
    private static final String UPDATE_QUERY = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
            " SET PASSWORD ={0}, ENABLED = {1}, EXPIRED = {2}, CREDENTIALS_EXPIRED = {3}, LOCKED = {4}" +
            " WHERE ID = {5};";
    private static final String FETCH_ROLES_BY_USER_ID_QUERY = "select roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "user_roles.user_id as user_role_user_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " on user_roles.ROLE_ID = roles.ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
//            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
//            " ON roles.ID = user_roles.ROLE_ID " +
            " WHERE user_roles.USER_ID={0};";
    private static final String FETCH_TEAMS_BY_USER_ID_QUERY = "select teams.ID as team_id, teams.TEAM_NAME as team_name, " +
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
            " WHERE user_roles.ID={0};";

    private static UserConfiguration instance;

    public static synchronized UserConfiguration getInstance() {
        if (instance == null) {
            instance = new UserConfiguration();
        }
        return instance;
    }

    private UserConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository());
    }

    @Override
    public Optional<User> get(UserKey metadataKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_SINGLE_QUERY, SQLTools.getStringForSQL(metadataKey.getUuid())),
                    "reader");
            return new UserListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> getByName(String name) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_BY_NAME_QUERY, SQLTools.getStringForSQL(name)),
                    "reader");
            return new UserListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<UUID> getUuidByName(String name) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_UUID_BY_NAME_QUERY, SQLTools.getStringForSQL(name)),
                    "reader");
            if (cachedRowSet.next()) {
                return Optional.of(UUID.fromString(cachedRowSet.getString("user_id")));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String username) {
        return getByName(username).isPresent();
    }

    @Override
    public List<User> getAll() {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(FETCH_ALL_QUERY, "reader");
            return new UserListResultSetExtractor().extractData(cachedRowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UserKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey.toString()));
        String deleteStatement = MessageFormat.format(DELETE_SINGLE_QUERY, SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteStatement);
        String deleteRolesStatement = MessageFormat.format(DELETE_USER_ROLES_BY_USER_ID_QUERY, SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteRolesStatement);
    }

    public void delete(String username) {
        log.trace(MessageFormat.format("Deleting {0}.", username));
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(MessageFormat.format(FETCH_ID_BY_NAME_QUERY, SQLTools.getStringForSQL(username)), "reader");
        try {
            if (cachedRowSet.next()) {
                UserKey userKey = new UserKey(UUID.fromString(cachedRowSet.getString("user_id")));
                delete(userKey);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(User metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatement =
                MessageFormat.format(INSERT_QUERY,
                        SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                        SQLTools.getStringForSQL(metadata.getUsername()),
                        SQLTools.getStringForSQL(metadata.getPassword()),
                        SQLTools.getStringForSQL(metadata.isEnabled()),
                        SQLTools.getStringForSQL(metadata.isExpired()),
                        SQLTools.getStringForSQL(metadata.isCredentialsExpired()),
                        SQLTools.getStringForSQL(metadata.isLocked()));
        getMetadataRepository().executeUpdate(insertStatement);
        for (RoleKey roleKey : metadata.getRoleKeys()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(INSERT_USER_ROLE_QUERY,
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(roleKey.getUuid()))
            );
        }
    }

    @Override
    public void update(User metadata) {
        getMetadataRepository().executeUpdate(MessageFormat.format(UPDATE_QUERY,
                SQLTools.getStringForSQL(metadata.getPassword()),
                SQLTools.getStringForSQL(metadata.isEnabled()),
                SQLTools.getStringForSQL(metadata.isExpired()),
                SQLTools.getStringForSQL(metadata.isCredentialsExpired()),
                SQLTools.getStringForSQL(metadata.isLocked()),
                SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid().toString())));

        String deleteRolesStatement = MessageFormat.format(DELETE_USER_ROLES_BY_USER_ID_QUERY, SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()));
        getMetadataRepository().executeUpdate(deleteRolesStatement);
        for (RoleKey roleKey : metadata.getRoleKeys()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(INSERT_USER_ROLE_QUERY,
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(roleKey.getUuid())));

        }
    }

    public Set<Role> getRoles(UserKey userKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_ROLES_BY_USER_ID_QUERY, SQLTools.getStringForSQL(userKey.getUuid())),
                    "reader");
            return new HashSet<>(new RoleListResultSetExtractor().extractData(cachedRowSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Team> getTeams(UserKey userKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_TEAMS_BY_USER_ID_QUERY, SQLTools.getStringForSQL(userKey.getUuid())),
                    "reader");
            return new HashSet<>(new TeamListResultSetExtractor().extractData(cachedRowSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Privilege> getPrivileges(UserKey userKey) {
        return getRoles(userKey).stream().map(Role::getPrivileges)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public void addRole(UserKey userKey, RoleKey roleKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(
                INSERT_USER_ROLE_QUERY,
                SQLTools.getStringForSQL(userKey.getUuid()),
                SQLTools.getStringForSQL(roleKey.getUuid())
        ));
    }

    public void removeRole(UserKey userKey, RoleKey roleKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(
                DELETE_USER_ROLES_BY_USER_ID_AND_ROLE_ID_QUERY,
                SQLTools.getStringForSQL(userKey.getUuid()),
                SQLTools.getStringForSQL(roleKey.getUuid())
        ));
    }

}
