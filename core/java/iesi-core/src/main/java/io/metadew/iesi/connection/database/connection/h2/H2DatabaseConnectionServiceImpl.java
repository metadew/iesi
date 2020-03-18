package io.metadew.iesi.connection.database.connection.h2;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Log4j2
public class H2DatabaseConnectionServiceImpl extends SchemaDatabaseConnectionServiceImpl<H2DatabaseConnection> implements SchemaDatabaseConnectionService<H2DatabaseConnection> {

    private static H2DatabaseConnectionServiceImpl INSTANCE;

    public synchronized static H2DatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new H2DatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private H2DatabaseConnectionServiceImpl() {}
    
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