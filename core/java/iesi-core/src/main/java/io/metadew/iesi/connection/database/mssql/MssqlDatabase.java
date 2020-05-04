package io.metadew.iesi.connection.database.mssql;

import io.metadew.iesi.connection.database.SchemaDatabase;
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
