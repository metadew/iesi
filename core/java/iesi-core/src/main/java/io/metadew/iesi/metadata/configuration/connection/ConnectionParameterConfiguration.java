package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ConnectionParameterConfiguration extends Configuration<ConnectionParameter, ConnectionParameterKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    public ConnectionParameterConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }


    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getControlMetadataRepository());
    }

    @Override
    public Optional<ConnectionParameter> get(ConnectionParameterKey connectionParameterKey) {
        try {
            String query = "select CONN_NM, CONN_PAR_NM, CONN_PAR_VAL, ENV_NM from " +
                    getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                    " WHERE " +
                    " CONN_NM  = " + SQLTools.getStringForSQL(connectionParameterKey.getConnectionKey().getName()) + " AND " +
                    " ENV_NM  = " + SQLTools.getStringForSQL(connectionParameterKey.getConnectionKey().getEnvironmentKey().getName()) + " AND " +
                    " CONN_PAR_NM = " + SQLTools.getStringForSQL(connectionParameterKey.getParameterName()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", connectionParameterKey.toString()));
            }
            cachedRowSet.next();
            String connectionParameterValue = SQLTools.getStringFromSQLClob(cachedRowSet, "CONN_PAR_VAL");
            return Optional.of(new ConnectionParameter(connectionParameterKey, connectionParameterValue));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ConnectionParameter> getAll() {
        List<ConnectionParameter> connectionParameters = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ConnectionParameters")
                + " order by CONN_NM ASC";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                connectionParameters.add(new ConnectionParameter(
                        crs.getString("CONN_NM"),
                        crs.getString("ENV_NM"),
                        crs.getString("CONN_PAR_NM"),
                        SQLTools.getStringFromSQLClob(crs, "CONN_PAR_VAL")));

            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return connectionParameters;
    }

    @Override
    public void delete(ConnectionParameterKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting Connection {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException(metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ConnectionParameterKey connectionParameterKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " WHERE " +
                " CONN_NM = " + SQLTools.getStringForSQL(connectionParameterKey.getConnectionKey().getName()) + " AND " +
                " ENV_NM = " + SQLTools.getStringForSQL(connectionParameterKey.getConnectionKey().getEnvironmentKey().getName()) + " AND " +
                " CONN_PAR_NM = " + SQLTools.getStringForSQL(connectionParameterKey.getParameterName()) + ";";
    }

    @Override
    public void insert(ConnectionParameter connectionParameter) {
        LOGGER.trace(MessageFormat.format("Inserting Connection {0}.", connectionParameter.getMetadataKey().toString()));
        if (exists(connectionParameter.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(connectionParameter.getMetadataKey());
        }
        String insertStatement = insertStatement(connectionParameter);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public String insertStatement(ConnectionParameter connectionParameter) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " (CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL) VALUES (" +
                SQLTools.getStringForSQL(connectionParameter.getMetadataKey().getConnectionKey().getName()) + "," +
                SQLTools.getStringForSQL(connectionParameter.getMetadataKey().getConnectionKey().getEnvironmentKey().getName()) + "," +
                SQLTools.getStringForSQL(connectionParameter.getMetadataKey().getParameterName()) + "," +
                SQLTools.getStringForSQLClob(connectionParameter.getValue(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) + ");";
    }

    public void deleteByConnection(ConnectionKey connectionKey) {
        getMetadataRepository().executeUpdate("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " WHERE " +
                " CONN_NM = " + SQLTools.getStringForSQL(connectionKey.getName()) + " AND " +
                " ENV_NM = " + SQLTools.getStringForSQL(connectionKey.getEnvironmentKey().getName()) + ";");
    }

    public List<ConnectionParameter> getByConnection(ConnectionKey connectionKey) {
        List<ConnectionParameter> connectionParameters = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " WHERE CONN_NM = " + SQLTools.getStringForSQL(connectionKey.getName()) +
                " AND ENV_NM = " + SQLTools.getStringForSQL(connectionKey.getEnvironmentKey().getName()) +
                " order by CONN_NM ASC";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                connectionParameters.add(new ConnectionParameter(
                        crs.getString("CONN_NM"),
                        crs.getString("ENV_NM"),
                        crs.getString("CONN_PAR_NM"),
                        SQLTools.getStringFromSQLClob(crs, "CONN_PAR_VAL")));

            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return connectionParameters;
    }

    public void update(ConnectionParameter connectionParameter) {
        if (exists(connectionParameter)) {
            getMetadataRepository().executeUpdate("UPDATE " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                    " SET CONN_PAR_VAL = " + SQLTools.getStringForSQLClob(connectionParameter.getValue(),
                            getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                    .findFirst()
                                    .orElseThrow(RuntimeException::new)) +
                    " WHERE CONN_NM = " + SQLTools.getStringForSQL(connectionParameter.getMetadataKey().getConnectionKey().getName()) +
                    " AND ENV_NM = " + SQLTools.getStringForSQL(connectionParameter.getMetadataKey().getConnectionKey().getEnvironmentKey().getName()) +
                    " AND CONN_PAR_NM = " + SQLTools.getStringForSQL(connectionParameter.getMetadataKey().getParameterName()) + ";");
        } else {
            insert(connectionParameter);
        }
    }

}