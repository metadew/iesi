package io.metadew.iesi.connection.database.connection.h2;

public class H2MemoryDatabaseConnection extends H2DatabaseConnection {

    public H2MemoryDatabaseConnection(String databaseName, String userName, String userPassword) {
        super("jdbc:h2:mem:" + databaseName, userName, userPassword);
    }
}
