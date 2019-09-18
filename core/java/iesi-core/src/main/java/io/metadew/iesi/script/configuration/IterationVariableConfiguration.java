package io.metadew.iesi.script.configuration;

import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class IterationVariableConfiguration {

    private final SqliteDatabase sqliteDatabase;
    private String runCacheFolderName;
    private String runCacheFileName = "iterationVariables.db3";
    private String runCacheFilePath;
    private String PRC_ITERATION_VAR = "PRC_ITERATION_VAR";

    // Constructors
    public IterationVariableConfiguration(String runCacheFolderName, boolean initialize)  {
        // Create database
        this.sqliteDatabase = new SqliteDatabase(new SqliteDatabaseConnection(runCacheFolderName + File.separator + runCacheFileName));

        // Initialize
        if (initialize) {
            this.createIterationVarTable();
        }

    }

    private void createIterationVarTable()  {
        String query = "CREATE TABLE " + PRC_ITERATION_VAR + " (" + "RUN_ID TEXT NOT NULL,"
                + "PRC_ID NUMERIC NOT NULL,LIST_ID NUMERIC NOT NULL,LIST_NM TEXT NOT NULL,"
                + "SET_ID NUMERIC NOT NULL,SET_NM TEXT NOT NULL,ORDER_NB NUMERIC NOT NULL,"
                + "VAR_NM TEXT NOT NULL,VAR_VAL TEXT);";
        sqliteDatabase.executeUpdate(query);
    }

    // Methods
    public void cleanIterationVariables(String runId)  {
        String query = "delete from " + PRC_ITERATION_VAR + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + ";";
        sqliteDatabase.executeUpdate(query);
    }

    public void cleanIterationVariables(String runId, long processId)  {
        String query = "delete from " + PRC_ITERATION_VAR
                + " where RUN_ID = " + SQLTools.GetStringForSQL(runId)
                + " and PRC_ID = " + SQLTools.GetStringForSQL(processId) + ";";
        sqliteDatabase.executeUpdate(query);
    }

    public void cleanIterationVariables(String runId, String iterationList)  {
        String query = "delete from " + PRC_ITERATION_VAR
                + " where RUN_ID = " + SQLTools.GetStringForSQL(runId)
                + " and LIST_NM = " + SQLTools.GetStringForSQL(iterationList) + ";";
        sqliteDatabase.executeUpdate(query);
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
        String query = "INSERT INTO " + PRC_ITERATION_VAR
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
        sqliteDatabase.executeUpdate(query);

    }


    public CachedRowSet getIterationList(String runId, String name)  {
        String query = "select run_id, prc_id, list_id, list_nm, set_id, set_nm, order_nb, var_nm, var_val from "
                + PRC_ITERATION_VAR + " where run_id = " + SQLTools.GetStringForSQL(runId) + " and list_nm = " + SQLTools.GetStringForSQL(name)
                + " order by order_nb asc, var_nm asc";
        return sqliteDatabase.executeQuery(query);
    }

}