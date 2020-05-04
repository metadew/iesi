package io.metadew.iesi.connection.database.drill;

import io.metadew.iesi.connection.database.SchemaDatabase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DrillDatabase extends SchemaDatabase {

    public DrillDatabase(DrillDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

}
