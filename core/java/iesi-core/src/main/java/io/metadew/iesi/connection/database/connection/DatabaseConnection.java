package io.metadew.iesi.connection.database.connection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;


/**
 * Connection object for databases. This is extended depending on the database
 * type.
 *
 * @author peter.billen
 */
@Log4j2
@Data
@ToString
@EqualsAndHashCode
public abstract class DatabaseConnection {

    private String type;
    private String connectionURL;
    private String userName;
    private String userPassword;
    private String connectionInitSql;

    public DatabaseConnection(String type, String connectionURL, String userName, String userPassword, String connectionInitSql) {
        this.type = type;
        this.connectionURL = connectionURL;
        this.userName = userName;
        this.userPassword = userPassword;
        this.connectionInitSql = connectionInitSql;
    }

}