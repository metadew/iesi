package io.metadew.iesi.script.configuration;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.h2.H2Database;
import io.metadew.iesi.connection.database.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class IterationVariableConfiguration {

    private final H2Database database;
    private String runCacheFileName = "iterationVariables.db3";
    private String PRC_ITERATION_VAR = "PRC_ITERATION_VAR";
    private final static int RUNTIME_VAR_VALUE_MAX_LENGTH = 4000;

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);

    // Constructors
    public IterationVariableConfiguration(String runCacheFolderName, boolean initialize)  {
        // Create database
        this.database = new H2Database(new H2MemoryDatabaseConnection(runCacheFolderName + File.separator + runCacheFileName, "sa", "", null));

        // Initialize
        if (initialize) {
            this.createIterationVarTable();
        }
    }

    private String truncateRuntimeVariableValue(String value) {
        if (value == null) {
            return null;
        } else {
            return value.substring(0, Math.min(RUNTIME_VAR_VALUE_MAX_LENGTH, value.length()));
        }
    }

    private void createIterationVarTable()  {
        String query = "CREATE TABLE " + PRC_ITERATION_VAR + " (" + "RUN_ID VARCHAR(200) NOT NULL,"
                + "PRC_ID INT NOT NULL, LIST_ID INT NOT NULL, LIST_NM VARCHAR(200) NOT NULL,"
                + "SET_ID INT NOT NULL, SET_NM TEXT NOT NULL, ORDER_NB INT NOT NULL,"
                + "VAR_NM VARCHAR(200) NOT NULL,VAR_VAL VARCHAR("+RUNTIME_VAR_VALUE_MAX_LENGTH+"));";
        databaseHandler.executeUpdate(database, query);
    }

    // Methods
    public void cleanIterationVariables(String runId)  {
        String query = "delete from " + PRC_ITERATION_VAR + " where RUN_ID = " + SQLTools.getStringForSQL(runId) + ";";
        databaseHandler.executeUpdate(database, query);
    }

    public void cleanIterationVariables(String runId, long processId)  {
        String query = "delete from " + PRC_ITERATION_VAR
                + " where RUN_ID = " + SQLTools.getStringForSQL(runId)
                + " and PRC_ID = " + SQLTools.getStringForSQL(processId) + ";";
        databaseHandler.executeUpdate(database, query);
    }

    public void cleanIterationVariables(String runId, String iterationList)  {
        String query = "delete from " + PRC_ITERATION_VAR
                + " where RUN_ID = " + SQLTools.getStringForSQL(runId)
                + " and LIST_NM = " + SQLTools.getStringForSQL(iterationList) + ";";
        databaseHandler.executeUpdate(database, query);
    }

    public void setIterationList(String runId, String iterationList, ResultSet resultSet)  {
        try {
            cleanIterationVariables(runId, iterationList);

            // Iterate over the iteration sets
            int setNumber = 0;

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columns = resultSetMetaData.getColumnCount();

            resultSet.beforeFirst();
            String setName;
            while (resultSet.next()) {
                setNumber++;
                setName = "auto generated iteration set " + setNumber;

                // Iterate over the iteration variables
                for (int i = 1; i < columns + 1; i++) {
                    setIterationVariable(runId, -1, iterationList, -1, setName, setNumber, resultSetMetaData.getColumnName(i), resultSet.getString(i));
                }
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setIterationVariable(String runId, int listId, String listName, int setId, String setName, int order,
                                     String name, String value)  {
        value = truncateRuntimeVariableValue(value);
        String query = "INSERT INTO " + PRC_ITERATION_VAR
                + "(run_id, prc_id, list_id, list_nm, set_id, set_nm, order_nb, var_nm, var_val) VALUES ("
                + SQLTools.getStringForSQL(runId) + ","
                + SQLTools.getStringForSQL(-1) + ","
                + SQLTools.getStringForSQL(listId) + ","
                + SQLTools.getStringForSQL(listName) + ","
                + SQLTools.getStringForSQL(setId) + ","
                + SQLTools.getStringForSQL(setName) + ","
                + SQLTools.getStringForSQL(order) + ","
                + SQLTools.getStringForSQL(name) + ","
                + SQLTools.getStringForSQL(value) + ");";
        databaseHandler.executeUpdate(database, query);

    }

    public CachedRowSet getIterationList(String runId, String name)  {
        String query = "select run_id, prc_id, list_id, list_nm, set_id, set_nm, order_nb, var_nm, var_val from "
                + PRC_ITERATION_VAR + " where run_id = " + SQLTools.getStringForSQL(runId) + " and list_nm = " + SQLTools.getStringForSQL(name)
                + " order by order_nb asc, var_nm asc";
        return databaseHandler.executeQuery(database, query);
    }

}