package io.metadew.iesi.connection.database.drill;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DrillDatabaseConnectionService extends SchemaDatabaseConnectionService<DrillDatabaseConnection> implements ISchemaDatabaseConnectionService<DrillDatabaseConnection> {

    private static DrillDatabaseConnectionService INSTANCE;

    public synchronized static DrillDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DrillDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private DrillDatabaseConnectionService() {}
    
    @Override
    public String getDriver(DrillDatabaseConnection databaseConnection) {
        return "org.apache.drill.jdbc.Driver";
    }

    @Override
    public Class<DrillDatabaseConnection> appliesTo() {
        return DrillDatabaseConnection.class;
    }
}