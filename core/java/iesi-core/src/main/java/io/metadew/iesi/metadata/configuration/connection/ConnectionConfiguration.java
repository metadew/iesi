package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.MetadataConfiguration;
import io.metadew.iesi.metadata.configuration.connection.exception.ConnectionAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.connection.exception.ConnectionDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConnectionConfiguration extends Configuration<Connection, ConnectionKey> {

    private final ConnectionParameterConfiguration connectionParameterConfiguration;
    private static ConnectionConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static ConnectionConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionConfiguration();
        }
        return INSTANCE;
    }

    public ConnectionConfiguration() {
        this.connectionParameterConfiguration = new ConnectionParameterConfiguration();
    }

    @Override
    public Optional<Connection> get(ConnectionKey metadataKey) {
        try {
            String query = "select CONN_NM, CONN_TYP_NM, CONN_DSC, ENV_NM from " +
                    MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Connections") +
                    " WHERE " +
                    " CONN_NM  = " + metadataKey.getName() + " AND " +
                    " ENV_NM = " + metadataKey.getEnvironment() + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", metadataKey.toString()));
            }
            cachedRowSet.next();
            List<ConnectionParameter> connectionParameters = getAllLinkedConnectionParameters(metadataKey);
            return Optional.of(new Connection(metadataKey, cachedRowSet.getString("CONN_TYP_NM"), cachedRowSet.getString("CONN_DSC"), connectionParameters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Connection> get(String connectionName, String environmentName){
        ConnectionKey connectionKey = new ConnectionKey(connectionName, environmentName);
        return get(connectionKey);
    }

    private List<ConnectionParameter> getAllLinkedConnectionParameters(ConnectionKey connectionKey) {
        List<ConnectionParameter> connectionParameters = new ArrayList<>();
        try {
            String query = "select CONN_PAR_NM, CONN_PAR_VAL  from " +
                    MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                    " WHERE " +
                    " CONN_NM  = " + connectionKey.getName() + " AND " +
                    " ENV_NM = " + connectionKey.getEnvironment() + ";";
            CachedRowSet crsConnectionParameters = getMetadataRepository().executeQuery(query, "reader");
            while (crsConnectionParameters.next()) {
                ConnectionParameter connectionParameter =
                        new ConnectionParameter(connectionKey.getName(), connectionKey.getEnvironment(),
                                crsConnectionParameters.getString("CONN_PAR_NM"), crsConnectionParameters.getString("CONN_PAR_VAL"));
                connectionParameters.add(connectionParameter);
            }
            crsConnectionParameters.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connectionParameters;
    }

    @Override
    public List<Connection> getAll() {
        List<Connection> connections = new ArrayList<>();
        String query = "select * from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Connections")
                + " order by CONN_NM ASC";
        CachedRowSet crs = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ConnectionKey connectionKey = new ConnectionKey(crs.getString("CONN_NM"), crs.getString("ENV_NM"));
                List<ConnectionParameter> connectionParameters = getAllLinkedConnectionParameters(connectionKey);
                connections.add(new Connection(
                        connectionKey,
                        crs.getString("CONN_TYP_NM"),
                        crs.getString("CONN_DSC"),
                        connectionParameters));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return connections;
    }

    @Override
    public void delete(ConnectionKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting Connection {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new ConnectionDoesNotExistException(MessageFormat.format(
                    "Connection {0} does not exists", metadataKey.toString()));
        }
        List<String> deleteStatements = getDeleteQuery(metadataKey.getName(), metadataKey.getEnvironment());
        getMetadataRepository().executeBatch(deleteStatements);
    }

    private List<String> getDeleteQuery(String name, String environment) {
        try {
            List<String> queries = new ArrayList<>();

            queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                    " WHERE CONN_NM = " + SQLTools.GetStringForSQL(name)
                    + "AND ENV_NM = " + SQLTools.GetStringForSQL(environment) + ";");

            // If this was the last remaining connection with name CONN_NM, remove entirely from connections
            String countQuery = "SELECT COUNT(DISTINCT ENV_NM ) AS total_environments FROM "
                    + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
                    + " WHERE ENV_NM != " + SQLTools.GetStringForSQL(environment) + ";";
            CachedRowSet crs = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(countQuery, "reader");

            if (crs.next() && Integer.parseInt(crs.getString("total_environments")) == 0) {
                queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Connections") +
                        " WHERE CONN_NM = " + SQLTools.GetStringForSQL(name) + ";");
            }

            return queries;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Connection connection) throws MetadataAlreadyExistsException {
        // frameworkInstance.getFrameworkLog().log(MessageFormat.format("Inserting connection {0}-{1}.", connection.getScriptName(), connection.getEnvironment()), Level.TRACE);
        LOGGER.trace(MessageFormat.format("Inserting Connection {0}.", connection.getMetadataKey().toString()));
        if (exists(connection.getMetadataKey())) {
            throw new ConnectionAlreadyExistsException(MessageFormat.format(
                    "Connection {0} already exists", connection.getMetadataKey().toString()));
        }
        List<String> insertQuery = getInsertQuery(connection);
        getMetadataRepository().executeBatch(insertQuery);
    }

    public String insertStatement(Connection connection) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("Connections") +
                " (CONN_NM, CONN_TYP_NM, CONN_DSC, ENV_NM) VALUES (" +
                SQLTools.GetStringForSQL(connection.getMetadataKey().getName()) + "," +
                SQLTools.GetStringForSQL(connection.getMetadataKey().getEnvironment()) + "," +
                SQLTools.GetStringForSQL(connection.getType()) + "," +
                SQLTools.GetStringForSQL(connection.getDescription()) + ";";
    }

    private List<String> getInsertQuery(Connection connection) {
        List<String> queries = new ArrayList<>();
        if (!exists(connection.getMetadataKey())) {
            queries.add(insertStatement(connection));
        }
        for (ConnectionParameter connectionParameter : connection.getParameters()) {
            queries.add(connectionParameterConfiguration.getInsertStatement(connection.getName(), connection.getEnvironment(), connectionParameter));
        }

        return queries;
    }

}