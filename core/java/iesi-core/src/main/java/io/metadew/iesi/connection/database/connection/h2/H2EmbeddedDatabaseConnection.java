package io.metadew.iesi.connection.database.connection.h2;

public class H2EmbeddedDatabaseConnection extends H2DatabaseConnection {

    public H2EmbeddedDatabaseConnection(String filePath, String userName, String userPassword) {
        super("jdbc:h2:file:"+filePath, userName, userPassword);
    }
}
