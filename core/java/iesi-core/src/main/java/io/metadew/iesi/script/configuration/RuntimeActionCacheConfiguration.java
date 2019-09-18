package io.metadew.iesi.script.configuration;

import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.RuntimeActionCache;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RuntimeActionCacheConfiguration {

    private final SqliteDatabase sqliteDatabase;
    private String runCacheFileName = "runtimeCache.db3";
    private String PRC_RUN_CACHE = "PRC_RUN_CACHE";

    // Constructors
    public RuntimeActionCacheConfiguration(String runCacheFolderName) {
        // Define path
        // Create database
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
                ")";
        sqliteDatabase.executeUpdate(query);
    }

    // Methods
    public void cleanRuntimeCache(String runId) {
        String query = "delete from " + PRC_RUN_CACHE
                + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + "";
        sqliteDatabase.executeUpdate(query);
    }

    public void cleanRuntimeCache(String runId, long processId) {
        String query = "delete from " + PRC_RUN_CACHE
                + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + " and PRC_ID = " + processId;
        sqliteDatabase.executeUpdate(query);
    }

    public void setRuntimeCache(String runId, String type, String name, String value) {
        // Verify if name already exists
        List<String> queries = new ArrayList<>();
        CachedRowSet crs = sqliteDatabase.executeQuery(
                "select run_id, prc_id,cache_typ_nm, cache_nm, cache_val from "
                + PRC_RUN_CACHE + " where run_id = "
                + SQLTools.GetStringForSQL(runId) + " and cache_nm = " + SQLTools.GetStringForSQL(name) + ";");

        // if so, the previous values will be deleted
        if (crs.size() > 0) {
            queries.add("delete from " + PRC_RUN_CACHE +
                    " where run_id = " + SQLTools.GetStringForSQL(runId) +
                    " and cache_typ_nm = " + SQLTools.GetStringForSQL(type) +
                    " and cache_nm = " + SQLTools.GetStringForSQL(name) + ";");
        }

        queries.add("delete from " + PRC_RUN_CACHE +
                " where run_id = " + SQLTools.GetStringForSQL(runId) +
                " and cache_typ_nm = " + SQLTools.GetStringForSQL(type) +
                " and cache_nm = " + SQLTools.GetStringForSQL(name) + ";");

        // new values can be stored
        queries.add("INSERT INTO " + PRC_RUN_CACHE + "(run_id, prc_id, cache_typ_nm, cache_nm, cache_val) VALUES (" +
                SQLTools.GetStringForSQL(runId) + "," +
                SQLTools.GetStringForSQL(-1) + "," +
                SQLTools.GetStringForSQL(type) + "," +
                SQLTools.GetStringForSQL(name) + "," +
                SQLTools.GetStringForSQL(value) + ")");
        sqliteDatabase.executeBatch(queries);

    }

    public String getRuntimeCacheValue(String runId, String type, String name) {
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
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return value;
    }

    public RuntimeActionCache getRuntimeActionCache(String runId, String type, String name) {
        RuntimeActionCache runtimeCache = new RuntimeActionCache();
        runtimeCache.setName(name);

        CachedRowSet crs = null;
        String query = "select CACHE_VAL from " + PRC_RUN_CACHE
                + " where run_id = " + SQLTools.GetStringForSQL(runId) + " and cache_typ_nm = " + SQLTools.GetStringForSQL(type) + " and cache_nm = " + SQLTools.GetStringForSQL(name) + ";";
        crs = sqliteDatabase.executeQuery(query);
        String value = "";
        try {
            while (crs.next()) {
                value = crs.getString("CACHE_VAL");
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        runtimeCache.setValue(value);
        return runtimeCache;
    }

}