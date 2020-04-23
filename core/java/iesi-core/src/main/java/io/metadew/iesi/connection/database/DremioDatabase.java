package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.dremio.DremioDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DremioDatabase extends SchemaDatabase {

    public DremioDatabase(DremioDatabaseConnection databaseConnection, String schema)  {
        super(databaseConnection, schema);
    }

}
