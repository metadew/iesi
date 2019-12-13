package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
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

public class EnvironmentConfiguration extends Configuration<Environment, EnvironmentKey> {

    private static EnvironmentConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static EnvironmentConfiguration getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new EnvironmentConfiguration();
        }
        return INSTANCE;
    }

    private EnvironmentConfiguration() {}

    public void init(MetadataRepository metadataRepository){
        setMetadataRepository(metadataRepository);
        EnvironmentParameterConfiguration.getInstance().init(metadataRepository);
    }

    @Override
    public Optional<Environment> get(EnvironmentKey metadataKey) {
        return getEnvironment(metadataKey.getName());
    }

    @Override
    public List<Environment> getAll() {
        return getAllEnvironments();
    }

    @Override
    public void delete(EnvironmentKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting Environment {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException("Environment", metadataKey);
        }
        List<String> deleteStatements = getDeleteStatement(metadataKey.getName());
        getMetadataRepository().executeBatch(deleteStatements);
    }

    @Override
    public void insert(Environment metadata) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting Environment {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException("Environment", metadata.getMetadataKey());
        }
        List<String> insertQuery = getInsertStatement(metadata);
        getMetadataRepository().executeBatch(insertQuery);
    }
    
    // Methods
    public Optional<Environment> getEnvironment(String environmentName) {
        MetadataRepository metadataRepository = getMetadataRepository();
        String queryEnvironment = "select ENV_NM, ENV_DSC from "
                + getMetadataRepository().getTableNameByLabel("Environments") + " where ENV_NM = "
                + SQLTools.GetStringForSQL(environmentName) + ";";
        CachedRowSet crsEnvironment = getMetadataRepository().executeQuery(queryEnvironment, "reader");
        EnvironmentParameterConfiguration environmentParameterConfiguration = EnvironmentParameterConfiguration.getInstance();
        try {
            crsEnvironment.next();
            // Get parameters
            String queryEnvironmentParameters = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from "
                    + getMetadataRepository().getTableNameByLabel("EnvironmentParameters")
                    + " where ENV_NM = '" + environmentName + "'";
            CachedRowSet crsEnvironmentParameters = getMetadataRepository()
                    .executeQuery(queryEnvironmentParameters, "reader");
            List<EnvironmentParameter> environmentParameters = new ArrayList<>();
            while (crsEnvironmentParameters.next()) {
                environmentParameters.add(environmentParameterConfiguration.getEnvironmentParameter(environmentName,
                        crsEnvironmentParameters.getString("ENV_PAR_NM")));
            }
            Environment environment = new Environment(environmentName, crsEnvironment.getString("ENV_DSC"), environmentParameters);
            crsEnvironmentParameters.close();
            crsEnvironment.close();
            return Optional.of(environment);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
    }

    public boolean exists(Environment environment) {
        return exists(environment.getName());
    }

    public boolean exists(String environmentName) {
        String queryEnvironment = "select * from "
                + getMetadataRepository().getTableNameByLabel("Environments")
                + " where ENV_NM = "
                + SQLTools.GetStringForSQL(environmentName) + ";";
        CachedRowSet crsEnvironment = getMetadataRepository().executeQuery(queryEnvironment, "reader");
        return crsEnvironment.size() >= 1;
    }

    public List<Environment> getAllEnvironments() {
        List<Environment> environments = new ArrayList<>();
        String query = "select ENV_NM, ENV_DSC from " + getMetadataRepository().getTableNameByLabel("Environments")
                + " order by ENV_NM ASC";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                String environmentName = crs.getString("ENV_NM");
                String environmentDescription = crs.getString("ENV_DSC");

                List<EnvironmentParameter> environmentParameters = new ArrayList<>();
                String parameterQuery = "select ENV_PAR_NM, ENV_PAR_VAL from "
                        + getMetadataRepository().getTableNameByLabel("EnvironmentParameters")
                        + " where ENV_NM = " + SQLTools.GetStringForSQL(environmentName) + ";";
                CachedRowSet crsParameters = getMetadataRepository().executeQuery(parameterQuery, "reader");
                while(crsParameters.next()) {
                    environmentParameters.add(new EnvironmentParameter(environmentName,
                            crsParameters.getString("ENV_PAR_NM"),
                            crsParameters.getString("ENV_PAR_VAL")));
                }
                environments.add(new Environment(environmentName, environmentDescription, environmentParameters));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return environments;
    }

    public void deleteEnvironment(Environment environment) throws EnvironmentDoesNotExistException {
        deleteEnvironment(environment.getName());
    }
    public void deleteEnvironment(String environmentName) throws EnvironmentDoesNotExistException {
        //TODO fix logging
        //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Deleting environment {0}", environment.getScriptName()), Level.TRACE);
        if (!exists(environmentName)) {
            throw new EnvironmentDoesNotExistException(
                    MessageFormat.format("Environment {0} is not present in the repository so cannot be updated", environmentName));
        }
        List<String> query = getDeleteStatement(environmentName);
        getMetadataRepository().executeBatch(query);
    }

    public List<String> getDeleteStatement(String environment) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Environments") +
                " WHERE ENV_NM = " +
                SQLTools.GetStringForSQL(environment) + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("EnvironmentParameters") +
                " WHERE ENV_NM = "
                + SQLTools.GetStringForSQL(environment) + ";");
        return queries;
    }

    public void deleteAllEnvironments() {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log("Deleting all environments", Level.TRACE);
        List<String> query = getDeleteAllStatement();
        getMetadataRepository().executeBatch(query);
    }

    private List<String> getDeleteAllStatement() {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Environments") + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("EnvironmentParameters") + ";");
        return queries;
    }

    public void insertEnvironment(Environment environment) throws EnvironmentAlreadyExistsException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting environment {0}", environment.getScriptName()), Level.TRACE);
        if (exists(environment)) {
            throw new EnvironmentAlreadyExistsException(MessageFormat.format("Environment {0} already exists", environment.getName()));
        }
        List<String> query = getInsertStatement(environment);
        getMetadataRepository().executeBatch(query);
    }

    public List<String> getInsertStatement(Environment environment) {
        EnvironmentParameterConfiguration environmentParameterConfiguration = EnvironmentParameterConfiguration.getInstance();
        List<String> queries = new ArrayList<>();
        queries.add("INSERT INTO " + getMetadataRepository().getTableNameByLabel("Environments") +
                " (ENV_NM, ENV_DSC) VALUES (" +
                SQLTools.GetStringForSQL(environment.getName()) + "," +
                SQLTools.GetStringForSQL(environment.getDescription())+ ");");


        for(EnvironmentParameter environmentParameter : environment.getParameters()) {
            queries.add(environmentParameterConfiguration.getInsertStatement(environmentParameter));
        }
        return queries;
    }

    public void updateEnvironment(Environment environment) throws EnvironmentDoesNotExistException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Updating environment {0}.", environment.getScriptName()), Level.TRACE);
        try {
            deleteEnvironment(environment);
            insertEnvironment(environment);
        } catch (EnvironmentDoesNotExistException e) {
            //TODO fix logging
        	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Environment {0} is not present in the repository so cannot be updated", environment.getScriptName()),Level.TRACE);
            throw new EnvironmentDoesNotExistException(MessageFormat.format(
                    "Environment {0} is not present in the repository so cannot be updated", environment.getName()));

        } catch (EnvironmentAlreadyExistsException e) {
            //TODO fix logging
        	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Environment {0} is not deleted correctly during update. {1}", environment.getScriptName(), e.toString()),Level.WARN);
        }
    }


}



