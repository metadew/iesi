package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.mssql.MssqlDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MssqlDatabase extends SchemaDatabase {

    public MssqlDatabase(MssqlDatabaseConnection databaseConnection, String schema)  {
        super(databaseConnection, schema);
    }

    public MssqlDatabase(MssqlDatabaseConnection databaseConnection)  {
        super(databaseConnection);
    }

}
