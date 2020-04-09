package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.drill.DrillDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
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
