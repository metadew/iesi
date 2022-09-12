package io.metadew.iesi.metadata.repository.coordinator;

import io.metadew.iesi.SpringContext;
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
        return SpringContext.getBean(DatabaseHandler.class).getAllTablesQuery(databases.get("reader"), pattern);
    }

    public CachedRowSet executeQuery(String query, String logonType) {
        CachedRowSet crs;
        crs = SpringContext.getBean(DatabaseHandler.class).executeQuery(this.databases.get(logonType), query);
        return crs;
    }

    public void executeUpdate(String query) {
        SpringContext.getBean(DatabaseHandler.class).executeUpdate(this.databases.get("writer"), query);
    }

    public void executeBatch(List<String> queries) {
        SpringContext.getBean(DatabaseHandler.class).executeBatch(this.databases.get("writer"), queries);
    }

    public void executeScript(String fileName, String logonType) {
        SqlScriptResult dcSQLScriptResult = SpringContext.getBean(DatabaseHandler.class).executeScript(this.databases.get(logonType), fileName);

        if (dcSQLScriptResult.getReturnCode() != 0) {
            throw new RuntimeException("Error executing SQL script");
        }
    }

    public void executeScript(InputStream inputStream, String logonType) {
        SqlScriptResult dcSQLScriptResult = SpringContext.getBean(DatabaseHandler.class).executeScript(this.databases.get(logonType), inputStream);

        if (dcSQLScriptResult.getReturnCode() != 0) {
            throw new RuntimeException("Error executing SQL script");
        }
    }

    public void cleanTable(MetadataTable table) {
        SpringContext.getBean(DatabaseHandler.class).cleanTable(this.databases.get("writer"), table);
    }

    public void dropTable(MetadataTable table) {
        SpringContext.getBean(DatabaseHandler.class).dropTable(this.databases.get("owner"), table);
    }

    public void createTable(MetadataTable table) {
        SpringContext.getBean(DatabaseHandler.class).createTable(this.databases.get("owner"), table);
    }

    public String getCreateStatement(MetadataTable table) {
        return SpringContext.getBean(DatabaseHandler.class).getCreateStatement(this.databases.get("reader"), table);
    }

    public String getDropStatement(MetadataTable table) {
        return SpringContext.getBean(DatabaseHandler.class).getDropStatement(this.databases.get("reader"), table);
    }

    public Map<String, Database> getDatabases() {
        return databases;
    }

    public void shutdown() {
        for (Database database : databases.values()) {
            SpringContext.getBean(DatabaseHandler.class)
                    .shutdown(database);
        }
    }
}
