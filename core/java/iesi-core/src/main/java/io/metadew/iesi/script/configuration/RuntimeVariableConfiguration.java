package io.metadew.iesi.script.configuration;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.h2.H2Database;
import io.metadew.iesi.connection.database.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.sql.SQLException;
import java.util.Optional;

public class RuntimeVariableConfiguration {

    private final H2Database database;
    private final static String runCacheFileName = "runtimeVariables.db3";
    private final static String PRC_RUN_VAR = "PRC_RUN_VAR";
    private final static int RUNTIME_VAR_VALUE_MAX_LENGTH = 4000;

    // Constructors
    public RuntimeVariableConfiguration(String runCacheFolderName) {
        database = new H2Database(new H2MemoryDatabaseConnection(runCacheFolderName + File.separator + runCacheFileName, "sa", "", null));
        String query = "CREATE TABLE " + PRC_RUN_VAR + " (" +
                "RUN_ID VARCHAR(200) NOT NULL," +
                "PRC_ID INT NOT NULL," +
                "VAR_NM VARCHAR(200) NOT NULL," +
                "VAR_VAL VARCHAR("+RUNTIME_VAR_VALUE_MAX_LENGTH+")" +
                ")";
        DatabaseHandler.getInstance().executeUpdate(database, query);
    }

    // Methods
    public void cleanRuntimeVariables(String runId) {
        String query = "delete from " + PRC_RUN_VAR
                + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + ";";
        DatabaseHandler.getInstance().executeUpdate(database, query);
    }

    public void cleanRuntimeVariables(String runId, long processId) {
        String query = "delete from " + PRC_RUN_VAR
                + " where RUN_ID = " + SQLTools.GetStringForSQL(runId)
                + " and PRC_ID = " + SQLTools.GetStringForSQL(processId) + ";";
        DatabaseHandler.getInstance().executeUpdate(database, query);
    }

    public void setRuntimeVariable(String runId, Long processId, String name, String value) {
        value = truncateRuntimeVariableValue(value);
        try {
            // Verify if variable already exists
            String query = "select run_id, prc_id, var_nm, var_val from " + PRC_RUN_VAR
                    + " where run_id = " + SQLTools.GetStringForSQL(runId)
                    + " and prc_id = " + SQLTools.GetStringForSQL(processId)
                    + " and var_nm = " + SQLTools.GetStringForSQL(name) + ";";
            CachedRowSet crs = DatabaseHandler.getInstance().executeQuery(database, query);

            // if so, the previous values will be deleted
            if (crs.size() > 0) {
                String deleteQuery = "delete from " + PRC_RUN_VAR
                        + " where run_id = " + SQLTools.GetStringForSQL(runId) + " and prc_id = " + SQLTools.GetStringForSQL(processId) + " and var_nm = " + SQLTools.GetStringForSQL(name) + ";";
                DatabaseHandler.getInstance().executeUpdate(database, deleteQuery);
            }
            crs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // DtNow, new values can be stored
        String insertQuery = "INSERT INTO " + PRC_RUN_VAR + "(run_id, prc_id, var_nm, var_val) VALUES ("
                + SQLTools.GetStringForSQL(runId) + ","
                + SQLTools.GetStringForSQL(processId) + ","
                + SQLTools.GetStringForSQL(name) + ","
                + SQLTools.GetStringForSQL(value) + ");";
        DatabaseHandler.getInstance().executeUpdate(database, insertQuery);
    }

    private String truncateRuntimeVariableValue(String value) {
        if (value == null) {
            return null;
        } else {
            return value.substring(0, Math.min(RUNTIME_VAR_VALUE_MAX_LENGTH, value.length()));
        }
    }

    public Optional<String> getRuntimeVariableValue(String runId, String name) {
        try {
            String query = "select VAR_VAL from " + PRC_RUN_VAR
                    + " where run_id = " + SQLTools.GetStringForSQL(runId) + " and var_nm = " + SQLTools.GetStringForSQL(name) + " ORDER BY prc_id DESC;";
            CachedRowSet crs = DatabaseHandler.getInstance().executeQuery(database, query);
            if (crs.size() == 0) {
                return Optional.empty();
            }
            // if (crs.size() > 1) {
            //    LOGGER.warn(MessageFormat.format("Found multiple implementations for RuntimeVariable {0}-{1}. Returning first implementation", runId, name));
            //}
            crs.next();
            String value = crs.getString("VAR_VAL");
            crs.close();
            return Optional.of(value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        DatabaseHandler.getInstance().shutdown(database);
    }

}