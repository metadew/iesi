package io.metadew.iesi.connection.database.presto;

import io.metadew.iesi.connection.database.SchemaDatabase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PrestoDatabase extends SchemaDatabase {


    public PrestoDatabase(PrestoDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }
}
