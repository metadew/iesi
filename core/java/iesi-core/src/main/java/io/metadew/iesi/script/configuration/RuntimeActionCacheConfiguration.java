package io.metadew.iesi.script.configuration;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.h2.H2Database;
import io.metadew.iesi.connection.database.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.sql.SQLException;

public class RuntimeActionCacheConfiguration {

    private final H2Database database;
    private final static String runCacheFileName = "runtimeActionCache.db3";
    private final static String PRC_RUN_CACHE = "PRC_RUN_CACHE";
    private final static int RUNTIME_VAR_VALUE_MAX_LENGTH = 4000;

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);

    // Constructors
    public RuntimeActionCacheConfiguration(String runCacheFolderName) {
        this.database = new H2Database(new H2MemoryDatabaseConnection(runCacheFolderName + File.separator + runCacheFileName, "sa", "", null));
        String query = "CREATE TABLE " + PRC_RUN_CACHE + " (" +
                "RUN_ID VARCHAR(200) NOT NULL," +
                "PRC_ID INT NOT NULL," +
                "CACHE_TYP_NM VARCHAR(200) NOT NULL," +
                "CACHE_NM VARCHAR(200) NOT NULL," +
                "CACHE_VAL VARCHAR("+RUNTIME_VAR_VALUE_MAX_LENGTH+")" +
                ");";
        databaseHandler.executeUpdate(database, query);
    }

    private String truncateRuntimeVariableValue(String value) {
        if (value == null) {
            return null;
        } else {
            return value.substring(0, Math.min(RUNTIME_VAR_VALUE_MAX_LENGTH, value.length()));
        }
    }

    public void setRuntimeCache(String runId, Long processId, String type, String name, String value) {
        // Verify if name already exists
        value = truncateRuntimeVariableValue(value);
        try {
            CachedRowSet crs = databaseHandler.executeQuery(database,
                    "select run_id, prc_id, cache_typ_nm, cache_nm, cache_val from " + PRC_RUN_CACHE +
                            " where run_id = " + SQLTools.getStringForSQL(runId) +
                            " and prc_id = " + SQLTools.getStringForSQL(processId) +
                            " and cache_typ_nm = " + SQLTools.getStringForSQL(type) +
                            " and cache_nm = " + SQLTools.getStringForSQL(name) + ";");

            // if so, the previous values will be deleted
            if (crs.size() > 0) {
                databaseHandler.executeUpdate(database, "delete from " + PRC_RUN_CACHE +
                        " where run_id = " + SQLTools.getStringForSQL(runId) +
                        " and cache_typ_nm = " + SQLTools.getStringForSQL(type) +
                        " and prc_id = " + SQLTools.getStringForSQL(processId) +
                        " and cache_nm = " + SQLTools.getStringForSQL(name) + ";");
            }
            crs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // new values can be stored
        databaseHandler.executeUpdate(database, "INSERT INTO " + PRC_RUN_CACHE + "(run_id, prc_id, cache_typ_nm, cache_nm, cache_val) VALUES (" +
                SQLTools.getStringForSQL(runId) + "," +
                SQLTools.getStringForSQL(processId) + "," +
                SQLTools.getStringForSQL(type) + "," +
                SQLTools.getStringForSQL(name) + "," +
                SQLTools.getStringForSQL(value) + ");");

    }

    public String getRuntimeCacheValue(String runId, Long processId, String type, String name) {
        String query = "select CACHE_VAL from " + PRC_RUN_CACHE
                + " where run_id = " + SQLTools.getStringForSQL(runId)
                + " and prc_id = " + SQLTools.getStringForSQL(processId)
                + " and cache_typ_nm = " + SQLTools.getStringForSQL(type)
                + " and cache_nm = " + SQLTools.getStringForSQL(name) + ";";
        CachedRowSet crs = databaseHandler.executeQuery(database, query);
        String value = "";
        try {
            while (crs.next()) {
                value = crs.getString("CACHE_VAL");
            }
            crs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public void shutdown() {
        databaseHandler.shutdown(database);
    }

}