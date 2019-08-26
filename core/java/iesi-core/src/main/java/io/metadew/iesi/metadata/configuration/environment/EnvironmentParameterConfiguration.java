package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class EnvironmentParameterConfiguration {

    private EnvironmentParameter environmentParameter;

    // Constructors
    public EnvironmentParameterConfiguration(EnvironmentParameter environmentParameter) {
        this.setEnvironmentParameter(environmentParameter);
    }

    public EnvironmentParameterConfiguration() {
    }

    public String getInsertStatement(String environmentName, EnvironmentParameter environmentParameter) {
        return "INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("EnvironmentParameters") + " (ENV_NM, ENV_PAR_NM, ENV_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(environmentName) + "," +
                SQLTools.GetStringForSQL(environmentParameter.getName()) + "," +
                SQLTools.GetStringForSQL(environmentParameter.getValue()) + ");";
    }


    // Insert
    public String getInsertStatement(String environmentName) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository()
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
                + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("EnvironmentParameters")
                + " where ENV_NM = '" + environmentName + "' and ENV_PAR_NM = '" + environmentParameterName + "'";
        crsEnvironmentParameter = MetadataControl.getInstance().getConnectivityMetadataRepository()
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
                + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("EnvironmentParameters")
                + " where ENV_NM = '" + environmentName + "' and ENV_PAR_NM = '" + environmentParameterName + "'";
        crsEnvironmentParameter = MetadataControl.getInstance().getConnectivityMetadataRepository()
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

}