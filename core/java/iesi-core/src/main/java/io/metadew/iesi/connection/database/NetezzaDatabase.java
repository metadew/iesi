package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.netezza.NetezzaDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NetezzaDatabase extends SchemaDatabase {


    public NetezzaDatabase(NetezzaDatabaseConnection databaseConnection, String schema)  {
        super(databaseConnection, schema);
    }

    public NetezzaDatabase(NetezzaDatabaseConnection databaseConnection)  {
        super(databaseConnection);
    }

}
