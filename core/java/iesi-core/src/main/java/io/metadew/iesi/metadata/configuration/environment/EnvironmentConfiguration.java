package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
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
public class EnvironmentConfiguration extends Configuration<Environment, EnvironmentKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final EnvironmentParameterConfiguration environmentParameterConfiguration;

    public EnvironmentConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration, EnvironmentParameterConfiguration environmentParameterConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.environmentParameterConfiguration = environmentParameterConfiguration;
    }

    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getControlMetadataRepository());
    }

    @Override
    public Optional<Environment> get(EnvironmentKey environmentKey) {
        String queryEnvironment = "select ENV_NM, ENV_DSC from "
                + getMetadataRepository().getTableNameByLabel("Environments") + " where ENV_NM = "
                + SQLTools.getStringForSQL(environmentKey.getName()) + ";";
        CachedRowSet crsEnvironment = getMetadataRepository().executeQuery(queryEnvironment, "reader");
        try {
            if (crsEnvironment.size() == 0) {
                return Optional.empty();
            }
            crsEnvironment.next();
            Environment environment = new Environment(environmentKey,
                    crsEnvironment.getString("ENV_DSC"),
                    environmentParameterConfiguration
                            .getByEnvironment(environmentKey));
            crsEnvironment.close();
            return Optional.of(environment);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Environment> getAll() {
        List<Environment> environments = new ArrayList<>();
        String query = "select ENV_NM, ENV_DSC from " + getMetadataRepository().getTableNameByLabel("Environments")
                + " order by ENV_NM ASC";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                String environmentName = crs.getString("ENV_NM");
                String environmentDescription = crs.getString("ENV_DSC");
                EnvironmentKey environmentKey = new EnvironmentKey(environmentName);
                environments.add(new Environment(environmentKey, environmentDescription, environmentParameterConfiguration.getByEnvironment(environmentKey)));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            throw new RuntimeException(e);
        }
        return environments;
    }

    @Override
    public void delete(EnvironmentKey environmentKey) {
        LOGGER.trace(MessageFormat.format("Deleting Environment {0}.", environmentKey.toString()));
        if (!exists(environmentKey)) {
            throw new MetadataDoesNotExistException(environmentKey);
        }

        environmentParameterConfiguration.deleteByEnvironment(environmentKey);
        String deleteStatements = getDeleteStatement(environmentKey);
        getMetadataRepository().executeUpdate(deleteStatements);
    }

    @Override
    public void insert(Environment environment) {
        LOGGER.trace(MessageFormat.format("Inserting Environment {0}.", environment.getMetadataKey().toString()));
        if (exists(environment.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(environment.getMetadataKey());
        }
        for (EnvironmentParameter environmentParameter : environment.getParameters()) {
            environmentParameterConfiguration.insert(environmentParameter);
        }
        getMetadataRepository().executeUpdate(getInsertStatement(environment));
    }

    public boolean exists(EnvironmentKey environmentKey) {
        String queryEnvironment = "select * from "
                + getMetadataRepository().getTableNameByLabel("Environments")
                + " where ENV_NM = "
                + SQLTools.getStringForSQL(environmentKey.getName()) + ";";
        CachedRowSet crsEnvironment = getMetadataRepository().executeQuery(queryEnvironment, "reader");
        return crsEnvironment.size() >= 1;
    }

    public String getDeleteStatement(EnvironmentKey environmentKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("Environments") +
                " WHERE ENV_NM = " +
                SQLTools.getStringForSQL(environmentKey.getName()) + ";";
    }

    public void deleteAll() {
        List<String> query = getDeleteAllStatement();
        getMetadataRepository().executeBatch(query);
    }

    private List<String> getDeleteAllStatement() {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Environments") + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("EnvironmentParameters") + ";");
        return queries;
    }

    public String getInsertStatement(Environment environment) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("Environments") +
                " (ENV_NM, ENV_DSC) VALUES (" +
                SQLTools.getStringForSQL(environment.getName()) + "," +
                SQLTools.getStringForSQL(environment.getDescription()) + ");";
    }

}



