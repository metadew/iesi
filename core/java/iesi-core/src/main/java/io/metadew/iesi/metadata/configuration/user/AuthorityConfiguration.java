package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.user.Authority;
import io.metadew.iesi.metadata.definition.user.AuthorityKey;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public class AuthorityConfiguration extends Configuration<Authority, AuthorityKey> {
    private static AuthorityConfiguration INSTANCE;

    public synchronized static AuthorityConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuthorityConfiguration();
        }
        return INSTANCE;
    }

    private AuthorityConfiguration() {
    }

    @Override
    public Optional<Authority> get(AuthorityKey metadataKey) {
        try {
            String queryScript = "select ID, AUTHORITY from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities") +
                    " WHERE ID=" + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            if (cachedRowSet.next()) {
                return Optional.of(mapAuthority(cachedRowSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Authority> getAll() {
        List<Authority> authorities = new ArrayList<>();
        try {
            String queryScript = "select ID, AUTHORITY from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                authorities.add(mapAuthority(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorities;
    }

    @Override
    public void delete(AuthorityKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey.toString()));
        String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities") +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    @Override
    public void insert(Authority metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatement = "INSERT INTO FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities") +
                " (ID, AUTHORITY) VALUES (" +
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(metadata.getAuthority()) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    @Override
    public void update(Authority metadata) {
        log.trace(MessageFormat.format("Updating {0}.", metadata.toString()));
        String updateStatement = "UPDATE " + getMetadataRepository().getTableNameByLabel("Authorities") +
                " SET AUTHORITY = " + SQLTools.GetStringForSQL(metadata.getAuthority()) +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(updateStatement);
    }

    public Authority mapAuthority(CachedRowSet cachedRowSet) throws SQLException {
        return new Authority(new AuthorityKey(UUID.fromString(cachedRowSet.getString("ID"))),
                cachedRowSet.getString("AUTHORITY"));
    }
}
