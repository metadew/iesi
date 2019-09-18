package io.metadew.iesi.script.configuration;

import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Optional;

public class RuntimeVariableConfiguration {

    private final static Logger LOGGER = LogManager.getLogger();
    private final SqliteDatabase sqliteDatabase;
    private String runCacheFileName = "runtimeVariables.db3";
    private String PRC_RUN_VAR = "PRC_RUN_VAR";

    // Constructors
    public RuntimeVariableConfiguration(String runCacheFolderName)  {

        // Create database
        sqliteDatabase = new SqliteDatabase(new SqliteDatabaseConnection(runCacheFolderName + File.separator + runCacheFileName));
        createRunVarTable();

    }

    private void createRunVarTable()  {
        String query = "CREATE TABLE " + PRC_RUN_VAR + " (" +
                "RUN_ID TEXT NOT NULL," +
                "PRC_ID NUMERIC NOT NULL," +
                "VAR_NM TEXT NOT NULL," +
                "VAR_VAL TEXT" +
                ")";
        sqliteDatabase.executeUpdate(query);
    }

    // Methods
    public void cleanRuntimeVariables(String runId)  {
        String query = "delete from " + PRC_RUN_VAR
                + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + ";";
        sqliteDatabase.executeUpdate(query);
    }

    public void cleanRuntimeVariables(String runId, long processId) {
        String query = "delete from " + PRC_RUN_VAR
                + " where RUN_ID = " + SQLTools.GetStringForSQL(runId)
                + " and PRC_ID = " + SQLTools.GetStringForSQL(processId) + ";";
        sqliteDatabase.executeUpdate(query);
    }

    public void setRuntimeVariable(String runId, Long processId, String name, String value) {
        // Verify if variable already exists
        String query = "select run_id, prc_id, var_nm, var_val from " + PRC_RUN_VAR
                + " where run_id = " + SQLTools.GetStringForSQL(runId)
                + " and prc_id = " + SQLTools.GetStringForSQL(processId)
                + " and var_nm = " + SQLTools.GetStringForSQL(name) + ";";
        CachedRowSet crs = sqliteDatabase.executeQuery(query);

        // if so, the previous values will be deleted
        if (crs.size() > 0) {
            String deleteQuery = "delete from " + PRC_RUN_VAR
                    + " where run_id = " + SQLTools.GetStringForSQL(runId) + " and prc_id = " + SQLTools.GetStringForSQL(processId) + " and var_nm = " + SQLTools.GetStringForSQL(name) + ";";
            sqliteDatabase.executeUpdate(deleteQuery);
        }

        // DtNow, new values can be stored
        String inserQuery = "INSERT INTO " + PRC_RUN_VAR + "(run_id, prc_id, var_nm, var_val) VALUES ("
                + SQLTools.GetStringForSQL(runId) + ","
                + SQLTools.GetStringForSQL(processId) + ","
                + SQLTools.GetStringForSQL(name) + ","
                + SQLTools.GetStringForSQL(value) + ");";
        sqliteDatabase.executeUpdate(inserQuery);
    }

    public Optional<String> getRuntimeVariableValue(String runId, String name) {
        try {
            String query = "select VAR_VAL from " + PRC_RUN_VAR
                    + " where run_id = " + SQLTools.GetStringForSQL(runId) + " and var_nm = " + SQLTools.GetStringForSQL(name) + ";";
            CachedRowSet crs = sqliteDatabase.executeQuery(query);
            if (crs.size() == 0) {
                return Optional.empty();
            }
            if (crs.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for RuntimeVariable {0}-{1}. Returning first implementation", runId, name));
            }
            crs.next();
            String value = crs.getString("VAR_VAL");
            crs.close();
            return Optional.of(value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}