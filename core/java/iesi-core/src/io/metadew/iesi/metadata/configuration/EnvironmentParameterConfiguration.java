package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.EnvironmentParameter;

public class EnvironmentParameterConfiguration
{

	private EnvironmentParameter environmentParameter;

	private FrameworkExecution frameworkExecution;

	// Constructors
	public EnvironmentParameterConfiguration(EnvironmentParameter environmentParameter, FrameworkExecution frameworkExecution)
	{
		this.setEnvironmentParameter(environmentParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public EnvironmentParameterConfiguration(FrameworkExecution frameworkExecution)
	{
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String environmentName)
	{
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
					.getTableNameByLabel("EnvironmentParameters");
		sql += " (ENV_NM, ENV_PAR_NM, ENV_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(environmentName);
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getEnvironmentParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getEnvironmentParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public EnvironmentParameter getEnvironmentParameter(String environmentName, String environmentParameterName)
	{
		EnvironmentParameter environmentParameter = new EnvironmentParameter();
		CachedRowSet crsEnvironmentParameter = null;
		String queryEnvironmentParameter = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from "
					+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
								.getTableNameByLabel("EnvironmentParameters")
					+ " where ENV_NM = '" + environmentName + "' and ENV_PAR_NM = '" + environmentParameterName + "'";
		crsEnvironmentParameter = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
					.executeQuery(queryEnvironmentParameter, "reader");
		try
		{
			while (crsEnvironmentParameter.next())
			{
				environmentParameter.setName(environmentParameterName);
				environmentParameter.setValue(crsEnvironmentParameter.getString("ENV_PAR_VAL"));
			}
			crsEnvironmentParameter.close();
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return environmentParameter;
	}

	public String getEnvironmentParameterValue(String environmentName, String environmentParameterName)
	{
		String output = "";
		CachedRowSet crsEnvironmentParameter = null;
		String queryEnvironmentParameter = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from "
					+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
								.getTableNameByLabel("EnvironmentParameters")
					+ " where ENV_NM = '" + environmentName + "' and ENV_PAR_NM = '" + environmentParameterName + "'";
		crsEnvironmentParameter = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
					.executeQuery(queryEnvironmentParameter, "reader");
		try
		{
			while (crsEnvironmentParameter.next())
			{
				output = crsEnvironmentParameter.getString("ENV_PAR_VAL");
			}
			crsEnvironmentParameter.close();
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		return output;
	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution()
	{
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution)
	{
		this.frameworkExecution = frameworkExecution;
	}

	public EnvironmentParameter getEnvironmentParameter()
	{
		return environmentParameter;
	}

	public void setEnvironmentParameter(EnvironmentParameter environmentParameter)
	{
		this.environmentParameter = environmentParameter;
	}

}