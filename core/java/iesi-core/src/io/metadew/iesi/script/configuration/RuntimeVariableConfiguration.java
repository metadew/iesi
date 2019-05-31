package io.metadew.iesi.script.configuration;

import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.RuntimeVariable;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

public class RuntimeVariableConfiguration {

    private RuntimeVariable runtimeVariable;
    private FrameworkExecution frameworkExecution;
    private String runCacheFolderName;
    private String runCacheFileName = "runtimeVariables.db3";
    private String runCacheFilePath;
    private SqliteDatabaseConnection sqliteDatabaseConnection;
    private String PRC_RUN_VAR = "PRC_RUN_VAR";

    // Constructors
    public RuntimeVariableConfiguration(FrameworkExecution frameworkExecution, String runCacheFolderName) {
        this.setFrameworkExecution(frameworkExecution);

        // Define path
        this.setRunCacheFolderName(runCacheFolderName);
        this.setRunCacheFilePath(this.getRunCacheFolderName() + File.separator + this.getRunCacheFileName());

        // Create database
        this.setSqliteDatabaseConnection(new SqliteDatabaseConnection(this.getRunCacheFilePath()));
        this.createRunVarTable();

    }

    private void createRunVarTable() {
        String query = "CREATE TABLE " + this.getPRC_RUN_VAR() + " (" +
                "RUN_ID TEXT NOT NULL," +
                "PRC_ID NUMERIC NOT NULL," +
                "VAR_NM TEXT NOT NULL," +
                "VAR_VAL TEXT" +
                ")";
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    // Methods
    public void cleanRuntimeVariables(String runId) {
        String query = "delete from " + this.getPRC_RUN_VAR()
                + " where RUN_ID = '" + runId + "'";
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    public void cleanRuntimeVariables(String runId, long processId) {
        String query = "delete from " + this.getPRC_RUN_VAR()
                + " where RUN_ID = '" + runId + "' and PRC_ID = " + processId;
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    public void setRuntimeVariable(String runId, Long processId, String name, String value) {
        // Verify if variable already exists
        String query = "";

        query = "select run_id, prc_id, var_nm, var_val from "
                + this.getPRC_RUN_VAR() + " where run_id = '"
                + runId + "' and prc_id = " + processId + " and var_nm = '" + name + "'";
        CachedRowSet crs = null;
        crs = this.getSqliteDatabaseConnection().executeQuery(query);

        // if so, the previous values will be deleted
        if (SQLTools.getRowCount(crs) > 0) {
            query = "delete from " + this.getPRC_RUN_VAR()
                    + " where run_id = '" + runId + "' and prc_id = " + processId + " and var_nm = '" + name + "'";
            this.getSqliteDatabaseConnection().executeUpdate(query);
        }

        // DtNow, new values can be stored
        query = "";
        query = "INSERT INTO " + this.getPRC_RUN_VAR();
        query = query + "(run_id, prc_id, var_nm, var_val)";
        query = query + " VALUES (";
        query += SQLTools.GetStringForSQL(runId);
        query += ",";
        query += SQLTools.GetStringForSQL(processId);
        query += ",";
        query += SQLTools.GetStringForSQL(name);
        query += ",";
        query += SQLTools.GetStringForSQL(value);
        query += ")";
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    public String getRuntimeVariableValue(String runId, String name) {
        CachedRowSet crs = null;
        String query = "select VAR_VAL from " + this.getPRC_RUN_VAR()
                + " where run_id = '" + runId + "' and var_nm = '" + name + "'";
        crs = this.getSqliteDatabaseConnection().executeQuery(query);
        String value = "";
        try {
            while (crs.next()) {
                value = crs.getString("VAR_VAL");
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return value;
    }

    public RuntimeVariable getRuntimeVariable(String runId, String name) {
        RuntimeVariable runtimeVariable = new RuntimeVariable();
        runtimeVariable.setName(name);

        CachedRowSet crs = null;
        String query = "select VAR_VAL from " + this.getPRC_RUN_VAR()
                + " where run_id = '" + runId + "' and var_nm = '" + name + "'";
        crs = this.getSqliteDatabaseConnection().executeQuery(query);
        String value = "";
        try {
            while (crs.next()) {
                value = crs.getString("VAR_VAL");
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        runtimeVariable.setValue(value);
        return runtimeVariable;
    }

    // Getters and Setters
    public RuntimeVariable getRuntimeVariable() {
        return runtimeVariable;
    }

    public void setRuntimeVariable(RuntimeVariable runtimeVariable) {
        this.runtimeVariable = runtimeVariable;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public String getRunCacheFileName() {
        return runCacheFileName;
    }

    public void setRunCacheFileName(String runCacheFileName) {
        this.runCacheFileName = runCacheFileName;
    }

    public String getRunCacheFolderName() {
        return runCacheFolderName;
    }

    public void setRunCacheFolderName(String runCacheFolderName) {
        this.runCacheFolderName = runCacheFolderName;
    }

    public String getRunCacheFilePath() {
        return runCacheFilePath;
    }

    public void setRunCacheFilePath(String runCacheFilePath) {
        this.runCacheFilePath = runCacheFilePath;
    }

    public SqliteDatabaseConnection getSqliteDatabaseConnection() {
        return sqliteDatabaseConnection;
    }

    public void setSqliteDatabaseConnection(SqliteDatabaseConnection sqliteDatabaseConnection) {
        this.sqliteDatabaseConnection = sqliteDatabaseConnection;
    }

    public String getPRC_RUN_VAR() {
        return PRC_RUN_VAR;
    }

    public void setPRC_RUN_VAR(String pRC_RUN_VAR) {
        PRC_RUN_VAR = pRC_RUN_VAR;
    }

}