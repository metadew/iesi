package io.metadew.iesi.metadata.configuration;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.metadata.definition.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.ListObject;

public class EnvironmentConfiguration {

	private Environment environment;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public EnvironmentConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	public EnvironmentConfiguration(Environment environment, FrameworkExecution frameworkExecution) {
		this.setEnvironment(environment);
		this.setFrameworkExecution(frameworkExecution);
	}

	public static String getDeleteStatement(String environmentName, String environmentTable, String environmentParametersTable) {
		String sql = "";

		sql += "DELETE FROM " + environmentTable;
		sql += " WHERE ENV_NM = "
				+ SQLTools.GetStringForSQL(environmentName);
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + environmentParametersTable;
		sql += " WHERE ENV_NM = "
				+ SQLTools.GetStringForSQL(environmentName);
		sql += ";";
		sql += "\n";
		return sql;
	}

	// Delete
	public String getDeleteStatement() {
		String sql = "";

		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments");
		sql += " WHERE ENV_NM = "
				+ SQLTools.GetStringForSQL(this.getEnvironment().getName());
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters");
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

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments");
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
		if (!sqlParameters.equals("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}

	private String getParameterInsertStatements(String environmentName) {
		String result = "";

		// Catch null parameters
		if (this.getEnvironment().getParameters() == null)
			return result;

		for (EnvironmentParameter environmentParameter : this.getEnvironment().getParameters()) {
			EnvironmentParameterConfiguration environmentParameterConfiguration = new EnvironmentParameterConfiguration(environmentParameter,
					this.getFrameworkExecution());
			if (!result.equals(""))
				result += "\n";
			result += environmentParameterConfiguration.getInsertStatement(environmentName);
		}

		return result;
	}

	// GEt Environment
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Environment getEnvironment(String environmentName) {
		Environment environment = new Environment();
		CachedRowSet crsEnvironment = null;
		String queryEnvironment = "select ENV_NM, ENV_DSC from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments") + " where ENV_NM = '"
				+ environmentName + "'";
		crsEnvironment = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeQuery(queryEnvironment, "reader");
		EnvironmentParameterConfiguration environmentParameterConfiguration = new EnvironmentParameterConfiguration(this.getFrameworkExecution());
		try {
			while (crsEnvironment.next()) {
				environment.setName(environmentName);
				environment.setDescription(crsEnvironment.getString("ENV_DSC"));

				// Get parameters
				CachedRowSet crsEnvironmentParameters = null;
				String queryEnvironmentParameters = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters")
						+ " where ENV_NM = '" + environmentName + "'";
				crsEnvironmentParameters = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
						.executeQuery(queryEnvironmentParameters, "reader");
				List<EnvironmentParameter> environmentParameterList = new ArrayList();
				while (crsEnvironmentParameters.next()) {
					environmentParameterList.add(environmentParameterConfiguration.getEnvironmentParameter(environmentName,
							crsEnvironmentParameters.getString("ENV_PAR_NM")));
				}
				environment.setParameters(environmentParameterList);
				crsEnvironmentParameters.close();
			}
			crsEnvironment.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return environment;
	}

	public ListObject getEnvironments() {
		List<Environment> environmentList = new ArrayList<>();
		CachedRowSet crs = null;
		String query = "select ENV_NM from " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Environments")
				+ " order by ENV_NM ASC";
		crs = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeQuery(query, "reader");
		EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(this.getFrameworkExecution());
		try {
			String environmentName = "";
			while (crs.next()) {
				environmentName = crs.getString("ENV_NM");
				environmentList.add(environmentConfiguration.getEnvironment(environmentName));
			}
			crs.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		return new ListObject(FrameworkObjectConfiguration.getFrameworkObjectType(new Environment()), environmentList);
	}

	public void createEnvironment(String data) {
		DataObjectConfiguration dataObjectConfiguration = new DataObjectConfiguration(this.getFrameworkExecution());
		ObjectMapper objectMapper = new ObjectMapper();
		
		if (dataObjectConfiguration.isJSONArray(data)) {
			for (DataObject dataObject : dataObjectConfiguration.getDataArray(data)) {

				Environment environment = objectMapper.convertValue(dataObject.getData(), Environment.class);
				EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment, this.getFrameworkExecution());
				String output = environmentConfiguration.getInsertStatement();

				InputStream inputStream = FileTools
						.convertToInputStream(output, this.getFrameworkExecution().getFrameworkControl());
				this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);

			}
		} else {
			Environment environment = objectMapper.convertValue(dataObjectConfiguration.getDataObject(data).getData(), Environment.class);
			EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment, this.getFrameworkExecution());
			String output = environmentConfiguration.getInsertStatement();

			InputStream inputStream = FileTools.convertToInputStream(output,
					this.getFrameworkExecution().getFrameworkControl());
			this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);
		}

	}
	
	public void deleteEnvironment(String environmentName) {
		Environment environment = this.getEnvironment(environmentName);
		EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment, this.getFrameworkExecution());
		String output = environmentConfiguration.getDeleteStatement();

		InputStream inputStream = FileTools
				.convertToInputStream(output, this.getFrameworkExecution().getFrameworkControl());
		this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);
	}
	
	public void copyEnvironment(String fromEnvironmentName, String toEnvironmentName) {
		Environment environment = this.getEnvironment(fromEnvironmentName);
		
		// Set new environment name
		environment.setName(toEnvironmentName);
		
		EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment, this.getFrameworkExecution());
		String output = environmentConfiguration.getInsertStatement();

		InputStream inputStream = FileTools.convertToInputStream(output,
				this.getFrameworkExecution().getFrameworkControl());
		this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);
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

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}