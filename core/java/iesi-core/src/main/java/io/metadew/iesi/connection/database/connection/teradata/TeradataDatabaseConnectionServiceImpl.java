package io.metadew.iesi.connection.database.connection.teradata;

import io.metadew.iesi.connection.database.connection.DatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TeradataDatabaseConnectionServiceImpl extends DatabaseConnectionServiceImpl<TeradataDatabaseConnection> implements DatabaseConnectionService<TeradataDatabaseConnection> {

    private static TeradataDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static TeradataDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TeradataDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private TeradataDatabaseConnectionServiceImpl() {}

    @Override
    public String getDriver(TeradataDatabaseConnection databaseConnection) {
        return "com.teradata.jdbc.TeraDriver";
    }

    @Override
    public Class<TeradataDatabaseConnection> appliesTo() {
        return TeradataDatabaseConnection.class;
    }
}