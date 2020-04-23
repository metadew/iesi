package io.metadew.iesi.connection.database.dremio;

import io.metadew.iesi.connection.database.SchemaDatabase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DremioDatabase extends SchemaDatabase {

    public DremioDatabase(DremioDatabaseConnection databaseConnection, String schema)  {
        super(databaseConnection, schema);
    }

}
