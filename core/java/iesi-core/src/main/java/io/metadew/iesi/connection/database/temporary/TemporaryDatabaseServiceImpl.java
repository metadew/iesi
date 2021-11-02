package io.metadew.iesi.connection.database.temporary;

import com.zaxxer.hikari.HikariDataSource;
import io.metadew.iesi.connection.database.IDatabaseService;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.definition.connection.Connection;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TemporaryDatabaseServiceImpl implements IDatabaseService<TemporaryDatabase> {

    private static TemporaryDatabaseServiceImpl INSTANCE;

    private final static String keyword = "db.temporary";


    public synchronized static TemporaryDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemporaryDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private TemporaryDatabaseServiceImpl() {
    }

    @Override
    public TemporaryDatabase getDatabase(Connection connection) {
        return new TemporaryDatabase(new TemporaryDatabaseConnection());
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public java.sql.Connection getConnection(TemporaryDatabase database) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public HikariDataSource createConnectionPool(TemporaryDatabase database, DatabaseConnection databaseConnection) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public void releaseConnection(TemporaryDatabase database, java.sql.Connection connection) {}

    @Override
    public void shutdown(TemporaryDatabase database) {}

    @Override
    public boolean isInitializeConnectionPool() {
        return false;
    }

    @Override
    public String getSystemTimestampExpression(TemporaryDatabase teradataDatabase) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public String getAllTablesQuery(TemporaryDatabase teradataDatabase, String pattern) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public java.sql.Connection getLiveConnection(TemporaryDatabase database) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public void executeUpdate(TemporaryDatabase database, String query) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public CachedRowSet executeQuery(TemporaryDatabase database, String query) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public void executeBatch(TemporaryDatabase database, List<String> queries) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public CachedRowSet executeQuery(TemporaryDatabase database, String query, java.sql.Connection connection) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public CachedRowSet executeQueryLimitRows(TemporaryDatabase database, String query, int limit) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public CachedRowSet executeQueryLimitRows(TemporaryDatabase database, String query, int limit, java.sql.Connection connection) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public SqlScriptResult executeScript(TemporaryDatabase database, String filename) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public SqlScriptResult executeScript(TemporaryDatabase database, String filename, java.sql.Connection connection) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public SqlScriptResult executeScript(TemporaryDatabase database, InputStream inputStream) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public SqlScriptResult executeScript(TemporaryDatabase database, InputStream inputStream, java.sql.Connection connection) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public CachedRowSet executeProcedure(TemporaryDatabase database, String sqlProcedure, String sqlParameters) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public String getCreateStatement(TemporaryDatabase database, MetadataTable table, String tableNamePrefix) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public String getDeleteStatement(TemporaryDatabase database, MetadataTable table) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public String getDropStatement(TemporaryDatabase database, MetadataTable table) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public String getCreateStatement(TemporaryDatabase database, MetadataTable table) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public Optional<String> getPrimaryKeyConstraints(TemporaryDatabase database, MetadataTable metadataTable) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public void dropTable(TemporaryDatabase database, MetadataTable table) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public void cleanTable(TemporaryDatabase database, MetadataTable table) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public void createTable(TemporaryDatabase database, MetadataTable table) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public String createQueryExtras(TemporaryDatabase teradataDatabase) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public boolean addComments(TemporaryDatabase teradataDatabase) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public String toQueryString(TemporaryDatabase teradataDatabase, MetadataField field) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public String fieldNameToQueryString(TemporaryDatabase database, String fieldName) {
        return fieldName;
    }

    @Override
    public Class<TemporaryDatabase> appliesTo() {
        return TemporaryDatabase.class;
    }

}
