package io.metadew.iesi.connection.database;

import com.zaxxer.hikari.HikariDataSource;
import io.metadew.iesi.common.FrameworkControl;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.bigquery.BigqueryDatabaseService;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.db2.Db2DatabaseService;
import io.metadew.iesi.connection.database.dremio.DremioDatabaseService;
import io.metadew.iesi.connection.database.drill.DrillDatabaseService;
import io.metadew.iesi.connection.database.h2.H2DatabaseService;
import io.metadew.iesi.connection.database.mariadb.MariadbDatabaseService;
import io.metadew.iesi.connection.database.mssql.MssqlDatabaseService;
import io.metadew.iesi.connection.database.mysql.MysqlDatabaseService;
import io.metadew.iesi.connection.database.netezza.NetezzaDatabaseService;
import io.metadew.iesi.connection.database.oracle.OracleDatabaseService;
import io.metadew.iesi.connection.database.postgresql.PostgresqlDatabaseService;
import io.metadew.iesi.connection.database.presto.PrestoDatabaseService;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.database.sqlite.SqliteDatabaseService;
import io.metadew.iesi.connection.database.temporary.TemporaryDatabaseServiceImpl;
import io.metadew.iesi.connection.database.teradata.TeradataDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Log4j2
public class DatabaseHandler implements IDatabaseHandler {

    private Map<ClassStringPair, IDatabaseService> databaseServiceMap;

    private static DatabaseHandler INSTANCE;

    public synchronized static DatabaseHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseHandler();
        }
        return INSTANCE;
    }

    private DatabaseHandler() {
        databaseServiceMap = new HashMap<>();
        databaseServiceMap.put(new ClassStringPair(BigqueryDatabaseService.getInstance().keyword(), BigqueryDatabaseService.getInstance().appliesTo()),
                Db2DatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(Db2DatabaseService.getInstance().keyword(), Db2DatabaseService.getInstance().appliesTo()),
                Db2DatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(DremioDatabaseService.getInstance().keyword(), DremioDatabaseService.getInstance().appliesTo()),
                DremioDatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(DrillDatabaseService.getInstance().keyword(), DrillDatabaseService.getInstance().appliesTo()),
                DrillDatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(H2DatabaseService.getInstance().keyword(), H2DatabaseService.getInstance().appliesTo()),
                H2DatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(MariadbDatabaseService.getInstance().keyword(), MariadbDatabaseService.getInstance().appliesTo()),
                MariadbDatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(MssqlDatabaseService.getInstance().keyword(), MssqlDatabaseService.getInstance().appliesTo()),
                MssqlDatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(MysqlDatabaseService.getInstance().keyword(), MysqlDatabaseService.getInstance().appliesTo()),
                MysqlDatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(NetezzaDatabaseService.getInstance().keyword(), NetezzaDatabaseService.getInstance().appliesTo()),
                NetezzaDatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(OracleDatabaseService.getInstance().keyword(), OracleDatabaseService.getInstance().appliesTo()),
                OracleDatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(PostgresqlDatabaseService.getInstance().keyword(), PostgresqlDatabaseService.getInstance().appliesTo()),
                PostgresqlDatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(PrestoDatabaseService.getInstance().keyword(), PrestoDatabaseService.getInstance().appliesTo()),
                PrestoDatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(SqliteDatabaseService.getInstance().keyword(), SqliteDatabaseService.getInstance().appliesTo()),
                SqliteDatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(TeradataDatabaseService.getInstance().keyword(), TeradataDatabaseService.getInstance().appliesTo()),
                TeradataDatabaseService.getInstance());
        databaseServiceMap.put(new ClassStringPair(TemporaryDatabaseServiceImpl.getInstance().keyword(), TemporaryDatabaseServiceImpl.getInstance().appliesTo()),
                TemporaryDatabaseServiceImpl.getInstance());
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
        log.debug("shutting down " + database.toString());
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

    private IDatabaseService getDatabaseService(Database database) {
        return databaseServiceMap.entrySet().stream()
                .filter(entry -> entry.getKey().clazz.isAssignableFrom(database.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find DatabaseService for " + database.getClass().getSimpleName()));
    }

    private IDatabaseService getDatabaseService(String databaseType) {
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