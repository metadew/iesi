package io.metadew.iesi.connection.database.db2;

import io.metadew.iesi.connection.database.SchemaDatabase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Db2Database extends SchemaDatabase {

    public Db2Database(Db2DatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

}
