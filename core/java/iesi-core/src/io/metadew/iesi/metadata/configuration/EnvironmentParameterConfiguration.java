package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.EnvironmentParameter;

public class EnvironmentParameterConfiguration {

    private EnvironmentParameter environmentParameter;

    private FrameworkExecution frameworkExecution;

    // Constructors
    public EnvironmentParameterConfiguration(EnvironmentParameter environmentParameter, FrameworkExecution frameworkExecution) {
        this.setEnvironmentParameter(environmentParameter);
        this.setFrameworkExecution(frameworkExecution);
    }

    public EnvironmentParameterConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    public String getInsertStatement(String environmentName, EnvironmentParameter environmentParameter) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
                .getMetadataTableConfiguration().getTableName("EnvironmentParameters");
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

        sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
                .getMetadataTableConfiguration().getTableName("EnvironmentParameters");
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
                + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
                .getMetadataTableConfiguration().getTableName("EnvironmentParameters")
                + " where ENV_NM = '" + environmentName + "' and ENV_PAR_NM = '" + environmentParameterName + "'";
        crsEnvironmentParameter = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
                .executeQuery(queryEnvironmentParameter);
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
        CachedRowSet crsEnvironmentParameter = null;
        String queryEnvironmentParameter = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from "
                + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
                .getMetadataTableConfiguration().getTableName("EnvironmentParameters")
                + " where ENV_NM = '" + environmentName + "' and ENV_PAR_NM = '" + environmentParameterName + "'";
        crsEnvironmentParameter = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
                .executeQuery(queryEnvironmentParameter);
        try {
            while (crsEnvironmentParameter.next()) {
                output = crsEnvironmentParameter.getString("ENV_PAR_VAL");
            }
            crsEnvironmentParameter.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
        return Optional.ofNullable(output);
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public EnvironmentParameter getEnvironmentParameter() {
        return environmentParameter;
    }

    public void setEnvironmentParameter(EnvironmentParameter environmentParameter) {
        this.environmentParameter = environmentParameter;
    }

}