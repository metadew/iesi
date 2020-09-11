package io.metadew.iesi.connection.database.bigquery;

import io.metadew.iesi.connection.database.SchemaDatabase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BigqueryDatabase extends SchemaDatabase {

    // The schema concept is mapped to the Bigquery dataset concept
    public BigqueryDatabase(BigqueryDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    public BigqueryDatabase(BigqueryDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

}