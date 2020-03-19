package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.netezza.NetezzaDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class NetezzaDatabase extends SchemaDatabase {


    public NetezzaDatabase(NetezzaDatabaseConnection databaseConnection, String schema)  {
        super(databaseConnection, schema);
    }

}
