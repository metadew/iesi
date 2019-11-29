package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
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

public class ConnectionParameterConfiguration extends Configuration<ConnectionParameter, ConnectionParameterKey> {

    private static ConnectionParameterConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    private ConnectionParameter connectionParameter;

    public synchronized static ConnectionParameterConfiguration getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new ConnectionParameterConfiguration();
        }
        return INSTANCE;
    }

    public ConnectionParameterConfiguration() {
    }

    @Override
    public Optional<ConnectionParameter> get(ConnectionParameterKey metadataKey) {
        try{
            String query = "select CONN_NM, CONN_PAR_NM, CONN_PAR_VAL, ENV_NM from " +
                    MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Connections") +
                    " WHERE " +
                    " CONN_NM  = " + metadataKey.getConnectionName() + " AND " +
                    " ENV_NM  = " + metadataKey.getEnvironment() + " AND " +
                    " CONN_PAR_NM = " + metadataKey.getParameterName() + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", metadataKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ConnectionParameter(metadataKey, cachedRowSet.getString("CONN_PAR_VAL")));
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ConnectionParameter> getAll() {
        List<ConnectionParameter> connectionParameters = new ArrayList<>();
        String query = "select * from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
                + " order by CONN_NM ASC";
        CachedRowSet crs = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                connectionParameters.add(new ConnectionParameter(
                        crs.getString("CONN_NM"),
                        crs.getString("ENV_NM"),
                        crs.getString("CONN_PAR_NM"),
                        crs.getString("CONN_PAR_VAL")));

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
    public void delete(ConnectionParameterKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting Connection {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException("ConnectionParameter", metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ConnectionParameterKey metadataKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " WHERE " +
                " CONN_NM = " + SQLTools.GetStringForSQL(metadataKey.getConnectionName()) + " AND " +
                " ENV_NM = " + SQLTools.GetStringForSQL(metadataKey.getEnvironment())+ " AND " +
                " CONN_PAR_NM = " + SQLTools.GetStringForSQL(metadataKey.getParameterName()) + ";";
    }

    @Override
    public void insert(ConnectionParameter metadata) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting Connection {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException("ConnectionParameter", metadata.getMetadataKey());
        }
        String insertStatement = insertStatement(metadata);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public String insertStatement(ConnectionParameter metadata){
        return "INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " (CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getConnectionName()) + "," +
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getEnvironment())+ "," +
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getParameterName()) + "," +
                SQLTools.GetStringForSQL(metadata.getValue()) + ");";
    }

    // Insert
    public String getInsertStatement(String connectionName, String environmentName) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters");
        sql += " (CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(connectionName);
        sql += ",";
        sql += SQLTools.GetStringForSQL(environmentName);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getConnectionParameter().getMetadataKey().getParameterName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getConnectionParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public String getInsertStatement(String connectionName, String environmentName, ConnectionParameter connectionParameter) {
        return "INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " (CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(connectionName) + "," +
                SQLTools.GetStringForSQL(environmentName)+ "," +
                SQLTools.GetStringForSQL(connectionParameter.getMetadataKey().getParameterName()) + "," +
                SQLTools.GetStringForSQL(connectionParameter.getValue()) + ");";
    }

    public Optional<ConnectionParameter> getConnectionParameter(String connectionName, String environmentName, String connectionParameterName) {
        ConnectionParameterKey connectionParameterKey =
                new ConnectionParameterKey(connectionName, environmentName, connectionParameterName);
        return get(connectionParameterKey);
    }

    public Optional<String> getConnectionParameterValue(String connectionName, String environmentName,
                                                        String connectionParameterName) {
        ConnectionParameterKey connectionParameterKey =
                new ConnectionParameterKey(connectionName, environmentName, connectionParameterName);
        Optional<ConnectionParameter> connectionParameterOptional = get(connectionParameterKey);
        return connectionParameterOptional.isPresent() ?
                Optional.of(connectionParameterOptional.get().getValue()) : Optional.empty();
    }


    // Getters and Setters
    public ConnectionParameter getConnectionParameter() {
        return connectionParameter;
    }

    public void setConnectionParameter(ConnectionParameter connectionParameter) {
        this.connectionParameter = connectionParameter;
    }
}