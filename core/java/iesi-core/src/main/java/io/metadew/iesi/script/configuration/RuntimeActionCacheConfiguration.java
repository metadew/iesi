package io.metadew.iesi.script.configuration;

import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.RuntimeActionCache;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RuntimeActionCacheConfiguration {

    private RuntimeActionCache runtimeActionCache;
    private String runCacheFolderName;
    private String runCacheFileName = "runtimeCache.db3";
    private String runCacheFilePath;
    private SqliteDatabaseConnection sqliteDatabaseConnection;
    private String PRC_RUN_CACHE = "PRC_RUN_CACHE";

    // Constructors
    public RuntimeActionCacheConfiguration(String runCacheFolderName) {
        // Define path
        this.setRunCacheFolderName(runCacheFolderName);
        this.setRunCacheFilePath(this.getRunCacheFolderName() + File.separator + this.getRunCacheFileName());

        // Create database
        this.setSqliteDatabaseConnection(new SqliteDatabaseConnection(this.getRunCacheFilePath()));
        this.createRunCacheTable();

    }

    private void createRunCacheTable() {
        String query = "CREATE TABLE " + this.getPRC_RUN_CACHE() + " (" +
                "RUN_ID TEXT NOT NULL," +
                "PRC_ID NUMERIC NOT NULL," +
                "CACHE_TYP_NM NUMERIC NOT NULL," +
                "CACHE_NM TEXT NOT NULL," +
                "CACHE_VAL TEXT" +
                ")";
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    // Methods
    public void cleanRuntimeCache(String runId) {
        String query = "delete from " + this.getPRC_RUN_CACHE()
                + " where RUN_ID = '" + runId + "'";
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    public void cleanRuntimeCache(String runId, long processId) {
        String query = "delete from " + this.getPRC_RUN_CACHE()
                + " where RUN_ID = '" + runId + "' and PRC_ID = " + processId;
        this.getSqliteDatabaseConnection().executeUpdate(query);
    }

    public void setRuntimeCache(String runId, String type, String name, String value) {
        // Verify if name already exists
        List<String> queries = new ArrayList<>();
        CachedRowSet crs = this.getSqliteDatabaseConnection().executeQuery(
                "select run_id, prc_id,cache_typ_nm, cache_nm, cache_val from "
                + this.getPRC_RUN_CACHE() + " where run_id = "
                + SQLTools.GetStringForSQL(runId) + " and cache_nm = " + SQLTools.GetStringForSQL(name) + ";");

        // if so, the previous values will be deleted
        if (crs.size() > 0) {
            queries.add("delete from " + this.getPRC_RUN_CACHE() +
                    " where run_id = " + SQLTools.GetStringForSQL(runId) +
                    " and cache_typ_nm = " + SQLTools.GetStringForSQL(type) +
                    " and cache_nm = " + SQLTools.GetStringForSQL(name) + ";");
        }

        queries.add("delete from " + this.getPRC_RUN_CACHE() +
                " where run_id = " + SQLTools.GetStringForSQL(runId) +
                " and cache_typ_nm = " + SQLTools.GetStringForSQL(type) +
                " and cache_nm = " + SQLTools.GetStringForSQL(name) + ";");

        // new values can be stored
        queries.add("INSERT INTO " + this.getPRC_RUN_CACHE() + "(run_id, prc_id, cache_typ_nm, cache_nm, cache_val) VALUES (" +
                SQLTools.GetStringForSQL(runId) + "," +
                SQLTools.GetStringForSQL(-1) + "," +
                SQLTools.GetStringForSQL(type) + "," +
                SQLTools.GetStringForSQL(name) + "," +
                SQLTools.GetStringForSQL(value) + ")");
        this.getSqliteDatabaseConnection().executeBatch(queries);

    }

    public String getRuntimeCacheValue(String runId, String type, String name) {
        CachedRowSet crs = null;
        String query = "select CACHE_VAL from " + this.getPRC_RUN_CACHE()
                + " where run_id = '" + runId + "' and cache_typ_nm = '" + type + "' and cache_nm = '" + name + "'";
        crs = this.getSqliteDatabaseConnection().executeQuery(query);
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
        String query = "select CACHE_VAL from " + this.getPRC_RUN_CACHE()
                + " where run_id = '" + runId + "' and cache_typ_nm = '" + type + "' and cache_nm = '" + name + "'";
        crs = this.getSqliteDatabaseConnection().executeQuery(query);
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

    public RuntimeActionCache getRuntimeActionCache() {
        return runtimeActionCache;
    }

    public void setRuntimeActionCache(RuntimeActionCache runtimeActionCache) {
        this.runtimeActionCache = runtimeActionCache;
    }

    public String getPRC_RUN_CACHE() {
        return PRC_RUN_CACHE;
    }

    public void setPRC_RUN_CACHE(String pRC_RUN_CACHE) {
        PRC_RUN_CACHE = pRC_RUN_CACHE;
    }

}