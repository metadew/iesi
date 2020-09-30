package io.metadew.iesi.script.configuration;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.h2.H2Database;
import io.metadew.iesi.connection.database.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.service.ConditionService;

import javax.script.ScriptException;
import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

public class IterationConfiguration {

    private final H2Database database;
    private final String runCacheFolderName;
    private final ExecutionControl executionControl;
    private final static String runCacheFileName = "iterationExecutions.db3";
    private final static String PRC_ITERATION_EXEC = "PRC_ITERATION_EXEC";
    private final static int RUNTIME_VAR_VALUE_MAX_LENGTH = 4000;

    // Constructors
    public IterationConfiguration(String runCacheFolderName, ExecutionControl executionControl)  {
        this.executionControl = executionControl;
        this.runCacheFolderName = runCacheFolderName;
        // Create database
        this.database = new H2Database(new H2MemoryDatabaseConnection(runCacheFolderName + File.separator + runCacheFileName, "sa", "", null));
        createIterationExecTable();

    }

    private void createIterationExecTable()  {
        String query = "CREATE TABLE " + PRC_ITERATION_EXEC + " (" + "RUN_ID VARCHAR(200) NOT NULL,"
                + "PRC_ID INT NOT NULL," + "LIST_ID INT NOT NULL," + "LIST_NM VARCHAR(200) NOT NULL,"
                + "SET_ID INT NOT NULL," + "SET_NM VARCHAR(200) NOT NULL," + "ORDER_NB INT NOT NULL,"
                + "VAR_NM VARCHAR(200) NOT NULL," + "VAR_VAL VARCHAR("+RUNTIME_VAR_VALUE_MAX_LENGTH+")" + ")";
        DatabaseHandler.getInstance().executeUpdate(database, query);
    }

    // Methods
    public void cleanIterationVariables(String runId)  {
        String query = "delete from " + PRC_ITERATION_EXEC + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + "";
        DatabaseHandler.getInstance().executeUpdate(database, query);
    }

    public void cleanIterationVariables(String runId, long processId)  {
        String query = "delete from " + PRC_ITERATION_EXEC + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + " and PRC_ID = "
                + processId;
        DatabaseHandler.getInstance().executeUpdate(database, query);
    }

    public void cleanIterationVariables(String runId, String iterationList)  {
        String query = "delete from " + PRC_ITERATION_EXEC + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + " and LIST_NM = "
                + SQLTools.GetStringForSQL(iterationList) + ";";
        DatabaseHandler.getInstance().executeUpdate(database, query);
    }


    private String truncateRuntimeVariableValue(String value) {
        if (value == null) {
            return null;
        } else {
            return value.substring(0, Math.min(RUNTIME_VAR_VALUE_MAX_LENGTH, value.length()));
        }
    }


    public void setIterationValues(String runId, String iterationList, String values)  {
        cleanIterationVariables(runId, iterationList);

        // Iterate over the iteration sets
        String setName;
        int setNumber = 0;

        String[] parts = values.split(",");
        for (int i = 0; i < parts.length; i++) {
            setNumber++;
            setName = "auto generated iteration set " + setNumber;
            String innerpart = parts[i];
            this.setIterationVariable(runId, -1, iterationList, -1, setName, setNumber, "key", innerpart);
        }
    }

    public void setIterationFor(String runId, String iterationList, String from, String to, String step)  {
        cleanIterationVariables(runId, iterationList);

        // Iterate over the iteration sets
        String setName ;
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
    public void setIterationList(String runId, String iterationList, String inputList)  {
        cleanIterationVariables(runId, iterationList);

        // Get iteration variable configuration
        IterationVariableConfiguration iterationVariableConfiguration = new IterationVariableConfiguration(runCacheFolderName, false);
        CachedRowSet crs = iterationVariableConfiguration.getIterationList(runId, inputList);

        // Iterate over the iteration sets
        String setName;
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
                                     String name, String value)  {
        value = truncateRuntimeVariableValue(value);
        String query = "INSERT INTO " + PRC_ITERATION_EXEC
                + "(run_id, prc_id, list_id, list_nm, set_id, set_nm, order_nb, var_nm, var_val) VALUES ("
                + SQLTools.GetStringForSQL(runId) + ","
                + SQLTools.GetStringForSQL(-1) + ","
                + SQLTools.GetStringForSQL(listId) + ","
                + SQLTools.GetStringForSQL(listName) + ","
                + SQLTools.GetStringForSQL(setId) + ","
                + SQLTools.GetStringForSQL(setName) + ","
                + SQLTools.GetStringForSQL(order) + ","
                + SQLTools.GetStringForSQL(name) + ","
                + SQLTools.GetStringForSQL(value) + ");";
        DatabaseHandler.getInstance().executeUpdate(database , query);

    }

    public IterationInstance hasNext(String runId, long orderNumber)  {
        String query = "select run_id, prc_id, list_id, list_nm, set_id, set_nm, order_nb, var_nm, var_val from "
                + PRC_ITERATION_EXEC + " where run_id = " + SQLTools.GetStringForSQL(runId) + " and order_nb = " + SQLTools.GetStringForSQL(orderNumber);
        CachedRowSet crs = DatabaseHandler.getInstance().executeQuery(database, query);
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

    public IterationInstance hasNext(String condition, ActionExecution actionExecution)  {
        IterationInstance iterationInstance = new IterationInstance();

        try {
            if (ConditionService.getInstance().evaluateCondition(condition, executionControl.getExecutionRuntime(), actionExecution)) {
                iterationInstance.setEmpty(false);
                iterationInstance.getVariableMap().put("iterate", "y");
            }
        } catch (ScriptException ignored) {}

        return iterationInstance;
    }

    public IterationInstance hasNextListItem(String runId, String listName, long orderNumber)  {
        String query = "select run_id, prc_id, list_id, list_nm, set_id, set_nm, order_nb, var_nm, var_val from "
                + PRC_ITERATION_EXEC + " where run_id = " + SQLTools.GetStringForSQL(runId) + " and list_nm = " + SQLTools.GetStringForSQL(listName)
                + " and order_nb = " + SQLTools.GetStringForSQL(orderNumber) + ";";
        CachedRowSet crs = DatabaseHandler.getInstance().executeQuery(database, query);
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

}