package io.metadew.iesi.connection.database.dremio;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DremioDatabaseConnectionService extends SchemaDatabaseConnectionService<DremioDatabaseConnection> implements ISchemaDatabaseConnectionService<DremioDatabaseConnection> {

    private static DremioDatabaseConnectionService INSTANCE;

    public synchronized static DremioDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DremioDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private DremioDatabaseConnectionService() {}

    @Override
    public String getDriver(DremioDatabaseConnection databaseConnection) {
        return "com.dremio.jdbc.Driver";
    }

    @Override
    public Class<DremioDatabaseConnection> appliesTo() {
        return DremioDatabaseConnection.class;
    }
}