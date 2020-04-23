package io.metadew.iesi.connection.database.connection.dremio;

import io.metadew.iesi.connection.database.connection.DatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DremioDatabaseConnectionServiceImpl extends SchemaDatabaseConnectionServiceImpl<DremioDatabaseConnection> implements SchemaDatabaseConnectionService<DremioDatabaseConnection> {

    private static DremioDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static DremioDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DremioDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private DremioDatabaseConnectionServiceImpl() {}

    @Override
    public String getDriver(DremioDatabaseConnection databaseConnection) {
        return "com.dremio.jdbc.Driver";
    }

    @Override
    public Class<DremioDatabaseConnection> appliesTo() {
        return DremioDatabaseConnection.class;
    }
}