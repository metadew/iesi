package io.metadew.iesi.connection.database.teradata;

import io.metadew.iesi.connection.database.connection.DatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.IDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TeradataDatabaseConnectionService extends DatabaseConnectionService<TeradataDatabaseConnection> implements IDatabaseConnectionService<TeradataDatabaseConnection> {

    private static TeradataDatabaseConnectionService INSTANCE;

    public synchronized static TeradataDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TeradataDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private TeradataDatabaseConnectionService() {}

    @Override
    public String getDriver(TeradataDatabaseConnection databaseConnection) {
        return "com.teradata.jdbc.TeraDriver";
    }

    @Override
    public Class<TeradataDatabaseConnection> appliesTo() {
        return TeradataDatabaseConnection.class;
    }
}