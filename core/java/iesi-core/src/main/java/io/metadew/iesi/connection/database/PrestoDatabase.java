package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.presto.PrestoDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

/**
 * Database object for PrestoSQL
 *
 * @author peter.billen
 */
public class PrestoDatabase extends SchemaDatabase {


    public PrestoDatabase(PrestoDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

}
