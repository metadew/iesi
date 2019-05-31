package io.metadew.iesi.script.configuration;

import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.RuntimeVariable;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.operation.ConditionOperation;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class IterationConfiguration {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private String runCacheFolderName;
    private String runCacheFileName = "iterationExecutions.db3";
    private String runCacheFilePath;
    private SqliteDatabaseConnection sqliteDatabaseConnection;
    private String PRC_ITERATION_EXEC = "PRC_ITERATION_EXEC";

    // Constructors
    public IterationConfiguration(FrameworkExecution frameworkExecution, String runCacheFolderName,
                                  ExecutionControl executionControl) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);

        // Define path
        this.setRunCacheFolderName(runCacheFolderName);
        this.setRunCacheFilePath(this.getRunCacheFolderName() + File.separator + this.getRunCacheFileName());

        // Create database
        this.setSqliteDatabaseConnection(new SqliteDatabaseConnection(this.getRunCacheFilePath()));
        this.createIterationExecTable();

    }

    private void createIterationExecTable() {
        String query = "CREATE TABLE " + this.getPRC_ITERATION_EXEC() + " (" + "RUN_ID TEXT NOT NULL,"
                + "PRC_ID NUMERIC NOT NULL," + "LIST_ID NUMERIC NOT NULL," + "LIST_NM TEXT NOT NULL,"
                + "SET_ID NUMERIC NOT NULL," + "SET_NM TEXT NOT NULL," + "ORDER_NB NUMERIC NOT NULL,"
                + "VAR_NM TEXT NOT NULL," + "VAR_VAL TEXT" + ")";
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    // Methods
    public void cleanIterationVariables(String runId) {
        String query = "delete from " + this.getPRC_ITERATION_EXEC() + " where RUN_ID = '" + runId + "'";
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    public void cleanIterationVariables(String runId, long processId) {
        String query = "delete from " + this.getPRC_ITERATION_EXEC() + " where RUN_ID = '" + runId + "' and PRC_ID = "
                + processId;
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    public void cleanIterationVariables(String runId, String iterationList) {
        String query = "delete from " + this.getPRC_ITERATION_EXEC() + " where RUN_ID = '" + runId + "' and LIST_NM = '"
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

    public void setIterationValues(String runId, String iterationList, String values) {
        this.cleanIterationVariables(runId, iterationList);

        // Iterate over the iteration sets
        String setName = "";
        int setNumber = 0;

        String[] parts = values.split(",");
        for (int i = 0; i < parts.length; i++) {
            setNumber++;
            setName = "auto generated iteration set " + setNumber;
            String innerpart = parts[i];
            this.setIterationVariable(runId, -1, iterationList, -1, setName, setNumber, "key", innerpart);
        }
    }

    public void setIterationFor(String runId, String iterationList, String from, String to, String step) {
        this.cleanIterationVariables(runId, iterationList);

        // Iterate over the iteration sets
        String setName = "";
        int setNumber = 0;

        // Parse for values
        int iFrom = (int) Double.parseDouble(from);
        int iTo = (int) Double.parseDouble(to);
        int iStep = (int) Double.parseDouble(step);
        if (iFrom < iTo) {
            while (iFrom <= iTo) {
                setNumber++;
                setName = "auto generated iteration set " + setNumber;
                this.setIterationVariable(runId, -1, iterationList, -1, setName, setNumber, "key",
                        String.valueOf(iFrom));

                iFrom = iFrom + iStep;
            }
        } else if (iFrom > iTo) {
            while (iFrom >= iTo) {
                setNumber++;
                setName = "auto generated iteration set " + setNumber;
                this.setIterationVariable(runId, -1, iterationList, -1, setName, setNumber, "key",
                        String.valueOf(iFrom));

                iFrom = iFrom - iStep;
            }
        } else {
            setNumber++;
            setName = "auto generated iteration set " + setNumber;
            this.setIterationVariable(runId, -1, iterationList, -1, setName, setNumber, "key",
                    String.valueOf(iFrom));
        }
    }

    // Type: list
    public void setIterationList(String runId, String iterationList, String inputList) {
        this.cleanIterationVariables(runId, iterationList);

        // Get iteration variable configuration
        IterationVariableConfiguration iterationVariableConfiguration = new IterationVariableConfiguration(
                this.getFrameworkExecution(), this.getRunCacheFolderName(), false);
        CachedRowSet crs = iterationVariableConfiguration.getIterationList(runId, inputList);

        // Iterate over the iteration sets
        String setName = "";
        int setNumber = 0;

        try {
            while (crs.next()) {
                setNumber++;
                setName = "auto generated iteration set " + setNumber;
                this.setIterationVariable(runId, -1, iterationList, -1, setName, crs.getInt("ORDER_NB"),
                        crs.getString("VAR_NM"), crs.getString("VAR_VAL"));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
    }

    public void setIterationVariable(String runId, int listId, String listName, int setId, String setName, int order,
                                     String name, String value) {
        String query = "";
        query = "INSERT INTO " + this.getPRC_ITERATION_EXEC();
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
        String query = "select VAR_VAL from " + this.getPRC_ITERATION_EXEC() + " where run_id = '" + runId
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

    public IterationInstance hasNext(String runId, long orderNumber) {
        CachedRowSet crs = null;
        String query = "select run_id, prc_id, list_id, list_nm, set_id, set_nm, order_nb, var_nm, var_val from "
                + this.getPRC_ITERATION_EXEC() + " where run_id = '" + runId + "' and order_nb = " + orderNumber;
        crs = this.getSqliteDatabaseConnection().executeQuery(query);
        IterationInstance iterationInstance = new IterationInstance();
        try {
            while (crs.next()) {
                iterationInstance.setEmpty(false);
                iterationInstance.getVariableMap().put(crs.getString("VAR_NM"), crs.getString("VAR_VAL"));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return iterationInstance;
    }

    public IterationInstance hasNext(String runId, String condition) {
        IterationInstance iterationInstance = new IterationInstance();

        boolean conditionResult = true;
        ConditionOperation conditionOperation = new ConditionOperation(this.getExecutionControl(), condition);
        try {
            conditionResult = conditionOperation.evaluateCondition();
        } catch (Exception exception) {
            conditionResult = true;
        }

        if (conditionResult) {
            iterationInstance.setEmpty(false);
            iterationInstance.getVariableMap().put("iterate", "y");
        }

        return iterationInstance;
    }

    public IterationInstance hasNextListItem(String runId, String listName, long orderNumber) {
        CachedRowSet crs = null;
        String query = "select run_id, prc_id, list_id, list_nm, set_id, set_nm, order_nb, var_nm, var_val from "
                + this.getPRC_ITERATION_EXEC() + " where run_id = '" + runId + "' and list_nm = '" + listName
                + "' and order_nb = " + orderNumber;
        crs = this.getSqliteDatabaseConnection().executeQuery(query);
        IterationInstance iterationInstance = new IterationInstance();
        int items = 0;
        try {
            while (crs.next()) {
                items++;

                if (items == 1)
                    iterationInstance.setEmpty(false);

                iterationInstance.getVariableMap().put(crs.getString("VAR_NM"), crs.getString("VAR_VAL"));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return iterationInstance;
    }

    public RuntimeVariable getRuntimeVariable(String runId, String name) {
        RuntimeVariable runtimeVariable = new RuntimeVariable();
        runtimeVariable.setName(name);

        CachedRowSet crs = null;
        String query = "select VAR_VAL from " + this.getPRC_ITERATION_EXEC() + " where run_id = '" + runId
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

    public String getPRC_ITERATION_EXEC() {
        return PRC_ITERATION_EXEC;
    }

    public void setPRC_ITERATION_EXEC(String pRC_ITERATION_EXEC) {
        PRC_ITERATION_EXEC = pRC_ITERATION_EXEC;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

}