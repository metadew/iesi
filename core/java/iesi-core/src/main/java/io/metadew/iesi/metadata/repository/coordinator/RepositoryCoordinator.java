package io.metadew.iesi.metadata.repository.coordinator;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.metadata.definition.MetadataTable;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RepositoryCoordinator {

    private Map<String, Database> databases;

    public RepositoryCoordinator(Map<String, Database> databases) {
        this.databases = databases;
    }

    public String getAllTablesQuery(String pattern) {
        return databases.get("reader").getAllTablesQuery(pattern);
    }

    public CachedRowSet executeQuery(String query, String logonType) {
        CachedRowSet crs;
        try {
            crs = this.databases.get(logonType).executeQuery(query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return crs;
    }

    public void executeUpdate(String query)  {
        this.databases.get("writer").executeUpdate(query);
    }
    public void executeBatch(List<String> queries)  {
        this.databases.get("writer").executeBatch(queries);
    }

    public void executeScript(String fileName, String logonType)  {
        SqlScriptResult dcSQLScriptResult = this.databases.get(logonType).executeScript(fileName);

        if (dcSQLScriptResult.getReturnCode() != 0) {
            throw new RuntimeException("Error executing SQL script");
        }
    }

    public void executeScript(InputStream inputStream, String logonType)  {
        SqlScriptResult dcSQLScriptResult = this.databases.get(logonType).executeScript(inputStream);

        if (dcSQLScriptResult.getReturnCode() != 0) {
            throw new RuntimeException("Error executing SQL script");
        }
    }

    public void cleanTable(MetadataTable table)  {
        this.databases.get("writer").cleanTable(table);
    }

    public void dropTable(MetadataTable table)  {
        this.databases.get("owner").dropTable(table);
    }

    public void createTable(MetadataTable table)  {
        this.databases.get("owner").createTable(table);
    }

    public String getCreateStatement(MetadataTable table) {
        return this.databases.get("reader").getCreateStatement(table);
    }

    public String getDropStatement(MetadataTable table) {
        return this.databases.get("reader").getDropStatement(table);
    }

    public Map<String, Database> getDatabases() {
        return databases;
    }

    public void shutdown() {
        for (Database database : databases.values()) {
            database.shutdown();
        }
    }
}
