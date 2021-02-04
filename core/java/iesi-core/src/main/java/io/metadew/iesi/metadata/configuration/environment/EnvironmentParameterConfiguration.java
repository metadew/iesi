package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class EnvironmentParameterConfiguration extends Configuration<EnvironmentParameter, EnvironmentParameterKey> {

    private static EnvironmentParameterConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static EnvironmentParameterConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EnvironmentParameterConfiguration();
        }
        return INSTANCE;
    }

    private EnvironmentParameterConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository());
    }

    @Override
    public boolean exists(EnvironmentParameterKey environmentParameterKey) {
        String queryEnvironmentParameter = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from " +
                getMetadataRepository().getTableNameByLabel("EnvironmentParameters") +
                " where ENV_NM = " + SQLTools.getStringForSQL(environmentParameterKey.getEnvironmentKey().getName()) +
                " and ENV_PAR_NM = " + SQLTools.getStringForSQL(environmentParameterKey.getParameterName()) + ";";
        CachedRowSet crsEnvironmentParameter = getMetadataRepository()
                .executeQuery(queryEnvironmentParameter, "reader");
        if (crsEnvironmentParameter.size() == 0) {
            return false;
        } else if (crsEnvironmentParameter.size() > 1) {
            LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", environmentParameterKey.toString()));
        }
        return true;
    }

    @Override
    public Optional<EnvironmentParameter> get(EnvironmentParameterKey metadataKey) {
        String queryEnvironmentParameter = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from " +
                getMetadataRepository().getTableNameByLabel("EnvironmentParameters") +
                " where ENV_NM = " + SQLTools.getStringForSQL(metadataKey.getEnvironmentKey().getName()) +
                " and ENV_PAR_NM = " + SQLTools.getStringForSQL(metadataKey.getParameterName()) + ";";
        CachedRowSet crsEnvironmentParameter = getMetadataRepository()
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<EnvironmentParameter> getAll() {
        List<EnvironmentParameter> environmentParameters = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("EnvironmentParameters")
                + " order by ENV_NM ASC";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                environmentParameters.add(new EnvironmentParameter(
                        crs.getString("ENV_NM"),
                        crs.getString("ENV_PAR_NM"),
                        crs.getString("ENV_PAR_VAL")));

            }
            crs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return environmentParameters;
    }

    @Override
    public void delete(EnvironmentParameterKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting Connection {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException(metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(EnvironmentParameterKey metadataKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("EnvironmentParameters") +
                " WHERE " +
                " ENV_NM = " + SQLTools.getStringForSQL(metadataKey.getEnvironmentKey().getName()) + " AND " +
                " ENV_PAR_NM = " + SQLTools.getStringForSQL(metadataKey.getParameterName()) + ";";
    }

    @Override
    public void insert(EnvironmentParameter metadata) {
        LOGGER.trace(MessageFormat.format("Inserting Connection {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(metadata.getMetadataKey());
        }
        String insertStatement = getInsertStatement(metadata);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public String getInsertStatement(EnvironmentParameter environmentParameter) {
        return "INSERT INTO " + getMetadataRepository()
                .getTableNameByLabel("EnvironmentParameters") + " (ENV_NM, ENV_PAR_NM, ENV_PAR_VAL) VALUES (" +
                SQLTools.getStringForSQL(environmentParameter.getMetadataKey().getEnvironmentKey().getName()) + "," +
                SQLTools.getStringForSQL(environmentParameter.getName()) + "," +
                SQLTools.getStringForSQL(environmentParameter.getValue()) + ");";
    }

    public void deleteByEnvironment(EnvironmentKey environmentKey) {
        getMetadataRepository().executeUpdate("DELETE FROM " + getMetadataRepository().getTableNameByLabel("EnvironmentParameters") +
                " WHERE " +
                " ENV_NM = " + SQLTools.getStringForSQL(environmentKey.getName()) + ";");
    }

    public List<EnvironmentParameter> getByEnvironment(EnvironmentKey environmentKey) {
        List<EnvironmentParameter> environmentParameters = new ArrayList<>();
        String queryEnvironmentParameter = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from "
                + getMetadataRepository().getTableNameByLabel("EnvironmentParameters")
                + " where ENV_NM = " + SQLTools.getStringForSQL(environmentKey.getName()) + ";";
        CachedRowSet crsEnvironmentParameter = getMetadataRepository()
                .executeQuery(queryEnvironmentParameter, "reader");
        try {
            while (crsEnvironmentParameter.next()) {
                environmentParameters.add(new EnvironmentParameter(environmentKey.getName(),
                        crsEnvironmentParameter.getString("ENV_PAR_NM"),
                        crsEnvironmentParameter.getString("ENV_PAR_VAL")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return environmentParameters;
    }

}