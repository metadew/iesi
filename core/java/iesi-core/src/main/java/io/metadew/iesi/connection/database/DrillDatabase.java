package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.drill.DrillDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

/**
 * Database object for Apache Drill
 * 
 * @author peter.billen
 *
 */
public class DrillDatabase extends SchemaDatabase {

    public DrillDatabase(DrillDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

}
