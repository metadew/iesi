package io.metadew.iesi.connection.database.bigquery;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BigqueryDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "bigquery";

    public BigqueryDatabaseConnection(String connectionURL) {
        super(type, connectionURL, "", "", "");
    }

}
