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
            String queryScript = "select ID, USERNAME, PASSWORD, ENABLED " +
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

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try {
            String queryScript = "select ID, USERNAME, PASSWORD, ENABLED " +
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

    @Override
    public void insert(User metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() +
                " (ID, USERNAME, PASSWORD, ENABLED) VALUES (" +
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(metadata.getUsername()) + ", " +
                SQLTools.GetStringForSQL(metadata.getPassword()) + ", " +
                SQLTools.GetStringForSQL(metadata.isEnabled()) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    @Override
    public void update(User metadata) {
        String updateStatement = "UPDATE " + getMetadataRepository().getTableNameByLabel("Users") +
                " SET USERNAME = " + SQLTools.GetStringForSQL(metadata.getUsername()) + ", " +
                "PASSWORD = " + SQLTools.GetStringForSQL(metadata.getPassword()) + ", " +
                "ENABLED = " + SQLTools.GetStringForSQL(metadata.isEnabled()) +
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

    public void addAuthority(UserKey userKey, AuthorityKey authorityKey) {
        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserAuthorities").getName() +
                " (USER_ID, AUTHORITY_ID) VALUES (" +
                SQLTools.GetStringForSQL(userKey.getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(authorityKey.getUuid().toString()) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void removeAuthority(UserKey userKey, AuthorityKey authorityKey) {
        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserAuthorities").getName() +
                " WHERE AUTHORITY_ID=" + SQLTools.GetStringForSQL(authorityKey.getUuid().toString()) + " and " +
                "USER_ID= " + SQLTools.GetStringForSQL(userKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public User mapUser(CachedRowSet cachedRowSet) throws SQLException {
        return new User(new UserKey(UUID.fromString(cachedRowSet.getString("ID"))),
                cachedRowSet.getString("USERNAME"),
                cachedRowSet.getString("PASSWORD"),
                SQLTools.getBooleanFromSql(cachedRowSet.getString("ENABLED")));
    }

    public Optional<User> getByName(String name) {
        try {
            String queryScript = "select ID, USERNAME, PASSWORD, ENABLED " +
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

}
