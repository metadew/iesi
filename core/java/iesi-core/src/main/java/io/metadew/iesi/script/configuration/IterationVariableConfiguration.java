package io.metadew.iesi.script.configuration;

import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.RuntimeVariable;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class IterationVariableConfiguration {

	private FrameworkExecution frameworkExecution;
	private String runCacheFolderName;
    private String runCacheFileName = "iterationVariables.db3";
    private String runCacheFilePath;
    private SqliteDatabaseConnection sqliteDatabaseConnection;
    private String PRC_ITERATION_VAR = "PRC_ITERATION_VAR";

    // Constructors
    public IterationVariableConfiguration(FrameworkExecution frameworkExecution, String runCacheFolderName,
                                          boolean initialize) {
    	this.setFrameworkExecution(frameworkExecution);
    	
        // Define path
        this.setRunCacheFolderName(runCacheFolderName);
        this.setRunCacheFilePath(this.getRunCacheFolderName() + File.separator + this.getRunCacheFileName());

        // Create database
        this.setSqliteDatabaseConnection(new SqliteDatabaseConnection(this.getRunCacheFilePath()));

        // Initialize
        if (initialize) {
            this.createIterationVarTable();
        }

    }

    private void createIterationVarTable() {
        String query = "CREATE TABLE " + this.getPRC_ITERATION_VAR() + " (" + "RUN_ID TEXT NOT NULL,"
                + "PRC_ID NUMERIC NOT NULL," + "LIST_ID NUMERIC NOT NULL," + "LIST_NM TEXT NOT NULL,"
                + "SET_ID NUMERIC NOT NULL," + "SET_NM TEXT NOT NULL," + "ORDER_NB NUMERIC NOT NULL,"
                + "VAR_NM TEXT NOT NULL," + "VAR_VAL TEXT" + ")";
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    // Methods
    public void cleanIterationVariables(String runId) {
        String query = "delete from " + this.getPRC_ITERATION_VAR() + " where RUN_ID = '" + runId + "'";
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    public void cleanIterationVariables(String runId, long processId) {
        String query = "delete from " + this.getPRC_ITERATION_VAR() + " where RUN_ID = '" + runId + "' and PRC_ID = "
                + processId;
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    public void cleanIterationVariables(String runId, String iterationList) {
        String query = "delete from " + this.getPRC_ITERATION_VAR() + " where RUN_ID = '" + runId + "' and LIST_NM = '"
                + iterationList + "'";
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    public void setIterationList(String runId, String iterationList, ResultSet resultSet) {
        try {
            this.cleanIterationVariables(runId, iterationList);

            // Iterate over the iteration sets
            String setName = "";
            int setNumber = 0;

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columns = resultSetMetaData.getColumnCount();

            resultSet.beforeFirst();
            while (resultSet.next()) {
                setNumber++;
                setName = "auto generated iteration set " + setNumber;

                // Iterate over the iteration variables
                for (int i = 1; i < columns + 1; i++) {
                    this.setIterationVariable(runId, -1, iterationList, -1, setName, setNumber,
                            resultSetMetaData.getColumnName(i), resultSet.getString(i));
                }
            }
            resultSet.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

    }

    public void setIterationVariable(String runId, int listId, String listName, int setId, String setName, int order,
                                     String name, String value) {
        String query = "";
        query = "INSERT INTO " + this.getPRC_ITERATION_VAR();
        query = query + "(run_id, prc_id, list_id, list_nm, set_id, set_nm, order_nb, var_nm, var_val)";
        query = query + " VALUES (";
        query += SQLTools.GetStringForSQL(runId);
        query += ",";
        query += SQLTools.GetStringForSQL(-1);
        query += ",";
        query += SQLTools.GetStringForSQL(listId);
        query += ",";
        query += SQLTools.GetStringForSQL(listName);
        query += ",";
        query += SQLTools.GetStringForSQL(setId);
        query += ",";
        query += SQLTools.GetStringForSQL(setName);
        query += ",";
        query += SQLTools.GetStringForSQL(order);
        query += ",";
        query += SQLTools.GetStringForSQL(name);
        query += ",";
        query += SQLTools.GetStringForSQL(value);
        query += ")";
        this.getSqliteDatabaseConnection().executeUpdate(query);

    }

    public String getRuntimeVariableValue(String runId, String name) {
        CachedRowSet crs = null;
        String query = "select VAR_VAL from " + this.getPRC_ITERATION_VAR() + " where run_id = '" + runId
                + "' and var_nm = '" + name + "'";
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

    public CachedRowSet getIterationList(String runId, String name) {
        CachedRowSet crs = null;
        String query = "select run_id, prc_id, list_id, list_nm, set_id, set_nm, order_nb, var_nm, var_val from "
                + this.getPRC_ITERATION_VAR() + " where run_id = '" + runId + "' and list_nm = '" + name
                + "' order by order_nb asc, var_nm asc";
        crs = this.getSqliteDatabaseConnection().executeQuery(query);
        return crs;
    }

    public RuntimeVariable getRuntimeVariable(String runId, String name) {
        RuntimeVariable runtimeVariable = new RuntimeVariable();
        runtimeVariable.setName(name);

        CachedRowSet crs = null;
        String query = "select VAR_VAL from " + this.getPRC_ITERATION_VAR() + " where run_id = '" + runId
                + "' and var_nm = '" + name + "'";
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

    public String getPRC_ITERATION_VAR() {
        return PRC_ITERATION_VAR;
    }

    public void setPRC_ITERATION_VAR(String pRC_ITERATION_VAR) {
        PRC_ITERATION_VAR = pRC_ITERATION_VAR;
    }

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}