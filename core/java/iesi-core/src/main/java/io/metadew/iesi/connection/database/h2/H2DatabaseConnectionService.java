package io.metadew.iesi.connection.database.h2;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Log4j2
public class H2DatabaseConnectionService extends SchemaDatabaseConnectionService<H2DatabaseConnection> implements ISchemaDatabaseConnectionService<H2DatabaseConnection> {

    private static H2DatabaseConnectionService INSTANCE;

    public synchronized static H2DatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new H2DatabaseConnectionService();
        }
        return INSTANCE;
    }

    private H2DatabaseConnectionService() {}
    
    @Override
    public String getDriver(H2DatabaseConnection databaseConnection) {
        return "org.h2.Driver";
    }

    @Override
    public Class<H2DatabaseConnection> appliesTo() {
        return H2DatabaseConnection.class;
    }

    public Connection getConnection(H2DatabaseConnection h2DatabaseConnection) {
        try {
            Connection connection = super.getConnection(h2DatabaseConnection);
            Optional<String> schema = h2DatabaseConnection.getSchema();
            if (schema.isPresent()) {
                connection.createStatement().execute("SET SCHEMA " + schema.get());
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}