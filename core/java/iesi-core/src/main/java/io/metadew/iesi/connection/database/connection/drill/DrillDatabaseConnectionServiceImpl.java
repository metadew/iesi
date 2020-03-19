package io.metadew.iesi.connection.database.connection.drill;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DrillDatabaseConnectionServiceImpl extends SchemaDatabaseConnectionServiceImpl<DrillDatabaseConnection> implements SchemaDatabaseConnectionService<DrillDatabaseConnection> {

    private static DrillDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static DrillDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DrillDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private DrillDatabaseConnectionServiceImpl() {}
    
    @Override
    public String getDriver(DrillDatabaseConnection databaseConnection) {
        return "org.apache.drill.jdbc.Driver";
    }

    @Override
    public Class<DrillDatabaseConnection> appliesTo() {
        return DrillDatabaseConnection.class;
    }
}