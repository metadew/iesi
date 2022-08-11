package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.user.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
public class UserConfiguration extends Configuration<User, UserKey> {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;
    private final TeamListResultSetExtractor teamListResultSetExtractor;
    private final RoleListResultSetExtractor roleListResultSetExtractor;

    public UserConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration,
                             MetadataTablesConfiguration metadataTablesConfiguration,
                             @Lazy TeamListResultSetExtractor teamListResultSetExtractor,
                             @Lazy RoleListResultSetExtractor roleListResultSetExtractor) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
        this.teamListResultSetExtractor = teamListResultSetExtractor;
        this.roleListResultSetExtractor = roleListResultSetExtractor;
    }


    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getControlMetadataRepository());
    }

    private String fetchIdByNameQuery() {
        return "select users.ID as user_id" +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Users").getName() + " users" +
                " WHERE USERNAME={0};";
    }

    private String fetchByNameQuery() {
        return "select users.ID as user_id, users.USERNAME as user_username, users.PASSWORD as user_password, " +
                "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, user_roles.ROLE_ID as role_id" +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Users").getName() + " users" +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
                " ON users.ID = user_roles.USER_ID " +
                " WHERE USERNAME={0};";
    }

    private String fetchSingleQuery() {
        return "select users.ID as user_id, users.USERNAME as user_username, users.PASSWORD as user_password, " +
                "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, user_roles.ROLE_ID as role_id" +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Users").getName() + " users" +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
                " ON users.ID = user_roles.USER_ID " +
                " WHERE ID={0};";
    }

    private String fetchUUidByNameQuery() {
        return "select users.ID as user_id " + "" +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Users").getName() + " users " +
                "WHERE USERNAME={0};";
    }

    private String fetchAllQuery() {
        return "select users.ID as user_id, users.USERNAME as user_username, users.PASSWORD as user_password, " +
                "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, user_roles.ROLE_ID as role_id" +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Users").getName() + " users" +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
                " ON users.ID = user_roles.USER_ID;";
    }

    private String deleteSingleQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Users").getName() +
                " WHERE ID={0};";
    }

    private String deleteUserRolesByUserIdQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() +
                " WHERE USER_ID={0};";
    }

    private String deleteUserRolesByUserIdAndRoleIdQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() +
                " WHERE USER_ID={0} AND ROLE_ID={1};";
    }

    private String deleteByNameQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Users").getName() +
                " WHERE USERNAME={0};";
    }

    private String insertQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("Users").getName() +
                " (ID, USERNAME, PASSWORD, ENABLED, EXPIRED, CREDENTIALS_EXPIRED, LOCKED) VALUES ({0}, {1}, {2}, {3}, {4}, {5}, {6});";
    }

    private String insertUserRoleQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() +
                " (USER_ID, ROLE_ID) VALUES ({0}, {1});";
    }

    private String updateQuery() {
        return "UPDATE " + metadataTablesConfiguration.getMetadataTableNameByLabel("Users").getName() +
                " SET PASSWORD ={0}, ENABLED = {1}, EXPIRED = {2}, CREDENTIALS_EXPIRED = {3}, LOCKED = {4}" +
                " WHERE ID = {5};";
    }

    private String fetchRoleSByUserIdQuery() {
        return "select roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
                "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
                "user_roles.user_id as user_role_user_id " +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
                " INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Roles").getName() + " roles " +
                " on user_roles.ROLE_ID = roles.ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
                " ON roles.ID = privileges.ROLE_ID " +
                " WHERE user_roles.USER_ID={0};";
    }

    private String fetchTeamsByuserIdQuery() {
        return "select teams.ID as team_id, teams.TEAM_NAME as team_name, " +
                "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
                "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
                "user_roles.user_id as user_role_user_id, " +
                "security_group_teams.security_group_id as security_group_id " +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Teams").getName() + " teams" +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Roles").getName() + " roles " +
                " ON teams.ID = roles.TEAM_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
                " ON roles.ID = privileges.ROLE_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
                " ON roles.ID = user_roles.ROLE_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
                " ON teams.ID = security_group_teams.TEAM_ID " +
                " WHERE user_roles.ID={0};";
    }

    @Override
    public Optional<User> get(UserKey metadataKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery(), SQLTools.getStringForSQL(metadataKey.getUuid())),
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
                    MessageFormat.format(fetchByNameQuery(), SQLTools.getStringForSQL(name)),
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
                    MessageFormat.format(fetchUUidByNameQuery(), SQLTools.getStringForSQL(name)),
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
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchAllQuery(), "reader");
            return new UserListResultSetExtractor().extractData(cachedRowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UserKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey.toString()));
        String deleteStatement = MessageFormat.format(deleteSingleQuery(), SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteStatement);
        String deleteRolesStatement = MessageFormat.format(deleteUserRolesByUserIdQuery(), SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteRolesStatement);
    }

    public void delete(String username) {
        log.trace(MessageFormat.format("Deleting {0}.", username));
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(MessageFormat.format(fetchIdByNameQuery(), SQLTools.getStringForSQL(username)), "reader");
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
                MessageFormat.format(insertQuery(),
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
                    MessageFormat.format(insertUserRoleQuery(),
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(roleKey.getUuid()))
            );
        }
    }

    @Override
    public void update(User metadata) {
        getMetadataRepository().executeUpdate(MessageFormat.format(updateQuery(),
                SQLTools.getStringForSQL(metadata.getPassword()),
                SQLTools.getStringForSQL(metadata.isEnabled()),
                SQLTools.getStringForSQL(metadata.isExpired()),
                SQLTools.getStringForSQL(metadata.isCredentialsExpired()),
                SQLTools.getStringForSQL(metadata.isLocked()),
                SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid().toString())));

        String deleteRolesStatement = MessageFormat.format(deleteUserRolesByUserIdQuery(), SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()));
        getMetadataRepository().executeUpdate(deleteRolesStatement);
        for (RoleKey roleKey : metadata.getRoleKeys()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertUserRoleQuery(),
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(roleKey.getUuid())));

        }
    }

    public Set<Role> getRoles(UserKey userKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchRoleSByUserIdQuery(), SQLTools.getStringForSQL(userKey.getUuid())),
                    "reader");
            return new HashSet<>(roleListResultSetExtractor.extractData(cachedRowSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Team> getTeams(UserKey userKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchTeamsByuserIdQuery(), SQLTools.getStringForSQL(userKey.getUuid())),
                    "reader");
            return new HashSet<>(teamListResultSetExtractor.extractData(cachedRowSet));
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
                insertUserRoleQuery(),
                SQLTools.getStringForSQL(userKey.getUuid()),
                SQLTools.getStringForSQL(roleKey.getUuid())
        ));
    }

    public void removeRole(UserKey userKey, RoleKey roleKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(
                deleteUserRolesByUserIdAndRoleIdQuery(),
                SQLTools.getStringForSQL(userKey.getUuid()),
                SQLTools.getStringForSQL(roleKey.getUuid())
        ));
    }

}
