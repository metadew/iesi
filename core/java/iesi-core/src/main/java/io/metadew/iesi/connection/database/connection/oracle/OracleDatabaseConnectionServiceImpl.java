package io.metadew.iesi.connection.database.connection.oracle;

import com.zaxxer.hikari.HikariConfig;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Log4j2
public class OracleDatabaseConnectionServiceImpl extends SchemaDatabaseConnectionServiceImpl<OracleDatabaseConnection> implements SchemaDatabaseConnectionService<OracleDatabaseConnection> {

    private static OracleDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static OracleDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OracleDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private OracleDatabaseConnectionServiceImpl() {
    }

    public HikariConfig configure(OracleDatabaseConnection oracleDatabaseConnection, HikariConfig hikariConfig) {
        hikariConfig.setJdbcUrl(oracleDatabaseConnection.getConnectionURL());
        hikariConfig.setUsername(oracleDatabaseConnection.getUserName());
        hikariConfig.setPassword(oracleDatabaseConnection.getUserPassword());
        hikariConfig.setAutoCommit(false);
        hikariConfig.setConnectionInitSql(oracleDatabaseConnection.getSchema().map(s -> "alter session set nls_timestamp_format='YYYY-MM-DD\"T\" HH24:MI:SS:FF' current_schema=" + s)
                .orElse("alter session set nls_timestamp_format='YYYY-MM-DD\"T\" HH24:MI:SS:FF'"));
        return hikariConfig;
    }

    @Override
    public String getDriver(OracleDatabaseConnection databaseConnection) {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public Class<OracleDatabaseConnection> appliesTo() {
        return OracleDatabaseConnection.class;
    }


    public Connection getConnection(OracleDatabaseConnection oracleDatabaseConnection) {
        try {
            Connection connection = super.getConnection(oracleDatabaseConnection);

            Optional<String> schema = oracleDatabaseConnection.getSchema();
            if (schema.isPresent()) {
                // TODO: The old JDBC API does not support the setSchema call
                connection.createStatement().execute("alter session set current_schema=" + schema.get());
                // connection.setSchema(schema.get());
            }
            connection.createStatement().execute("alter session set nls_timestamp_format='YYYY-MM-DD\"T\" HH24:MI:SS:FF'");
            return connection;
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            throw new RuntimeException(e);
        }
    }
}