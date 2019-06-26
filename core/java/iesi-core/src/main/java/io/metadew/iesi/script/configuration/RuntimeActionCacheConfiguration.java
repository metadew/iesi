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

public class RuntimeActionCacheConfiguration {

    private RuntimeActionCache runtimeActionCache;
    private FrameworkExecution frameworkExecution;
    private String runCacheFolderName;
    private String runCacheFileName = "runtimeCache.db3";
    private String runCacheFilePath;
    private SqliteDatabaseConnection sqliteDatabaseConnection;
    private String PRC_RUN_CACHE = "PRC_RUN_CACHE";

    // Constructors
    public RuntimeActionCacheConfiguration(FrameworkExecution frameworkExecution, String runCacheFolderName) {
        this.setFrameworkExecution(frameworkExecution);

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
        String query = "";

        query = "select run_id, prc_id,cache_typ_nm, cache_nm, cache_val from "
                + this.getPRC_RUN_CACHE() + " where run_id = '"
                + runId + "' and cache_nm = '" + name + "'";
        CachedRowSet crs = null;
        crs = this.getSqliteDatabaseConnection().executeQuery(query);

        // if so, the previous values will be deleted
        if (SQLTools.getRowCount(crs) > 0) {
            query = "delete from " + this.getPRC_RUN_CACHE()
                    + " where run_id = '" + runId + "' and cache_typ_nm = '" + type + "' and cache_nm = '" + name + "'";
            this.getSqliteDatabaseConnection().executeUpdate(query);
        }

        // new values can be stored
        query = "";
        query = "INSERT INTO " + this.getPRC_RUN_CACHE();
        query = query + "(run_id, prc_id, cache_typ_nm, cache_nm, cache_val)";
        query = query + " VALUES (";
        query += SQLTools.GetStringForSQL(runId);
        query += ",";
        query += SQLTools.GetStringForSQL(-1);
        query += ",";
        query += SQLTools.GetStringForSQL(type);
        query += ",";
        query += SQLTools.GetStringForSQL(name);
        query += ",";
        query += SQLTools.GetStringForSQL(value);
        query += ")";
        this.getSqliteDatabaseConnection().executeUpdate(query);

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