package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DatabaseHandlerImpl implements DatabaseHandler {

    private Map<Class<? extends Database>, DatabaseService> databaseServiceMap;

    private static DatabaseHandlerImpl INSTANCE;

    public synchronized static DatabaseHandlerImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseHandlerImpl();
        }
        return INSTANCE;
    }

    private DatabaseHandlerImpl() {
        databaseServiceMap = new HashMap<>();
        databaseServiceMap.put(Db2DatabaseServiceImpl.getInstance().appliesTo(), Db2DatabaseServiceImpl.getInstance());
        databaseServiceMap.put(DremioDatabaseServiceImpl.getInstance().appliesTo(), DremioDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(DrillDatabaseServiceImpl.getInstance().appliesTo(), DrillDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(H2DatabaseServiceImpl.getInstance().appliesTo(), H2DatabaseServiceImpl.getInstance());
        databaseServiceMap.put(MariadbDatabaseServiceImpl.getInstance().appliesTo(), MariadbDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(MssqlDatabaseServiceImpl.getInstance().appliesTo(), MssqlDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(MysqlDatabaseServiceImpl.getInstance().appliesTo(), MysqlDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(NetezzaDatabaseServiceImpl.getInstance().appliesTo(), NetezzaDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(OracleDatabaseServiceImpl.getInstance().appliesTo(), OracleDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(PostgresqlDatabaseServiceImpl.getInstance().appliesTo(), PostgresqlDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(PrestoDatabaseServiceImpl.getInstance().appliesTo(), PrestoDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(SqliteDatabaseServiceImpl.getInstance().appliesTo(), SqliteDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(TeradataDatabaseServiceImpl.getInstance().appliesTo(), TeradataDatabaseServiceImpl.getInstance());

    }

    @SuppressWarnings("unchecked")
    public Connection getConnection(Database database) {
        return getDatabaseService(database).getConnection(database);
    }

    @SuppressWarnings("unchecked")
    public boolean releaseConnection(Database database, Connection connection) {
        return getDatabaseService(database).releaseConnection(database, connection);
    }

    @SuppressWarnings("unchecked")
    public void shutdown(Database database) {
        getDatabaseService(database).shutdown(database);
    }

    @SuppressWarnings("unchecked")
    public String getSystemTimestampExpression(Database database) {
        return getDatabaseService(database).getSystemTimestampExpression(database);
    }

    @SuppressWarnings("unchecked")
    public String getAllTablesQuery(Database database, String pattern) {
        return getDatabaseService(database).getAllTablesQuery(database, pattern);
    }

    @SuppressWarnings("unchecked")
    public Connection getLiveConnection(Database database) {
        return getDatabaseService(database).getLiveConnection(database);
    }

    @SuppressWarnings("unchecked")
    public void executeUpdate(Database database, String query) {
        getDatabaseService(database).executeUpdate(database, query);
    }

    @SuppressWarnings("unchecked")
    public CachedRowSet executeQuery(Database database, String query) {
        return getDatabaseService(database).executeQuery(database, query);
    }

    @SuppressWarnings("unchecked")
    public void executeBatch(Database database, List<String> queries) {
        getDatabaseService(database).executeBatch(database, queries);
    }

    @SuppressWarnings("unchecked")
    public CachedRowSet executeQuery(Database database, String query, Connection connection) {
        return getDatabaseService(database).executeQuery(database, query, connection);
    }

    @SuppressWarnings("unchecked")
    public CachedRowSet executeQueryLimitRows(Database database, String query, int limit) {
        return getDatabaseService(database).executeQueryLimitRows(database, query, limit);
    }

    @SuppressWarnings("unchecked")
    public CachedRowSet executeQueryLimitRows(Database database, String query, int limit, Connection connection) {
        return getDatabaseService(database).executeQueryLimitRows(database, query, limit, connection);
    }

    @SuppressWarnings("unchecked")
    public SqlScriptResult executeScript(Database database, String filename) {
        return getDatabaseService(database).executeScript(database, filename);
    }

    @SuppressWarnings("unchecked")
    public SqlScriptResult executeScript(Database database, String filename, Connection connection) {
        return getDatabaseService(database).executeScript(database, filename, connection);
    }

    @SuppressWarnings("unchecked")
    public SqlScriptResult executeScript(Database database, InputStream inputStream) {
        return getDatabaseService(database).executeScript(database, inputStream);
    }

    @SuppressWarnings("unchecked")
    public SqlScriptResult executeScript(Database database, InputStream inputStream, Connection connection) {
        return getDatabaseService(database).executeScript(database, inputStream, connection);
    }

    @SuppressWarnings("unchecked")
    public CachedRowSet executeProcedure(Database database, String sqlProcedure, String sqlParameters) {
        return getDatabaseService(database).executeProcedure(database, sqlProcedure, sqlParameters);
    }

    @SuppressWarnings("unchecked")
    public String getCreateStatement(Database database, MetadataTable table, String tableNamePrefix) {
        return getDatabaseService(database).getCreateStatement(database, table, tableNamePrefix);
    }

    @SuppressWarnings("unchecked")
    public String getDeleteStatement(Database database, MetadataTable table) {
        return getDatabaseService(database).getDeleteStatement(database, table);
    }

    @SuppressWarnings("unchecked")
    public String getDropStatement(Database database, MetadataTable table) {
        return getDatabaseService(database).getDropStatement(database, table);
    }

    @SuppressWarnings("unchecked")
    public String getCreateStatement(Database database, MetadataTable table) {
        return getDatabaseService(database).getCreateStatement(database, table);
    }

    @SuppressWarnings("unchecked")
    public Optional<String> getPrimaryKeyConstraints(Database database, MetadataTable metadataTable) {
        return getDatabaseService(database).getPrimaryKeyConstraints(database, metadataTable);
    }

    @SuppressWarnings("unchecked")
    public void dropTable(Database database, MetadataTable table) {
        getDatabaseService(database).dropTable(database, table);
    }

    @SuppressWarnings("unchecked")
    public void cleanTable(Database database, MetadataTable table) {
        getDatabaseService(database).cleanTable(database, table);
    }

    @SuppressWarnings("unchecked")
    public void createTable(Database database, MetadataTable table) {
        getDatabaseService(database).createTable(database, table);
    }

    @SuppressWarnings("unchecked")
    public String createQueryExtras(Database database) {
        return getDatabaseService(database).createQueryExtras(database);
    }

    @SuppressWarnings("unchecked")
    public boolean addComments(Database database) {
        return getDatabaseService(database).addComments(database);
    }

    @SuppressWarnings("unchecked")
    public String toQueryString(Database database, MetadataField field) {
        return getDatabaseService(database).toQueryString(database, field);
    }

    private DatabaseService getDatabaseService(Database database) {
        return databaseServiceMap.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(database.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find DatabaseService for " + database.getClass().getSimpleName()));
    }

}