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
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
=======
import java.util.*;
import java.util.stream.Collectors;
>>>>>>> master

@Log4j2
public class UserConfiguration extends Configuration<User, UserKey> {

<<<<<<< HEAD
=======
    private static String fetchIdByNameQuery = "select users.ID as user_id" +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " WHERE USERNAME={0};";
    private static String fetchSingleQuery = "select users.ID as user_id, users.USERNAME as user_username, users.PASSWORD as user_password, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, user_roles.ROLE_ID as role_id" +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID " +
            " WHERE ID={0};";
    private static String fetchByNameQuery = "select users.ID as user_id, users.USERNAME as user_username, users.PASSWORD as user_password, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, user_roles.ROLE_ID as role_id" +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID " +
            " WHERE USERNAME={0};";
    private static String fetchAllQuery = "select users.ID as user_id, users.USERNAME as user_username, users.PASSWORD as user_password, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, user_roles.ROLE_ID as role_id" +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID;";
    private static String deleteSingleQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
            " WHERE ID={0};";
    private static String deleteUserRolesByUserIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() +
            " WHERE USER_ID={0};";
    private static String deleteUserRolesByUserIdAndRoleIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() +
            " WHERE USER_ID={0} AND ROLE_ID={1};";
    private static String deleteByNameQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
            " WHERE USERNAME={0};";
    private static String insertQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
            " (ID, USERNAME, PASSWORD, ENABLED, EXPIRED, CREDENTIALS_EXPIRED, LOCKED) VALUES ({0}, {1}, {2}, {3}, {4}, {5}, {6});";
    private static String insertUserRoleQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() +
            " (USER_ID, ROLE_ID) VALUES ({0}, {1});";
    private static String updateQuery = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
            " SET USERNAME = {0}, PASSWORD ={1}, ENABLED = {2}, EXPIRED = {3}, CREDENTIALS_EXPIRED = {4}, LOCKED = {5}" +
            " WHERE ID = {6};";
    private static String fetchRolesByUserIdQuery = "select roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
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
    private static String fetchTeamsByUserIdQuery = "select teams.ID as team_id, teams.TEAM_NAME as team_name, " +
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

>>>>>>> master
    private static UserConfiguration INSTANCE;

    public synchronized static UserConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserConfiguration();
        }
        return INSTANCE;
    }

    private UserConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository());
    }

    @Override
    public Optional<User> get(UserKey metadataKey) {
        try {
<<<<<<< HEAD
            String queryScript = "select ID, USERNAME, PASSWORD, ENABLED, EXPIRED, CREDENTIALS_EXPIRED, LOCKED " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
                    " WHERE ID=" + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            if (cachedRowSet.next()) {
                return Optional.of(mapUser(cachedRowSet));
            } else {
                return Optional.empty();
            }
=======
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery, SQLTools.getStringForSQL(metadataKey.getUuid())),
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
                    MessageFormat.format(fetchByNameQuery, SQLTools.getStringForSQL(name)),
                    "reader");
            return new UserListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
>>>>>>> master
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String username) {
<<<<<<< HEAD
        return get(username).isPresent();
=======
        return getByName(username).isPresent();
>>>>>>> master
    }

    @Override
    public List<User> getAll() {
<<<<<<< HEAD
        List<User> users = new ArrayList<>();
        try {
            String queryScript = "select ID, USERNAME, PASSWORD, ENABLED, EXPIRED, CREDENTIALS_EXPIRED, LOCKED " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                users.add(mapUser(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
=======
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchAllQuery, "reader");
            return new UserListResultSetExtractor().extractData(cachedRowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
>>>>>>> master
    }

    @Override
    public void delete(UserKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey.toString()));
<<<<<<< HEAD
        String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public void delete(String username) {
        String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
                " WHERE USERNAME = " + SQLTools.GetStringForSQL(username) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
=======
        String deleteStatement = MessageFormat.format(deleteSingleQuery, SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteStatement);
        String deleteRolesStatement = MessageFormat.format(deleteUserRolesByUserIdQuery, SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteRolesStatement);
    }

    public void delete(String username) {
        log.trace(MessageFormat.format("Deleting {0}.", username));
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(MessageFormat.format(fetchIdByNameQuery, SQLTools.getStringForSQL(username)), "reader");
        try {
            if (cachedRowSet.next()) {
                UserKey userKey = new UserKey(UUID.fromString(cachedRowSet.getString("user_id")));
                delete(userKey);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
>>>>>>> master
    }

    @Override
    public void insert(User metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
<<<<<<< HEAD
        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
                " (ID, USERNAME, PASSWORD, ENABLED, EXPIRED, CREDENTIALS_EXPIRED, LOCKED) VALUES (" +
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(metadata.getUsername()) + ", " +
                SQLTools.GetStringForSQL(metadata.getPassword()) + ", " +
                SQLTools.GetStringForSQL(metadata.isEnabled()) + ", " +
                SQLTools.GetStringForSQL(metadata.isExpired()) + ", " +
                SQLTools.GetStringForSQL(metadata.isCredentialsExpired()) + ", " +
                SQLTools.GetStringForSQL(metadata.isLocked()) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
=======
        String insertStatement =
                MessageFormat.format(insertQuery,
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
                    MessageFormat.format(insertUserRoleQuery,
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(roleKey.getUuid()))
            );
        }
>>>>>>> master
    }

    @Override
    public void update(User metadata) {
<<<<<<< HEAD
        String updateStatement = "UPDATE " + getMetadataRepository().getTableNameByLabel("Users") +
                " SET USERNAME = " + SQLTools.GetStringForSQL(metadata.getUsername()) + ", " +
                "PASSWORD = " + SQLTools.GetStringForSQL(metadata.getPassword()) + ", " +
                "ENABLED = " + SQLTools.GetStringForSQL(metadata.isEnabled()) + ", " +
                "EXPIRED = " + SQLTools.GetStringForSQL(metadata.isExpired()) + ", " +
                "CREDENTIALS_EXPIRED = " + SQLTools.GetStringForSQL(metadata.isCredentialsExpired()) + ", " +
                "LOCKED = " + SQLTools.GetStringForSQL(metadata.isLocked()) +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(updateStatement);
    }

    public List<Group> getGroups(UserKey userKey) {
        List<Group> groups = new ArrayList<>();
        try {
            String queryScript = "select groups.ID, groups.GROUP_NAME " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() + " group_members " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups " +
                    "on group_members.GROUP_ID=groups.ID WHERE " +
                    "USER_ID =" + SQLTools.GetStringForSQL(userKey.getUuid().toString()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                groups.add(GroupConfiguration.getInstance().mapGroup(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return groups;
    }

    public List<Group> getGroups(String username) {
        List<Group> groups = new ArrayList<>();
        try {
            String queryScript = "select groups.ID, groups.GROUP_NAME " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() + " group_members " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " users " +
                    "on group_members.USER_ID=users.ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups " +
                    "on group_members.GROUP_ID=groups.ID WHERE " +
                    "USERNAME=" + SQLTools.GetStringForSQL(username) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                groups.add(GroupConfiguration.getInstance().mapGroup(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return groups;
    }

    public List<Authority> getAuthorities(UserKey userKey) {
        List<Authority> authorities = new ArrayList<>();
        try {
            String queryScript = "select authorities.ID, authorities.AUTHORITY " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() + " group_members " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() + " group_authorities " +
                    "on group_authorities.GROUP_ID=group_members.GROUP_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
                    "on group_authorities.AUTHORITY_ID=authorities.ID " +
                    "WHERE group_members.USER_ID = " + SQLTools.GetStringForSQL(userKey.getUuid().toString()) +
                    " UNION " +
                    "select authorities.ID, authorities.AUTHORITY " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserAuthorities").getName() + " user_authorities " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
                    "on user_authorities.AUTHORITY_ID=authorities.ID WHERE " +
                    "user_authorities.USER_ID =" + SQLTools.GetStringForSQL(userKey.getUuid().toString()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                authorities.add(AuthorityConfiguration.getInstance().mapAuthority(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorities;
    }

    public List<Authority> getAuthorities(String username) {
        List<Authority> authorities = new ArrayList<>();
        try {
            String queryScript = "select authorities.ID, authorities.AUTHORITY " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() + " group_members " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
                    "on users.ID=group_members.USER_ID " +
                    "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() + " group_authorities " +
                    "on group_authorities.GROUP_ID=group_members.GROUP_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
                    "on group_authorities.AUTHORITY_ID=authorities.ID " +
                    "WHERE users.username = " + SQLTools.GetStringForSQL(username) +
                    " UNION " +
                    "select authorities.ID, authorities.AUTHORITY " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserAuthorities").getName() + " user_authorities " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
                    "on users.ID=user_authorities.USER_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
                    "on user_authorities.AUTHORITY_ID=authorities.ID WHERE " +
                    "users.username =" + SQLTools.GetStringForSQL(username) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                authorities.add(AuthorityConfiguration.getInstance().mapAuthority(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorities;
    }

    public void addAuthority(UserKey userKey, AuthorityKey authorityKey) {
        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserAuthorities").getName() +
                " (USER_ID, AUTHORITY_ID) VALUES (" +
                SQLTools.GetStringForSQL(userKey.getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(authorityKey.getUuid().toString()) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void addAuthority(String username, String authority) {
        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserAuthorities").getName() +
                " (USER_ID, AUTHORITY_ID) SELECT users.ID, authorities.ID FROM " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users, " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
                " where users.USERNAME=" + SQLTools.GetStringForSQL(username) + " and " +
                " authorities.AUTHORITY=" + SQLTools.GetStringForSQL(authority) + ";";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void removeAuthority(UserKey userKey, AuthorityKey authorityKey) {
        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserAuthorities").getName() +
                " WHERE AUTHORITY_ID=" + SQLTools.GetStringForSQL(authorityKey.getUuid().toString()) + " and " +
                "USER_ID= " + SQLTools.GetStringForSQL(userKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void removeAuthority(String username, String authority) {
        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserAuthorities").getName() +
                " WHERE (USER_ID, AUTHORITY_ID) IN ( " +
                "SELECT users.ID, authorities.ID FROM " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users, " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
                " where users.USERNAME=" + SQLTools.GetStringForSQL(username) + " and " +
                " authorities.AUTHORITY=" + SQLTools.GetStringForSQL(authority) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public Optional<User> get(String name) {
        try {
            String queryScript = "select ID, USERNAME, PASSWORD, ENABLED, EXPIRED, CREDENTIALS_EXPIRED, LOCKED " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
                    " WHERE USERNAME=" + SQLTools.GetStringForSQL(name) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            if (cachedRowSet.next()) {
                return Optional.of(mapUser(cachedRowSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User mapUser(CachedRowSet cachedRowSet) throws SQLException {
        return new User(new UserKey(UUID.fromString(cachedRowSet.getString("ID"))),
                cachedRowSet.getString("USERNAME"),
                cachedRowSet.getString("PASSWORD"),
                SQLTools.getBooleanFromSql(cachedRowSet.getString("ENABLED")),
                SQLTools.getBooleanFromSql(cachedRowSet.getString("EXPIRED")),
                SQLTools.getBooleanFromSql(cachedRowSet.getString("CREDENTIALS_EXPIRED")),
                SQLTools.getBooleanFromSql(cachedRowSet.getString("LOCKED"))
        );
=======
        getMetadataRepository().executeUpdate(MessageFormat.format(updateQuery,
                SQLTools.getStringForSQL(metadata.getUsername()),
                SQLTools.getStringForSQL(metadata.getPassword()),
                SQLTools.getStringForSQL(metadata.isEnabled()),
                SQLTools.getStringForSQL(metadata.isExpired()),
                SQLTools.getStringForSQL(metadata.isCredentialsExpired()),
                SQLTools.getStringForSQL(metadata.isLocked()),
                SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid().toString())));

        String deleteRolesStatement = MessageFormat.format(deleteUserRolesByUserIdQuery, SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()));
        getMetadataRepository().executeUpdate(deleteRolesStatement);
        for (RoleKey roleKey : metadata.getRoleKeys()) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertUserRoleQuery,
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(roleKey.getUuid())));

        }
    }

    public Set<Role> getRoles(UserKey userKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchRolesByUserIdQuery, SQLTools.getStringForSQL(userKey.getUuid())),
                    "reader");
            return new HashSet<>(new RoleListResultSetExtractor().extractData(cachedRowSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Team> getTeams(UserKey userKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchTeamsByUserIdQuery, SQLTools.getStringForSQL(userKey.getUuid())),
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
                insertUserRoleQuery,
                SQLTools.getStringForSQL(userKey.getUuid()),
                SQLTools.getStringForSQL(roleKey.getUuid())
        ));
    }

    public void removeRole(UserKey userKey, RoleKey roleKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(
                deleteUserRolesByUserIdAndRoleIdQuery,
                SQLTools.getStringForSQL(userKey.getUuid()),
                SQLTools.getStringForSQL(roleKey.getUuid())
        ));
>>>>>>> master
    }

}
