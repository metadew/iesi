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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public class UserConfiguration extends Configuration<User, UserKey> {

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
            String queryScript = "select ID, USERNAME, PASSWORD, ENABLED, EXPIRED, CREDENTIALS_EXPIRED, LOCKED " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
                    " WHERE ID=" + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
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

    public boolean exists(String username) {
        return get(username).isPresent();
    }

    @Override
    public List<User> getAll() {
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
    }

    @Override
    public void delete(UserKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey.toString()));
        String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public void delete(String username) {
        String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
                " WHERE USERNAME = " + SQLTools.GetStringForSQL(username) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    @Override
    public void insert(User metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
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
    }

    @Override
    public void update(User metadata) {
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

    public List<Team> getGroups(UserKey userKey) {
        List<Team> teams = new ArrayList<>();
        try {
            String queryScript = "select groups.ID, groups.GROUP_NAME " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() + " group_members " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups " +
                    "on group_members.GROUP_ID=groups.ID WHERE " +
                    "USER_ID =" + SQLTools.GetStringForSQL(userKey.getUuid().toString()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                teams.add(GroupConfiguration.getInstance().mapGroup(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teams;
    }

    public List<Team> getGroups(String username) {
        List<Team> teams = new ArrayList<>();
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
                teams.add(GroupConfiguration.getInstance().mapGroup(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teams;
    }

    public List<Privilege> getAuthorities(UserKey userKey) {
        List<Privilege> authorities = new ArrayList<>();
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

    public List<Privilege> getAuthorities(String username) {
        List<Privilege> authorities = new ArrayList<>();
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

    public void addAuthority(UserKey userKey, PrivilegeKey privilegeKey) {
        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserAuthorities").getName() +
                " (USER_ID, AUTHORITY_ID) VALUES (" +
                SQLTools.GetStringForSQL(userKey.getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(privilegeKey.getUuid().toString()) + ");";
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

    public void removeAuthority(UserKey userKey, PrivilegeKey privilegeKey) {
        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserAuthorities").getName() +
                " WHERE AUTHORITY_ID=" + SQLTools.GetStringForSQL(privilegeKey.getUuid().toString()) + " and " +
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
                SQLTools.getBooleanFromSql(cachedRowSet.getString("LOCKED")),
                privileges, teams);
    }

}
