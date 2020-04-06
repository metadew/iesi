package io.metadew.iesi.connection.database;

import com.zaxxer.hikari.HikariDataSource;
import io.metadew.iesi.common.FrameworkControl;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import lombok.RequiredArgsConstructor;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DatabaseHandlerImpl implements DatabaseHandler {

    private Map<ClassStringPair, DatabaseService> databaseServiceMap;

    private static DatabaseHandlerImpl INSTANCE;

    public synchronized static DatabaseHandlerImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseHandlerImpl();
        }
        return INSTANCE;
    }

    private DatabaseHandlerImpl() {
        databaseServiceMap = new HashMap<>();
        databaseServiceMap.put(new ClassStringPair(Db2DatabaseServiceImpl.getInstance().keyword(), Db2DatabaseServiceImpl.getInstance().appliesTo()),
                Db2DatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(DremioDatabaseServiceImpl.getInstance().keyword(), DremioDatabaseServiceImpl.getInstance().appliesTo()),
                DremioDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(DrillDatabaseServiceImpl.getInstance().keyword(), DrillDatabaseServiceImpl.getInstance().appliesTo()),
                DrillDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(H2DatabaseServiceImpl.getInstance().keyword(), H2DatabaseServiceImpl.getInstance().appliesTo()),
                H2DatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(MariadbDatabaseServiceImpl.getInstance().keyword(), MariadbDatabaseServiceImpl.getInstance().appliesTo()),
                MariadbDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(MssqlDatabaseServiceImpl.getInstance().keyword(), MssqlDatabaseServiceImpl.getInstance().appliesTo()),
                MssqlDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(MysqlDatabaseServiceImpl.getInstance().keyword(), MysqlDatabaseServiceImpl.getInstance().appliesTo()),
                MysqlDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(NetezzaDatabaseServiceImpl.getInstance().keyword(), NetezzaDatabaseServiceImpl.getInstance().appliesTo()),
                NetezzaDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(OracleDatabaseServiceImpl.getInstance().keyword(), OracleDatabaseServiceImpl.getInstance().appliesTo()),
                OracleDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(PostgresqlDatabaseServiceImpl.getInstance().keyword(), PostgresqlDatabaseServiceImpl.getInstance().appliesTo()),
                PostgresqlDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(PrestoDatabaseServiceImpl.getInstance().keyword(), PrestoDatabaseServiceImpl.getInstance().appliesTo()),
                PrestoDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(SqliteDatabaseServiceImpl.getInstance().keyword(), SqliteDatabaseServiceImpl.getInstance().appliesTo()),
                SqliteDatabaseServiceImpl.getInstance());
        databaseServiceMap.put(new ClassStringPair(TeradataDatabaseServiceImpl.getInstance().keyword(), TeradataDatabaseServiceImpl.getInstance().appliesTo()),
                TeradataDatabaseServiceImpl.getInstance());
    }

    @Override
    public Database getDatabase(io.metadew.iesi.metadata.definition.connection.Connection connection) {
        return getDatabaseService(connection.getType()).getDatabase(connection);
    }

    @SuppressWarnings("unchecked")
    public Connection getConnection(Database database) throws SQLException {
        return getDatabaseService(database).getConnection(database);
    }

    @SuppressWarnings("unchecked")
    public void releaseConnection(Database database, Connection connection) {
        getDatabaseService(database).releaseConnection(database, connection);
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

    @Override
    public boolean isInitializeConnectionPool(Database database) {
        return getDatabaseService(database).isInitializeConnectionPool();
    }

    @Override
    @SuppressWarnings("unchecked")
    public HikariDataSource createConnectionPool(Database database, DatabaseConnection databaseConnection) {
        return getDatabaseService(database).createConnectionPool(database, databaseConnection);
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

    public String getMandatoryParameterWithKey(io.metadew.iesi.metadata.definition.connection.Connection connection, String key) {
        return connection.getParameters().stream()
                .filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase(key))
                .findFirst()
                .map(connectionParameter -> FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()))
                .map(connectionParameterValue -> FrameworkCrypto.getInstance().decryptIfNeeded(connectionParameterValue))
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Connection {0} does not contain mandatory parameter ''{1}''", connection, key)));

    }

    public Optional<String> getOptionalParameterWithKey(io.metadew.iesi.metadata.definition.connection.Connection connection, String key) {
        return connection.getParameters().stream()
                .filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase(key))
                .findFirst()
                .map(connectionParameter -> FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()))
                .map(connectionParameterValue -> FrameworkCrypto.getInstance().decryptIfNeeded(connectionParameterValue));
    }

    private DatabaseService getDatabaseService(Database database) {
        return databaseServiceMap.entrySet().stream()
                .filter(entry -> entry.getKey().clazz.isAssignableFrom(database.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find DatabaseService for " + database.getClass().getSimpleName()));
    }

    private DatabaseService getDatabaseService(String databaseType) {
        return databaseServiceMap.entrySet().stream()
                .filter(entry -> entry.getKey().keyword.equals(databaseType))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find DatabaseService for " + databaseType));
    }

    @RequiredArgsConstructor
    private static class ClassStringPair {

        private final String keyword;
        private final Class<? extends Database> clazz;

    }

}