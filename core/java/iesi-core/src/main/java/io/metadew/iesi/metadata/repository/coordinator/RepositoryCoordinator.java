package io.metadew.iesi.metadata.repository.coordinator;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.metadata.definition.MetadataTable;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class RepositoryCoordinator {

    private Map<String, Database> databases;

    public RepositoryCoordinator(Map<String, Database> databases) {
        this.databases = databases;
    }

    public String getAllTablesQuery(String pattern) {
        return DatabaseHandler.getInstance().getAllTablesQuery(databases.get("reader"), pattern);
    }

    public CachedRowSet executeQuery(String query, String logonType) {
        CachedRowSet crs;
        crs = DatabaseHandler.getInstance().executeQuery(this.databases.get(logonType), query);
        return crs;
    }

    public void executeUpdate(String query) {
        DatabaseHandler.getInstance().executeUpdate(this.databases.get("writer"), query);
    }

    public void executeBatch(List<String> queries) {
        DatabaseHandler.getInstance().executeBatch(this.databases.get("writer"), queries);
    }

    public void executeScript(String fileName, String logonType) {
        SqlScriptResult dcSQLScriptResult = DatabaseHandler.getInstance().executeScript(this.databases.get(logonType), fileName);

        if (dcSQLScriptResult.getReturnCode() != 0) {
            throw new RuntimeException("Error executing SQL script");
        }
    }

    public void executeScript(InputStream inputStream, String logonType) {
        SqlScriptResult dcSQLScriptResult = DatabaseHandler.getInstance().executeScript(this.databases.get(logonType), inputStream);

        if (dcSQLScriptResult.getReturnCode() != 0) {
            throw new RuntimeException("Error executing SQL script");
        }
    }

    public void cleanTable(MetadataTable table) {
        DatabaseHandler.getInstance().cleanTable(this.databases.get("writer"), table);
    }

    public void dropTable(MetadataTable table) {
        DatabaseHandler.getInstance().dropTable(this.databases.get("owner"), table);
    }

    public void createTable(MetadataTable table) {
        DatabaseHandler.getInstance().createTable(this.databases.get("owner"), table);
    }

    public String getCreateStatement(MetadataTable table) {
        return DatabaseHandler.getInstance().getCreateStatement(this.databases.get("reader"), table);
    }

    public String getDropStatement(MetadataTable table) {
        return DatabaseHandler.getInstance().getDropStatement(this.databases.get("reader"), table);
    }

    public Map<String, Database> getDatabases() {
        return databases;
    }

    public void shutdown() {
        for (Database database : databases.values()) {
            DatabaseHandler.getInstance().shutdown(database);
        }
    }
}
