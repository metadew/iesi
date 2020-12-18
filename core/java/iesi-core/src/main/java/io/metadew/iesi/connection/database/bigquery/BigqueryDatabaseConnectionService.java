package io.metadew.iesi.connection.database.bigquery;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Log4j2
public class BigqueryDatabaseConnectionService extends SchemaDatabaseConnectionService<BigqueryDatabaseConnection> implements ISchemaDatabaseConnectionService<BigqueryDatabaseConnection> {

    private static BigqueryDatabaseConnectionService INSTANCE;

    public synchronized static BigqueryDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BigqueryDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private BigqueryDatabaseConnectionService() {}

    @Override
    public Connection getConnection(BigqueryDatabaseConnection bigqueryDatabaseConnection) {
        try {
            Class.forName(getDriver(bigqueryDatabaseConnection));
            Connection connection;
            connection = DriverManager.getConnection(bigqueryDatabaseConnection.getConnectionURL());
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDriver(BigqueryDatabaseConnection databaseConnection) {
        //The version of the driver needs to be modified here
        return "com.simba.googlebigquery.jdbc42.Driver";
    }

    @Override
    public Class<BigqueryDatabaseConnection> appliesTo() {
        return BigqueryDatabaseConnection.class;
    }
}