package io.metadew.iesi.connection.database.presto;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PrestoDatabaseConnectionService extends SchemaDatabaseConnectionService<PrestoDatabaseConnection> implements ISchemaDatabaseConnectionService<PrestoDatabaseConnection> {

    private static PrestoDatabaseConnectionService INSTANCE;

    public synchronized static PrestoDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrestoDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private PrestoDatabaseConnectionService() {}
    
    @Override
    public String getDriver(PrestoDatabaseConnection databaseConnection) {
        return "io.prestosql.jdbc.PrestoDriver";
    }

    @Override
    public Class<PrestoDatabaseConnection> appliesTo() {
        return PrestoDatabaseConnection.class;
    }

    @Override
    public String refactorLimitAndOffset(PrestoDatabaseConnection databaseConnection, String query) {
        return query;
    }
}