package io.metadew.iesi.connection.database.connection;

import com.zaxxer.hikari.HikariConfig;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.bigquery.BigqueryDatabaseConnectionService;
import io.metadew.iesi.connection.database.db2.Db2DatabaseConnectionService;
import io.metadew.iesi.connection.database.dremio.DremioDatabaseConnectionService;
import io.metadew.iesi.connection.database.drill.DrillDatabaseConnectionService;
import io.metadew.iesi.connection.database.h2.H2DatabaseConnectionService;
import io.metadew.iesi.connection.database.mariadb.MariadbDatabaseConnectionService;
import io.metadew.iesi.connection.database.mssql.MssqlDatabaseConnectionService;
import io.metadew.iesi.connection.database.mysql.MysqlDatabaseConnectionService;
import io.metadew.iesi.connection.database.netezza.NetezzaDatabaseConnectionService;
import io.metadew.iesi.connection.database.oracle.OracleDatabaseConnectionService;
import io.metadew.iesi.connection.database.postgresql.PostgresqlDatabaseConnectionService;
import io.metadew.iesi.connection.database.presto.PrestoDatabaseConnectionService;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.database.sqlite.SqliteDatabaseConnectionService;
import io.metadew.iesi.connection.database.teradata.TeradataDatabaseConnectionService;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnectionHandler implements IDatabaseConnectionHandler {

    private Map<Class<? extends DatabaseConnection>, IDatabaseConnectionService> databaseConnectionServiceMap;

    private static DatabaseConnectionHandler INSTANCE;

    public synchronized static DatabaseConnectionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseConnectionHandler();
        }
        return INSTANCE;
    }

    private DatabaseConnectionHandler() {
        databaseConnectionServiceMap = new HashMap<>();
        databaseConnectionServiceMap.put(BigqueryDatabaseConnectionService.getInstance().appliesTo(), BigqueryDatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(Db2DatabaseConnectionService.getInstance().appliesTo(), Db2DatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(DremioDatabaseConnectionService.getInstance().appliesTo(), DremioDatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(DrillDatabaseConnectionService.getInstance().appliesTo(), DrillDatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(H2DatabaseConnectionService.getInstance().appliesTo(), H2DatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(MariadbDatabaseConnectionService.getInstance().appliesTo(), MariadbDatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(MssqlDatabaseConnectionService.getInstance().appliesTo(), MssqlDatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(MysqlDatabaseConnectionService.getInstance().appliesTo(), MysqlDatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(NetezzaDatabaseConnectionService.getInstance().appliesTo(), NetezzaDatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(OracleDatabaseConnectionService.getInstance().appliesTo(), OracleDatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(PostgresqlDatabaseConnectionService.getInstance().appliesTo(), PostgresqlDatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(PrestoDatabaseConnectionService.getInstance().appliesTo(), PrestoDatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(SqliteDatabaseConnectionService.getInstance().appliesTo(), SqliteDatabaseConnectionService.getInstance());
        databaseConnectionServiceMap.put(TeradataDatabaseConnectionService.getInstance().appliesTo(), TeradataDatabaseConnectionService.getInstance());
    }

    @SuppressWarnings("unchecked")
    public String getDriver(DatabaseConnection databaseConnection) {
        return getDatabaseConnectionService(databaseConnection).getDriver(databaseConnection);
    }

    @SuppressWarnings("unchecked")
    public Connection getConnection(DatabaseConnection databaseConnection) {
        return getDatabaseConnectionService(databaseConnection).getConnection(databaseConnection);
    }

    @SuppressWarnings("unchecked")
    public String removeIllgegalCharactersForSingleQuery(DatabaseConnection databaseConnection, String input) {
        return getDatabaseConnectionService(databaseConnection).removeIllegalCharactersForSingleQuery(databaseConnection, input);
    }

    @SuppressWarnings("unchecked")
    public CachedRowSet executeQuery(DatabaseConnection databaseConnection, String query) throws SQLException {
        return getDatabaseConnectionService(databaseConnection).executeQuery(databaseConnection, query);
    }

    @SuppressWarnings("unchecked")
    public CachedRowSet executeQuery(DatabaseConnection databaseConnection, String query, Connection connection) throws SQLException {
        return getDatabaseConnectionService(databaseConnection).executeQuery(databaseConnection, query, connection);
    }

    @SuppressWarnings("unchecked")
    public CachedRowSet executeQueryLimitRows(DatabaseConnection databaseConnection, String query, int limit) throws SQLException {
        return getDatabaseConnectionService(databaseConnection).executeQueryLimitRows(databaseConnection, query, limit);
    }

    @SuppressWarnings("unchecked")
    public CachedRowSet executeQueryLimitRows(DatabaseConnection databaseConnection, String query, int limit, Connection connection) throws SQLException {
        return getDatabaseConnectionService(databaseConnection).executeQueryLimitRows(databaseConnection, query, limit, connection);
    }

    @SuppressWarnings("unchecked")
    public CachedRowSet executeProcedure(DatabaseConnection databaseConnection, String sqlProcedure, String sqlParameters) throws SQLException {
        return getDatabaseConnectionService(databaseConnection).executeProcedure(databaseConnection, sqlProcedure, sqlParameters);
    }

    @SuppressWarnings("unchecked")
    public CachedRowSet executeProcedure(DatabaseConnection databaseConnection, String sqlProcedure, String sqlParameters, Connection connection) throws SQLException {
        return getDatabaseConnectionService(databaseConnection).executeProcedure(databaseConnection, sqlProcedure, sqlParameters, connection);
    }

    @SuppressWarnings("unchecked")
    public void executeUpdate(DatabaseConnection databaseConnection, String query) throws SQLException {
        getDatabaseConnectionService(databaseConnection).executeUpdate(databaseConnection, query);
    }

    @SuppressWarnings("unchecked")
    public void executeUpdate(DatabaseConnection databaseConnection, String query, Connection connection) throws SQLException {
        getDatabaseConnectionService(databaseConnection).executeUpdate(databaseConnection, query, connection);
    }

    @SuppressWarnings("unchecked")
    public void executeBatch(DatabaseConnection databaseConnection, List<String> queries) throws SQLException {
        getDatabaseConnectionService(databaseConnection).executeBatch(databaseConnection, queries);
    }

    @SuppressWarnings("unchecked")
    public void executeBatch(DatabaseConnection databaseConnection, List<String> queries, Connection connection) throws SQLException {
        getDatabaseConnectionService(databaseConnection).executeBatch(databaseConnection, queries, connection);
    }

    @SuppressWarnings("unchecked")
    public SqlScriptResult executeScript(DatabaseConnection databaseConnection, String fileName) throws SQLException, IOException {
        return getDatabaseConnectionService(databaseConnection).executeScript(databaseConnection, fileName);
    }

    @SuppressWarnings("unchecked")
    public SqlScriptResult executeScript(DatabaseConnection databaseConnection, String fileName, Connection connection) throws IOException, SQLException {
        return getDatabaseConnectionService(databaseConnection).executeScript(databaseConnection, fileName, connection);
    }

    @SuppressWarnings("unchecked")
    public SqlScriptResult executeScript(DatabaseConnection databaseConnection, InputStream inputStream) throws SQLException, IOException {
        return getDatabaseConnectionService(databaseConnection).executeScript(databaseConnection, inputStream);
    }

    @SuppressWarnings("unchecked")
    public SqlScriptResult executeScript(DatabaseConnection databaseConnection, InputStream inputStream, Connection connection) throws IOException, SQLException {
        return getDatabaseConnectionService(databaseConnection).executeScript(databaseConnection, inputStream, connection);
    }

    @SuppressWarnings("unchecked")
    public HikariConfig configure(DatabaseConnection databaseConnection, HikariConfig hikariConfig) {
        return getDatabaseConnectionService(databaseConnection).configure(databaseConnection, hikariConfig);
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public PreparedStatement createPreparedStatement(DatabaseConnection databaseConnection, Connection connection, String sqlStatement) throws SQLException {
        return getDatabaseConnectionService(databaseConnection).createPreparedStatement(databaseConnection, connection, sqlStatement);
    }

    @Override
    public String generateClobInsertValue(DatabaseConnection databaseConnection, String clobString) {
        return getDatabaseConnectionService(databaseConnection).generateClobInsertValue(clobString);
    }

    private IDatabaseConnectionService getDatabaseConnectionService(DatabaseConnection databaseConnection) {
        return databaseConnectionServiceMap.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(databaseConnection.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find DatabaseConnectionService for " + databaseConnection.getClass().getSimpleName() + "(" + databaseConnection.getType() + ")"));
    }

}