package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
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
import java.util.UUID;

@Component
public class ConnectionConfiguration extends Configuration<Connection, ConnectionKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final ConnectionParameterConfiguration connectionParameterConfiguration;

    public ConnectionConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration, ConnectionParameterConfiguration connectionParameterConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.connectionParameterConfiguration = connectionParameterConfiguration;
    }

    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getControlMetadataRepository());
    }

    @Override
    public Optional<Connection> get(ConnectionKey connectionKey) {
        try {
            String query = "select CONN_NM, CONN_TYP_NM, CONN_DSC, SECURITY_GROUP_ID, SECURITY_GROUP_NM from " +
                    getMetadataRepository().getTableNameByLabel("Connections") +
                    " WHERE " +
                    " CONN_NM  = " + SQLTools.getStringForSQL(connectionKey.getName()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", connectionKey.toString()));
            }
            cachedRowSet.next();
            List<ConnectionParameter> connectionParameters = connectionParameterConfiguration.getByConnection(connectionKey);
            if (connectionParameters.isEmpty()) {
                return Optional.empty();
            } else {
                SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.fromString(cachedRowSet.getString("SECURITY_GROUP_ID")));
                return Optional.of(
                        new Connection(
                                connectionKey,
                                securityGroupKey,
                                cachedRowSet.getString("SECURITY_GROUP_NM"),
                                cachedRowSet.getString("CONN_TYP_NM"),
                                cachedRowSet.getString("CONN_DSC"), connectionParameters));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Connection> getAll() {
        List<Connection> connections = new ArrayList<>();
        String query = "select CONN_NM, CONN_TYP_NM, CONN_DSC, SECURITY_GROUP_ID, SECURITY_GROUP_NM from " + getMetadataRepository().getTableNameByLabel("Connections")
                + " order by CONN_NM ASC";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                String queryConnectionParameters = "select DISTINCT ENV_NM from "
                        + getMetadataRepository().getTableNameByLabel("ConnectionParameters")
                        + " where CONN_NM = " + SQLTools.getStringForSQL(crs.getString("CONN_NM")) + ";";
                CachedRowSet environmentCachedRowSet = getMetadataRepository().executeQuery(queryConnectionParameters, "reader");
                while (environmentCachedRowSet.next()) {
                    ConnectionKey connectionKey = new ConnectionKey(crs.getString("CONN_NM"), environmentCachedRowSet.getString("ENV_NM"));
                    SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.fromString(crs.getString("SECURITY_GROUP_ID")));
                    connections.add(new Connection(
                            connectionKey,
                            securityGroupKey,
                            crs.getString("SECURITY_GROUP_NM"),
                            crs.getString("CONN_TYP_NM"),
                            crs.getString("CONN_DSC"),
                            connectionParameterConfiguration.getByConnection(connectionKey)));
                }
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return connections;
    }

    @Override
    public void delete(ConnectionKey connectionKey) {
        LOGGER.trace(MessageFormat.format("Deleting Connection {0}.", connectionKey.toString()));
        if (!exists(connectionKey)) {
            throw new MetadataDoesNotExistException(connectionKey);
        }
        connectionParameterConfiguration.deleteByConnection(connectionKey);

        getDeleteQuery(connectionKey).ifPresent(getMetadataRepository()::executeUpdate);
    }

    private Optional<String> getDeleteQuery(ConnectionKey connectionKey) {
        try {
            // If this was the last remaining connection with name CONN_NM, remove entirely from connections
            String countQuery = "SELECT COUNT(DISTINCT ENV_NM) AS total_environments FROM "
                    + getMetadataRepository().getTableNameByLabel("ConnectionParameters")
                    + " WHERE ENV_NM != " + SQLTools.getStringForSQL(connectionKey.getEnvironmentKey().getName()) +
                    " AND CONN_NM = " + SQLTools.getStringForSQL(connectionKey.getName()) + ";";
            CachedRowSet crs = getMetadataRepository().executeQuery(countQuery, "reader");

            if (crs.next() && Integer.parseInt(crs.getString("total_environments")) == 0) {
                return Optional.of("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Connections") +
                        " WHERE CONN_NM = " + SQLTools.getStringForSQL(connectionKey.getName()) + ";");
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Connection connection) {
        // frameworkInstance.getFrameworkLog().log(MessageFormat.format("Inserting connection {0}-{1}.", connection.getScriptName(), connection.getEnvironment()), Level.TRACE);
        LOGGER.trace(MessageFormat.format("Inserting Connection {0}.", connection.getMetadataKey().toString()));
        if (exists(connection.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(connection);
        }
        connection.getParameters()
                .forEach(connectionParameter -> connectionParameterConfiguration.insert(connectionParameter));
        insertStatement(connection).ifPresent(getMetadataRepository()::executeUpdate);
    }

    private Optional<String> insertStatement(Connection connection) {
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery("SELECT * FROM " + getMetadataRepository().getTableNameByLabel("Connections") +
                " WHERE CONN_NM = " + SQLTools.getStringForSQL(connection.getMetadataKey().getName()) + ";", "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.of("INSERT INTO " + getMetadataRepository().getTableNameByLabel("Connections") +
                    " (CONN_NM, SECURITY_GROUP_ID, SECURITY_GROUP_NM, CONN_TYP_NM, CONN_DSC) VALUES (" +
                    SQLTools.getStringForSQL(connection.getMetadataKey().getName()) + "," +
                    SQLTools.getStringForSQL(connection.getSecurityGroupKey().getUuid()) + "," +
                    SQLTools.getStringForSQL(connection.getSecurityGroupName()) + "," +
                    SQLTools.getStringForSQL(connection.getType()) + "," +
                    SQLTools.getStringForSQL(connection.getDescription()) + ");");
        } else {
            return Optional.empty();
        }


    }

    public List<Connection> getByName(String connectionName) {
        List<Connection> connections = new ArrayList<>();
        String queryConnection = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + getMetadataRepository().getTableNameByLabel("Connections")
                + " where CONN_NM = " + SQLTools.getStringForSQL(connectionName) + ";";
        CachedRowSet crsConnection = getMetadataRepository().executeQuery(queryConnection, "reader");

        try {
            crsConnection.next();
            if (crsConnection.size() == 0) {
                return connections;
            } else if (crsConnection.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", connectionName));
            }

            // Get parameters
            String queryConnectionParameters = "select DISTINCT ENV_NM from "
                    + getMetadataRepository().getTableNameByLabel("ConnectionParameters")
                    + " where CONN_NM = " + SQLTools.getStringForSQL(connectionName) + ";";
            CachedRowSet crsConnectionEnvironments = getMetadataRepository().executeQuery(queryConnectionParameters, "reader");
            while (crsConnectionEnvironments.next()) {
                get(new ConnectionKey(connectionName, crsConnectionEnvironments.getString("ENV_NM")))
                        .ifPresent(connections::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connections;
    }

    public List<Connection> getByEnvironment(String environmentName) {
        List<Connection> connections = new ArrayList<>();

        String connectionsByEnvironmentQuery = "select distinct CONN_NM from "
                + getMetadataRepository().getTableNameByLabel("ConnectionParameters")
                + " where ENV_NM = " + SQLTools.getStringForSQL(environmentName) + ";";
        CachedRowSet connectionsByEnvironment = getMetadataRepository().executeQuery(connectionsByEnvironmentQuery, "reader");
        try {
            while (connectionsByEnvironment.next()) {
                get(new ConnectionKey(connectionsByEnvironment.getString("CONN_NM"), environmentName))
                        .ifPresent(connections::add);
            }
            connectionsByEnvironment.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connections;
    }

    public void deleteAll() {
        List<String> query = getDeleteAllStatement();
        getMetadataRepository().executeBatch(query);
    }

    private List<String> getDeleteAllStatement() {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Connections") + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") + ";");
        return queries;
    }

    public void deleteByName(String connectionName) {
        List<String> deleteQuery = getDeleteByNameQuery(connectionName);
        getMetadataRepository().executeBatch(deleteQuery);
    }

    private List<String> getDeleteByNameQuery(String connectionName) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Connections") +
                " WHERE CONN_NM = "
                + SQLTools.getStringForSQL(connectionName) + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " WHERE CONN_NM = "
                + SQLTools.getStringForSQL(connectionName) + ";");
        return queries;
    }

    public boolean exists(String connectionName) {
        CachedRowSet crsConnection;
        String queryConnection = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + getMetadataRepository().getTableNameByLabel("Connections")
                + " where CONN_NM = " + SQLTools.getStringForSQL(connectionName) + ";";
        crsConnection = getMetadataRepository().executeQuery(queryConnection, "reader");
        return crsConnection.size() > 0;
    }

    public void update(Connection connection) {
        connectionParameterConfiguration.deleteByConnection(
                connection.getMetadataKey()
        );
        for (ConnectionParameter connectionParameter : connection.getParameters()) {
            connectionParameterConfiguration.insert(connectionParameter);
        }
        getMetadataRepository().executeUpdate("UPDATE " + getMetadataRepository().getTableNameByLabel("Connections") +
                " SET CONN_TYP_NM = " + SQLTools.getStringForSQL(connection.getType()) +
                " , CONN_DSC = " + SQLTools.getStringForSQL(connection.getDescription()) +
                " WHERE CONN_NM = " + SQLTools.getStringForSQL(connection.getMetadataKey().getName()) + ";");
    }
}