package io.metadew.iesi.metadata.configuration.environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.configuration.DataObjectConfiguration;
import io.metadew.iesi.metadata.configuration.MetadataConfiguration;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentDoesNotExistException;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.ListObject;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnvironmentConfiguration extends MetadataConfiguration {

    private Environment environment;

    // Constructors
    public EnvironmentConfiguration() {
    }

    public EnvironmentConfiguration(Environment environment) {
        this.setEnvironment(environment);
    }
    
    // Abstract method implementations
	@Override
    public List<Environment> getAllObjects() {
    	return this.getAllEnvironments();
    }
    
    // Methods
    public Optional<Environment> getEnvironment(String environmentName) {
        Environment environment = null;
        String queryEnvironment = "select ENV_NM, ENV_DSC from "
                + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Environments") + " where ENV_NM = '"
                + environmentName + "'";
        CachedRowSet crsEnvironment = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryEnvironment, "reader");
        EnvironmentParameterConfiguration environmentParameterConfiguration = new EnvironmentParameterConfiguration();
        try {
            while (crsEnvironment.next()) {
                // Get parameters
                String queryEnvironmentParameters = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from "
                        + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters")
                        + " where ENV_NM = '" + environmentName + "'";
                CachedRowSet crsEnvironmentParameters = MetadataControl.getInstance().getConnectivityMetadataRepository()
                        .executeQuery(queryEnvironmentParameters, "reader");
                List<EnvironmentParameter> environmentParameters = new ArrayList<>();
                while (crsEnvironmentParameters.next()) {
                    environmentParameters.add(environmentParameterConfiguration.getEnvironmentParameter(environmentName,
                            crsEnvironmentParameters.getString("ENV_PAR_NM")));
                }
                environment = new Environment(environmentName, crsEnvironment.getString("ENV_DSC"), environmentParameters);
                crsEnvironmentParameters.close();
            }
            crsEnvironment.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return Optional.ofNullable(environment);
    }

    public boolean exists(Environment environment) {
        String queryEnvironment = "select * from "
                + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Environments")
                + " where ENV_NM = '"
                + environment.getName() + "'";
        CachedRowSet crsEnvironment = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryEnvironment, "reader");
        return crsEnvironment.size() == 1;
    }
    
    public List<Environment> getAllEnvironments() {
        List<Environment> environments = new ArrayList<>();
        String query = "select ENV_NM from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Environments")
                + " order by ENV_NM ASC";
        CachedRowSet crs = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(query, "reader");
        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration();
        try {
            while (crs.next()) {
                String environmentName = crs.getString("ENV_NM");
                environmentConfiguration.getEnvironment(environmentName).ifPresent(environments::add);
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return environments;
    }

    public void deleteEnvironment(Environment environment) throws EnvironmentDoesNotExistException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Deleting environment {0}", environment.getScriptName()), Level.TRACE);
        if (!exists(environment)) {
            throw new EnvironmentDoesNotExistException(
                    MessageFormat.format("Environment {0} is not present in the repository so cannot be updated",
                            environment.getName()));
        }
        List<String> query = getDeleteStatement(environment);
        MetadataControl.getInstance().getConnectivityMetadataRepository().executeBatch(query);
    }

    public List<String> getDeleteStatement(Environment environment) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Environments") +
                " WHERE ENV_NM = " +
                SQLTools.GetStringForSQL(environment.getName()) + ";");
        queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters") +
                " WHERE ENV_NM = "
                + SQLTools.GetStringForSQL(environment.getName()) + ";");
        return queries;
    }

    public void deleteAllEnvironments() {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log("Deleting all environments", Level.TRACE);
        List<String> query = getDeleteAllStatement();
        MetadataControl.getInstance().getConnectivityMetadataRepository().executeBatch(query);
    }

    private List<String> getDeleteAllStatement() {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Environments") + ";");
        queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters") + ";");
        return queries;
    }

    public void insertEnvironment(Environment environment) throws EnvironmentAlreadyExistsException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting environment {0}", environment.getScriptName()), Level.TRACE);
        if (exists(environment)) {
            throw new EnvironmentAlreadyExistsException(MessageFormat.format("Environment {0} already exists", environment.getName()));
        }
        List<String> query = getInsertStatement(environment);
        MetadataControl.getInstance().getConnectivityMetadataRepository().executeBatch(query);
    }

    public List<String> getInsertStatement(Environment environment) {
        EnvironmentParameterConfiguration environmentParameterConfiguration = new EnvironmentParameterConfiguration();
        List<String> queries = new ArrayList<>();
        queries.add("INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Environments") +
                " (ENV_NM, ENV_DSC) VALUES (" +
                SQLTools.GetStringForSQL(environment.getName()) + "," +
                SQLTools.GetStringForSQL(environment.getDescription())+ ");");


        for(EnvironmentParameter environmentParameter : environment.getParameters()) {
            queries.add(environmentParameterConfiguration.getInsertStatement(environment.getName(), environmentParameter));
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

    // Delete
    public String getDeleteStatement() {
        String sql = "";

        sql += "DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Environments");
        sql += " WHERE ENV_NM = "
                + SQLTools.GetStringForSQL(this.getEnvironment().getName());
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters");
        sql += " WHERE ENV_NM = "
                + SQLTools.GetStringForSQL(this.getEnvironment().getName());
        sql += ";";
        sql += "\n";

        return sql;

    }

    // Insert
    public String getInsertStatement() {
        String sql = "";

        if (this.exists()) {
            sql += this.getDeleteStatement();
        }

        sql += "INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Environments");
        sql += " (ENV_NM, ENV_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(this.getEnvironment().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getEnvironment().getDescription());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements(this.getEnvironment().getName());
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getParameterInsertStatements(Environment environment) {
        String result = "";

        // Catch null parameters
        if (environment.getParameters() == null)
            return result;

        for (EnvironmentParameter environmentParameter : environment.getParameters()) {
            EnvironmentParameterConfiguration environmentParameterConfiguration = new EnvironmentParameterConfiguration();
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += environmentParameterConfiguration.getInsertStatement(environment.getName(), environmentParameter);
        }

        return result;
    }


    private String getParameterInsertStatements(String environmentName) {
        String result = "";

        // Catch null parameters
        if (this.getEnvironment().getParameters() == null)
            return result;

        for (EnvironmentParameter environmentParameter : this.getEnvironment().getParameters()) {
            EnvironmentParameterConfiguration environmentParameterConfiguration = new EnvironmentParameterConfiguration(environmentParameter);
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += environmentParameterConfiguration.getInsertStatement(environmentName);
        }

        return result;
    }

    public ListObject getEnvironments() {
        List<Environment> environmentList = new ArrayList<>();
        CachedRowSet crs = null;
        String query = "select ENV_NM from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Environments")
                + " order by ENV_NM ASC";
        crs = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(query, "reader");
        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration();
        try {
            String environmentName = "";
            while (crs.next()) {
                environmentName = crs.getString("ENV_NM");
                environmentConfiguration.getEnvironment(environmentName).ifPresent(environmentList::add);
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        return new ListObject(FrameworkObjectConfiguration.getFrameworkObjectType(new Environment()), environmentList);
    }

    public void createEnvironment(String data) {
        DataObjectConfiguration dataObjectConfiguration = new DataObjectConfiguration();
        ObjectMapper objectMapper = new ObjectMapper();

        if (dataObjectConfiguration.isJSONArray(data)) {
            for (DataObject dataObject : dataObjectConfiguration.getDataArray(data)) {

                Environment environment = objectMapper.convertValue(dataObject.getData(), Environment.class);
                EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment);
                String output = environmentConfiguration.getInsertStatement();

                InputStream inputStream = FileTools
                        .convertToInputStream(output, FrameworkControl.getInstance());
                MetadataControl.getInstance().getConnectivityMetadataRepository().executeScript(inputStream);

            }
        } else {
            Environment environment = objectMapper.convertValue(dataObjectConfiguration.getDataObject(data).getData(), Environment.class);
            EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment);
            String output = environmentConfiguration.getInsertStatement();

            InputStream inputStream = FileTools.convertToInputStream(output,FrameworkControl.getInstance());
            MetadataControl.getInstance().getConnectivityMetadataRepository().executeScript(inputStream);
        }

    }

    public void deleteEnvironment(String environmentName) {
        this.getEnvironment(environmentName).ifPresent(environment -> {
                    EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment);
                    String output = environmentConfiguration.getDeleteStatement();

                    InputStream inputStream = FileTools
                            .convertToInputStream(output, FrameworkControl.getInstance());
                    MetadataControl.getInstance().getConnectivityMetadataRepository().executeScript(inputStream);
                }
        );

    }

    public void copyEnvironment(String fromEnvironmentName, String toEnvironmentName) {
        // TODO: check optional
        Environment environment = this.getEnvironment(fromEnvironmentName).get();

        // Set new environment name
        environment.setName(toEnvironmentName);

        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment);
        String output = environmentConfiguration.getInsertStatement();

        InputStream inputStream = FileTools.convertToInputStream(output,
                FrameworkControl.getInstance());
        MetadataControl.getInstance().getConnectivityMetadataRepository().executeScript(inputStream);
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}



