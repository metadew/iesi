package io.metadew.iesi.connection.database.connection;

import com.zaxxer.hikari.HikariConfig;
import io.metadew.iesi.connection.database.connection.db2.Db2DatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.dremio.DremioDatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.drill.DrillDatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.h2.H2DatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.mariadb.MariadbDatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.mssql.MssqlDatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.mysql.MysqlDatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.netezza.NetezzaDatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.oracle.OracleDatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.postgresql.PostgresqlDatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.presto.PrestoDatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.connection.teradata.TeradataDatabaseConnectionServiceImpl;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnectionHandlerImpl implements DatabaseConnectionHandler {

    private Map<Class<? extends DatabaseConnection>, DatabaseConnectionService> databaseConnectionServiceMap;

    private static DatabaseConnectionHandlerImpl INSTANCE;

    public synchronized static DatabaseConnectionHandlerImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseConnectionHandlerImpl();
        }
        return INSTANCE;
    }

    private DatabaseConnectionHandlerImpl() {
        databaseConnectionServiceMap = new HashMap<>();
        databaseConnectionServiceMap.put(Db2DatabaseConnectionServiceImpl.getInstance().appliesTo(), Db2DatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(DremioDatabaseConnectionServiceImpl.getInstance().appliesTo(), DremioDatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(DrillDatabaseConnectionServiceImpl.getInstance().appliesTo(), DrillDatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(H2DatabaseConnectionServiceImpl.getInstance().appliesTo(), H2DatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(MariadbDatabaseConnectionServiceImpl.getInstance().appliesTo(), MariadbDatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(MssqlDatabaseConnectionServiceImpl.getInstance().appliesTo(), MssqlDatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(MysqlDatabaseConnectionServiceImpl.getInstance().appliesTo(), MysqlDatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(NetezzaDatabaseConnectionServiceImpl.getInstance().appliesTo(), NetezzaDatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(OracleDatabaseConnectionServiceImpl.getInstance().appliesTo(), OracleDatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(PostgresqlDatabaseConnectionServiceImpl.getInstance().appliesTo(), PostgresqlDatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(PrestoDatabaseConnectionServiceImpl.getInstance().appliesTo(), PrestoDatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(SqliteDatabaseConnectionServiceImpl.getInstance().appliesTo(), SqliteDatabaseConnectionServiceImpl.getInstance());
        databaseConnectionServiceMap.put(TeradataDatabaseConnectionServiceImpl.getInstance().appliesTo(), TeradataDatabaseConnectionServiceImpl.getInstance());
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
        return getDatabaseConnectionService(databaseConnection).removeIllgegalCharactersForSingleQuery(databaseConnection, input);
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

    private DatabaseConnectionService getDatabaseConnectionService(DatabaseConnection databaseConnection) {
        return databaseConnectionServiceMap.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(databaseConnection.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find DatabaseConnectionService for " + databaseConnection.getClass().getSimpleName() + "(" + databaseConnection.getType() + ")"));
    }

}