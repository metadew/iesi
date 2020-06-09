package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.user.Authority;
import io.metadew.iesi.metadata.definition.user.Group;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
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
    }

    @Override
    public Optional<User> get(UserKey metadataKey) {
        try {
            String queryScript = "select ID, USERNAME, PASSWORD, ENABLED from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users") +
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
            String queryScript = "select ID, USERNAME, PASSWORD, ENABLED from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users") + ";";
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
        String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users") +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    @Override
    public void insert(User metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatement = "INSERT INTO FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users") +
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
        return new ArrayList<>();
    }

    public List<Authority> getAuthorities(UserKey userKey) {
        return new ArrayList<>();
    }

    public User mapUser(CachedRowSet cachedRowSet) throws SQLException {
        return new User(new UserKey(UUID.fromString(cachedRowSet.getString("ID"))),
                cachedRowSet.getString("USERNAME"),
                cachedRowSet.getString("PASSWORD"),
                SQLTools.getBooleanFromSql(cachedRowSet.getString("ENABLED")));
    }

}
