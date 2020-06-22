package io.metadew.iesi.connection.database.oracle;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Log4j2
public class OracleDatabaseConnectionService extends SchemaDatabaseConnectionService<OracleDatabaseConnection> implements ISchemaDatabaseConnectionService<OracleDatabaseConnection> {

    private static OracleDatabaseConnectionService INSTANCE;

    public synchronized static OracleDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OracleDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private OracleDatabaseConnectionService() {
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