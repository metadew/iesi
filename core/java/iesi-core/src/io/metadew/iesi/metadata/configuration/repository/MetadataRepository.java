package io.metadew.iesi.metadata.configuration.repository;

import io.metadew.iesi.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.util.Map;

public class MetadataRepository {
    private Map<String, DatabaseConnection> databaseConnections;
    private String category;

    public String getSystemTimestampExpression() {
        // TODO: make this class responsible for the query generation for its behaviour -> get script, action. Make subclasses of MetadataRepository based on category
        return databaseConnections.get("owner").getSystemTimestampExpression();
    }

    public MetadataRepository(String category, Map<String, DatabaseConnection> databaseConnections) {
        this.category = category;
        this.databaseConnections = databaseConnections;
    }

    public CachedRowSet executeQuery(String query, String logonType) {
        CachedRowSet crs;
        try {
            crs = this.databaseConnections.get(logonType).executeQuery(query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return crs;
    }

    public void executeUpdate(String query, String logonType) {
        this.databaseConnections.get(logonType).executeUpdate(query);
    }

    public void executeScript(String fileName, String logonType) {
        SqlScriptResult dcSQLScriptResult = this.databaseConnections.get(logonType).executeScript(fileName);

        if (dcSQLScriptResult.getReturnCode() != 0) {
            throw new RuntimeException("Error executing SQL script");
        }
    }

    public void executeScript(InputStream inputStream, String logonType) {
        SqlScriptResult dcSQLScriptResult = this.databaseConnections.get(logonType).executeScript(inputStream);

        if (dcSQLScriptResult.getReturnCode() != 0) {
            throw new RuntimeException("Error executing SQL script");
        }
    }

    public void dropTable(String schemaName, String tableName, String logonType) {
        String queryDropTable;
        if (schemaName.equals("")) {
            queryDropTable = "drop table " + tableName;
        } else {
            queryDropTable = "drop table " + schemaName + "." + tableName;
        }
        this.databaseConnections.get(logonType).executeUpdate(queryDropTable);
    }

    public void cleanTable(String schemaName, String tableName, String logonType) {
        String queryCleanTable;
        if (schemaName.equals("")) {
            queryCleanTable = "delete from " + tableName;
        } else {
            queryCleanTable = "delete from " + schemaName + "." + tableName;
        }
        this.databaseConnections.get(logonType).executeUpdate(queryCleanTable);
    }

    public String getCategory() {
        return this.category;
    }

    public Map<String, DatabaseConnection> getDatabaseConnections() {
        return databaseConnections;
    }
}
