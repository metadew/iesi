package io.metadew.iesi.script.configuration;

import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.RuntimeActionCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.sql.SQLException;

public class RuntimeActionCacheConfiguration {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SqliteDatabase sqliteDatabase;
    private String runCacheFileName = "runtimeActionCache.db3";
    private String PRC_RUN_CACHE = "PRC_RUN_CACHE";

    // Constructors
    public RuntimeActionCacheConfiguration(String runCacheFolderName) {
        this.sqliteDatabase = new SqliteDatabase(new SqliteDatabaseConnection(runCacheFolderName + File.separator + runCacheFileName));
        createRunCacheTable();
    }

    private void createRunCacheTable() {
        String query = "CREATE TABLE " + PRC_RUN_CACHE + " (" +
                "RUN_ID TEXT NOT NULL," +
                "PRC_ID NUMERIC NOT NULL," +
                "CACHE_TYP_NM NUMERIC NOT NULL," +
                "CACHE_NM TEXT NOT NULL," +
                "CACHE_VAL TEXT" +
                ");";
        sqliteDatabase.executeUpdate(query);
    }

    // Methods
    public void cleanRuntimeCache(String runId) {
        String query = "delete from " + PRC_RUN_CACHE + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + ";";
        sqliteDatabase.executeUpdate(query);
    }

    public void cleanRuntimeCache(String runId, long processId) {
        String query = "delete from " + PRC_RUN_CACHE
                + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + " and PRC_ID = " + SQLTools.GetStringForSQL(processId) + ";";
        sqliteDatabase.executeUpdate(query);
    }

    public void setRuntimeCache(String runId, Long processId, String type, String name, String value) {
        // Verify if name already exists
        try {
            CachedRowSet crs = sqliteDatabase.executeQuery(
                    "select run_id, prc_id,cache_typ_nm, cache_nm, cache_val from " + PRC_RUN_CACHE +
                            " where run_id = " + SQLTools.GetStringForSQL(runId) +
                            " and prc_id = " + SQLTools.GetStringForSQL(processId) +
                            " and cache_typ_nm = " + SQLTools.GetStringForSQL(type) +
                            " and cache_nm = " + SQLTools.GetStringForSQL(name) + ";");

            // if so, the previous values will be deleted
            if (crs.size() > 0) {
                sqliteDatabase.executeUpdate("delete from " + PRC_RUN_CACHE +
                        " where run_id = " + SQLTools.GetStringForSQL(runId) +
                        " and cache_typ_nm = " + SQLTools.GetStringForSQL(type) +
                        " and prc_id = " + SQLTools.GetStringForSQL(processId) +
                        " and cache_nm = " + SQLTools.GetStringForSQL(name) + ";");
            }
            crs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // new values can be stored
        sqliteDatabase.executeUpdate("INSERT INTO " + PRC_RUN_CACHE + "(run_id, prc_id, cache_typ_nm, cache_nm, cache_val) VALUES (" +
                SQLTools.GetStringForSQL(runId) + "," +
                SQLTools.GetStringForSQL(processId) + "," +
                SQLTools.GetStringForSQL(type) + "," +
                SQLTools.GetStringForSQL(name) + "," +
                SQLTools.GetStringForSQL(value) + ");");

    }

    public String getRuntimeCacheValue(String runId, Long processId, String type, String name) {
        String query = "select CACHE_VAL from " + PRC_RUN_CACHE
                + " where run_id = " + SQLTools.GetStringForSQL(runId)
                + " and prc_id = " + SQLTools.GetStringForSQL(processId)
                + " and cache_typ_nm = " + SQLTools.GetStringForSQL(type)
                + " and cache_nm = " + SQLTools.GetStringForSQL(name) + ";";
        CachedRowSet crs = sqliteDatabase.executeQuery(query);
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

    public RuntimeActionCache getRuntimeActionCache(String runId, String type, String name) {
        RuntimeActionCache runtimeCache = new RuntimeActionCache();
        runtimeCache.setName(name);

        String query = "select CACHE_VAL from " + PRC_RUN_CACHE
                + " where run_id = " + SQLTools.GetStringForSQL(runId) + " and cache_typ_nm = " + SQLTools.GetStringForSQL(type) + " and cache_nm = " + SQLTools.GetStringForSQL(name) + ";";
        CachedRowSet crs = sqliteDatabase.executeQuery(query);
        String value = "";
        try {
            while (crs.next()) {
                value = crs.getString("CACHE_VAL");
            }
            crs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        runtimeCache.setValue(value);
        return runtimeCache;
    }

}