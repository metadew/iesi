package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
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

    public synchronized static ConnectionParameterConfiguration getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new ConnectionParameterConfiguration();
        }
        return INSTANCE;
    }

    private ConnectionParameterConfiguration() {    }


    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ConnectionParameter> get(ConnectionParameterKey connectionParameterKey) {
        try{
            String query = "select CONN_NM, CONN_PAR_NM, CONN_PAR_VAL, ENV_NM from " +
                    getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                    " WHERE " +
                    " CONN_NM  = " + SQLTools.GetStringForSQL(connectionParameterKey.getConnectionKey().getName()) + " AND " +
                    " ENV_NM  = " + SQLTools.GetStringForSQL(connectionParameterKey.getConnectionKey().getEnvironmentKey().getName()) + " AND " +
                    " CONN_PAR_NM = " + SQLTools.GetStringForSQL(connectionParameterKey.getParameterName()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", connectionParameterKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ConnectionParameter(connectionParameterKey, cachedRowSet.getString("CONN_PAR_VAL")));
        }catch (SQLException e) {
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
                " CONN_NM = " + SQLTools.GetStringForSQL(connectionParameterKey.getConnectionKey().getName()) + " AND " +
                " ENV_NM = " + SQLTools.GetStringForSQL(connectionParameterKey.getConnectionKey().getEnvironmentKey().getName())+ " AND " +
                " CONN_PAR_NM = " + SQLTools.GetStringForSQL(connectionParameterKey.getParameterName()) + ";";
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

    public String insertStatement(ConnectionParameter connectionParameter){
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " (CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(connectionParameter.getMetadataKey().getConnectionKey().getName()) + "," +
                SQLTools.GetStringForSQL(connectionParameter.getMetadataKey().getConnectionKey().getEnvironmentKey().getName())+ "," +
                SQLTools.GetStringForSQL(connectionParameter.getMetadataKey().getParameterName()) + "," +
                SQLTools.GetStringForSQL(connectionParameter.getValue()) + ");";
    }

}