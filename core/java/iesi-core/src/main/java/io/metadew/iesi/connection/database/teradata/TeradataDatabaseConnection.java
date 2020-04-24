package io.metadew.iesi.connection.database.teradata;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class TeradataDatabaseConnection extends DatabaseConnection {

    private static String type = "teradata";

    public TeradataDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword, null);
    }

    public TeradataDatabaseConnection(String hostName, int portNumber, String databaseName, String userName,
                                      String userPassword) {
        this("jdbc:teradata://" + hostName + "/" + "DATABASE=" + databaseName, userName, userPassword);
    }


}
