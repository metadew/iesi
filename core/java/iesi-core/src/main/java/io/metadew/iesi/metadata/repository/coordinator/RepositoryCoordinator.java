package io.metadew.iesi.metadata.repository.coordinator;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandlerImpl;
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
        return DatabaseHandlerImpl.getInstance().getAllTablesQuery(databases.get("reader"), pattern);
    }

    public CachedRowSet executeQuery(String query, String logonType) {
        CachedRowSet crs;
        crs = DatabaseHandlerImpl.getInstance().executeQuery(this.databases.get(logonType), query);
        return crs;
    }

    public void executeUpdate(String query) {
        DatabaseHandlerImpl.getInstance().executeUpdate(this.databases.get("writer"), query);
    }

    public void executeBatch(List<String> queries) {
        DatabaseHandlerImpl.getInstance().executeBatch(this.databases.get("writer"), queries);
    }

    public void executeScript(String fileName, String logonType) {
        SqlScriptResult dcSQLScriptResult = DatabaseHandlerImpl.getInstance().executeScript(this.databases.get(logonType), fileName);

        if (dcSQLScriptResult.getReturnCode() != 0) {
            throw new RuntimeException("Error executing SQL script");
        }
    }

    public void executeScript(InputStream inputStream, String logonType) {
        SqlScriptResult dcSQLScriptResult = DatabaseHandlerImpl.getInstance().executeScript(this.databases.get(logonType), inputStream);

        if (dcSQLScriptResult.getReturnCode() != 0) {
            throw new RuntimeException("Error executing SQL script");
        }
    }

    public void cleanTable(MetadataTable table) {
        DatabaseHandlerImpl.getInstance().cleanTable(this.databases.get("writer"), table);
    }

    public void dropTable(MetadataTable table) {
        DatabaseHandlerImpl.getInstance().dropTable(this.databases.get("owner"), table);
    }

    public void createTable(MetadataTable table) {
        DatabaseHandlerImpl.getInstance().createTable(this.databases.get("owner"), table);
    }

    public String getCreateStatement(MetadataTable table) {
        return DatabaseHandlerImpl.getInstance().getCreateStatement(this.databases.get("reader"), table);
    }

    public String getDropStatement(MetadataTable table) {
        return DatabaseHandlerImpl.getInstance().getDropStatement(this.databases.get("reader"), table);
    }

    public Map<String, Database> getDatabases() {
        return databases;
    }

    public void shutdown() {
        for (Database database : databases.values()) {
            DatabaseHandlerImpl.getInstance().shutdown(database);
        }
    }
}
