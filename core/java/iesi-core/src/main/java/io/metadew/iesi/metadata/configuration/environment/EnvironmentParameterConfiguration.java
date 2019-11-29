package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionParameterConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;
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

public class EnvironmentParameterConfiguration extends Configuration<EnvironmentParameter, EnvironmentParameterKey> {

    private EnvironmentParameter environmentParameter;

    private static EnvironmentParameterConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static EnvironmentParameterConfiguration getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new EnvironmentParameterConfiguration();
        }
        return INSTANCE;
    }

    // Constructors
    public EnvironmentParameterConfiguration(EnvironmentParameter environmentParameter) {
        this.setEnvironmentParameter(environmentParameter);
    }

    public EnvironmentParameterConfiguration() {
    }



    @Override
    public Optional<EnvironmentParameter> get(EnvironmentParameterKey metadataKey) {
        String queryEnvironmentParameter = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from "
                + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("EnvironmentParameters")
                + " where ENV_NM = '" + metadataKey.getEnvironmentName() + "' and ENV_PAR_NM = '" + metadataKey.getParameterName() + "'";
        CachedRowSet crsEnvironmentParameter = MetadataControl.getInstance().getConnectivityMetadataRepository()
                .executeQuery(queryEnvironmentParameter, "reader");
        try {
            if (crsEnvironmentParameter.size() == 0) {
                return Optional.empty();
            } else if (crsEnvironmentParameter.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", metadataKey.toString()));
            }
            crsEnvironmentParameter.next();
            String environmentParameterValue = crsEnvironmentParameter.getString("ENV_PAR_VAL");
            crsEnvironmentParameter.close();
            return Optional.of(new EnvironmentParameter(metadataKey, environmentParameterValue));
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            throw new RuntimeException();
        }
    }

    @Override
    public List<EnvironmentParameter> getAll() {
        List<EnvironmentParameter> environmentParameters = new ArrayList<>();
        String query = "select * from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters")
                + " order by ENV_NM ASC";
        CachedRowSet crs = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                environmentParameters.add(new EnvironmentParameter(
                        crs.getString("ENV_NM"),
                        crs.getString("ENV_PAR_NM"),
                        crs.getString("ENV_PAR_VAL")));

            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return environmentParameters;
    }

    @Override
    public void delete(EnvironmentParameterKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting Connection {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException("EnvironmentParameter", metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(EnvironmentParameterKey metadataKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("EnvironmentParameters") +
                " WHERE " +
                " ENV_NM = " + SQLTools.GetStringForSQL(metadataKey.getEnvironmentName()) + " AND " +
                " ENV_PAR_NM = " + SQLTools.GetStringForSQL(metadataKey.getParameterName()) + ";";
    }

    @Override
    public void insert(EnvironmentParameter metadata) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting Connection {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException("EnvironmentParameter", metadata.getMetadataKey());
        }
        String insertStatement = getInsertStatement(metadata);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public String getInsertStatement(EnvironmentParameter environmentParameter) {
        return "INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("EnvironmentParameters") + " (ENV_NM, ENV_PAR_NM, ENV_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(environmentParameter.getMetadataKey().getEnvironmentName()) + "," +
                SQLTools.GetStringForSQL(environmentParameter.getName()) + "," +
                SQLTools.GetStringForSQL(environmentParameter.getValue()) + ");";
    }

    EnvironmentParameter getEnvironmentParameter(String environmentName, String environmentParameterName) {
        EnvironmentParameterKey environmentParameterKey = new EnvironmentParameterKey(environmentName, environmentParameterName);
        Optional<EnvironmentParameter> environmentParameterOpt = get(environmentParameterKey);
        return environmentParameterOpt.get();
    }

    public Optional<String> getEnvironmentParameterValue(String environmentName, String environmentParameterName) {
        EnvironmentParameterKey environmentParameterKey = new EnvironmentParameterKey(environmentName, environmentParameterName);
        Optional<EnvironmentParameter> environmentParameterOpt = get(environmentParameterKey);
        return environmentParameterOpt.map(EnvironmentParameter::getValue);
    }

    // Getters and Setters
    public EnvironmentParameter getEnvironmentParameter() {
        return environmentParameter;
    }

    private void setEnvironmentParameter(EnvironmentParameter environmentParameter) {
        this.environmentParameter = environmentParameter;
    }

}