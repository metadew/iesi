package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.EnvironmentParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class EnvironmentParameterConfiguration {

    private EnvironmentParameter environmentParameter;

    private FrameworkInstance frameworkInstance;

    // Constructors
    public EnvironmentParameterConfiguration(EnvironmentParameter environmentParameter, FrameworkInstance frameworkInstance) {
        this.setEnvironmentParameter(environmentParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public EnvironmentParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public String getInsertStatement(String environmentName, EnvironmentParameter environmentParameter) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("EnvironmentParameters");
        sql += " (ENV_NM, ENV_PAR_NM, ENV_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(environmentName);
        sql += ",";
        sql += SQLTools.GetStringForSQL(environmentParameter.getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(environmentParameter.getValue());
        sql += ")";
        sql += ";";

        return sql;
    }


    // Insert
    public String getInsertStatement(String environmentName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
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

    public EnvironmentParameter getEnvironmentParameter(String environmentName, String environmentParameterName) {
        EnvironmentParameter environmentParameter = new EnvironmentParameter();
        CachedRowSet crsEnvironmentParameter = null;
        String queryEnvironmentParameter = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("EnvironmentParameters")
                + " where ENV_NM = '" + environmentName + "' and ENV_PAR_NM = '" + environmentParameterName + "'";
        crsEnvironmentParameter = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .executeQuery(queryEnvironmentParameter, "reader");
        try {
            while (crsEnvironmentParameter.next()) {
                environmentParameter.setName(environmentParameterName);
                environmentParameter.setValue(crsEnvironmentParameter.getString("ENV_PAR_VAL"));
            }
            crsEnvironmentParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return environmentParameter;
    }

    public Optional<String> getEnvironmentParameterValue(String environmentName, String environmentParameterName) {
        String output = null;
        CachedRowSet crsEnvironmentParameter;
        String queryEnvironmentParameter = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .getTableNameByLabel("EnvironmentParameters")
                + " where ENV_NM = '" + environmentName + "' and ENV_PAR_NM = '" + environmentParameterName + "'";
        crsEnvironmentParameter = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                .executeQuery(queryEnvironmentParameter, "reader");
        try {
            while (crsEnvironmentParameter.next()) {
                output = crsEnvironmentParameter.getString("ENV_PAR_VAL");
            }
            crsEnvironmentParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }

        return Optional.ofNullable(output);
    }

    // Getters and Setters
    public EnvironmentParameter getEnvironmentParameter() {
        return environmentParameter;
    }

    public void setEnvironmentParameter(EnvironmentParameter environmentParameter) {
        this.environmentParameter = environmentParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}