package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.dremio.DremioDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

/**
 * Database object for Dremio
 *
 * @author peter.billen
 */
public class DremioDatabase extends SchemaDatabase {

    public DremioDatabase(DremioDatabaseConnection databaseConnection, String schema)  {
        super(databaseConnection, schema);
    }

}
