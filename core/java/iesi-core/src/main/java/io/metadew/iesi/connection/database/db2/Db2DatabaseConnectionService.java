package io.metadew.iesi.connection.database.db2;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Db2DatabaseConnectionService extends SchemaDatabaseConnectionService<Db2DatabaseConnection> implements ISchemaDatabaseConnectionService<Db2DatabaseConnection> {

    private static Db2DatabaseConnectionService INSTANCE;

    public synchronized static Db2DatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Db2DatabaseConnectionService();
        }
        return INSTANCE;
    }

    private Db2DatabaseConnectionService() {
    }

    @Override
    public String getDriver(Db2DatabaseConnection databaseConnection) {
        return "com.ibm.db2.jcc.DB2Driver";
    }

    @Override
    public Class<Db2DatabaseConnection> appliesTo() {
        return Db2DatabaseConnection.class;
    }

    @Override
    public String refactorLimitAndOffset(Db2DatabaseConnection databaseConnection, String query) {
        return query;
    }
}