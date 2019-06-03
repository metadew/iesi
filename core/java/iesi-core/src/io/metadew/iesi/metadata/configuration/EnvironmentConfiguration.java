package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentDoesNotExistException;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.metadata.definition.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.ListObject;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnvironmentConfiguration extends MetadataConfiguration{

    private Environment environment;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public EnvironmentConfiguration(FrameworkInstance frameworkInstance) {
        this.setFrameworkInstance(frameworkInstance);
    }

    public EnvironmentConfiguration(Environment environment, FrameworkInstance frameworkInstance) {
        this.setEnvironment(environment);
        this.setFrameworkInstance(frameworkInstance);
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
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments") + " where ENV_NM = '"
                + environmentName + "'";
        CachedRowSet crsEnvironment = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeQuery(queryEnvironment, "reader");
        EnvironmentParameterConfiguration environmentParameterConfiguration = new EnvironmentParameterConfiguration(this.getFrameworkInstance());
        try {
            while (crsEnvironment.next()) {
                // Get parameters
                String queryEnvironmentParameters = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from "
                        + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters")
                        + " where ENV_NM = '" + environmentName + "'";
                CachedRowSet crsEnvironmentParameters = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
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
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments")
                + " where ENV_NM = '"
                + environment.getName() + "'";
        CachedRowSet crsEnvironment = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeQuery(queryEnvironment, "reader");
        return crsEnvironment.size() == 1;
    }
    
    public List<Environment> getAllEnvironments() {
        List<Environment> environments = new ArrayList<>();
        String query = "select ENV_NM from " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments")
                + " order by ENV_NM ASC";
        CachedRowSet crs = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeQuery(query, "reader");
        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(this.getFrameworkInstance());
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
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Deleting environment {0}", environment.getName()), Level.TRACE);
        if (!exists(environment)) {
            throw new EnvironmentDoesNotExistException(
                    MessageFormat.format("Environment {0} is not present in the repository so cannot be updated",
                            environment.getName()));
        }
        String query = getDeleteStatement(environment);
        this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeUpdate(query);
    }

    public String getDeleteStatement(Environment environment) {
        String sql = "";

        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments");
        sql += " WHERE ENV_NM = "
                + SQLTools.GetStringForSQL(environment.getName());
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters");
        sql += " WHERE ENV_NM = "
                + SQLTools.GetStringForSQL(environment.getName());
        sql += ";";
        sql += "\n";

        return sql;

    }

    public void deleteAllEnvironments() {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log("Deleting all environments", Level.TRACE);
        String query = getDeleteAllStatement();
        this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeUpdate(query);
    }

    private String getDeleteAllStatement() {
        String sql = "";

        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments");
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters");
        sql += ";";
        sql += "\n";

        return sql;
    }

    public void insertEnvironment(Environment environment) throws EnvironmentAlreadyExistsException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting environment {0}", environment.getName()), Level.TRACE);
        if (exists(environment)) {
            throw new EnvironmentAlreadyExistsException(MessageFormat.format("Environment {0} already exists", environment.getName()));
        }
        String query = getInsertStatement(environment);
        this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeUpdate(query);
    }

    public String getInsertStatement(Environment environment) {
        String sql = "";
        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments");
        sql += " (ENV_NM, ENV_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(environment.getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(environment.getDescription());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements(environment);
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    public void updateEnvironment(Environment environment) throws EnvironmentDoesNotExistException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Updating environment {0}.", environment.getName()), Level.TRACE);
        try {
            deleteEnvironment(environment);
            insertEnvironment(environment);
        } catch (EnvironmentDoesNotExistException e) {
            //TODO fix logging
        	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Environment {0} is not present in the repository so cannot be updated", environment.getName()),Level.TRACE);
            throw new EnvironmentDoesNotExistException(MessageFormat.format(
                    "Environment {0} is not present in the repository so cannot be updated", environment.getName()));

        } catch (EnvironmentAlreadyExistsException e) {
            //TODO fix logging
        	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Environment {0} is not deleted correctly during update. {1}", environment.getName(), e.toString()),Level.WARN);
        }
    }

    // Delete
    public String getDeleteStatement() {
        String sql = "";

        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments");
        sql += " WHERE ENV_NM = "
                + SQLTools.GetStringForSQL(this.getEnvironment().getName());
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters");
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

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments");
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
            EnvironmentParameterConfiguration environmentParameterConfiguration = new EnvironmentParameterConfiguration(this.getFrameworkInstance());
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
            EnvironmentParameterConfiguration environmentParameterConfiguration = new EnvironmentParameterConfiguration(environmentParameter,
                    this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += environmentParameterConfiguration.getInsertStatement(environmentName);
        }

        return result;
    }

    public ListObject getEnvironments() {
        List<Environment> environmentList = new ArrayList<>();
        CachedRowSet crs = null;
        String query = "select ENV_NM from " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments")
                + " order by ENV_NM ASC";
        crs = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeQuery(query, "reader");
        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(this.getFrameworkInstance());
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
                EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment, this.getFrameworkInstance());
                String output = environmentConfiguration.getInsertStatement();

                InputStream inputStream = FileTools
                        .convertToInputStream(output, this.getFrameworkInstance().getFrameworkControl());
                this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);

            }
        } else {
            Environment environment = objectMapper.convertValue(dataObjectConfiguration.getDataObject(data).getData(), Environment.class);
            EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment, this.getFrameworkInstance());
            String output = environmentConfiguration.getInsertStatement();

            InputStream inputStream = FileTools.convertToInputStream(output,
                    this.getFrameworkInstance().getFrameworkControl());
            this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);
        }

    }

    public void deleteEnvironment(String environmentName) {
        this.getEnvironment(environmentName).ifPresent(environment -> {
                    EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment, this.getFrameworkInstance());
                    String output = environmentConfiguration.getDeleteStatement();

                    InputStream inputStream = FileTools
                            .convertToInputStream(output, this.getFrameworkInstance().getFrameworkControl());
                    this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);
                }
        );

    }

    public void copyEnvironment(String fromEnvironmentName, String toEnvironmentName) {
        // TODO: check optional
        Environment environment = this.getEnvironment(fromEnvironmentName).get();

        // Set new environment name
        environment.setName(toEnvironmentName);

        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment, this.getFrameworkInstance());
        String output = environmentConfiguration.getInsertStatement();

        InputStream inputStream = FileTools.convertToInputStream(output,
                this.getFrameworkInstance().getFrameworkControl());
        this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}