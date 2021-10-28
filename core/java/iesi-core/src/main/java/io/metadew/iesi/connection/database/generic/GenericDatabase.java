package io.metadew.iesi.connection.database.generic;

import io.metadew.iesi.connection.database.SchemaDatabase;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;

public class GenericDatabase extends SchemaDatabase {

    public GenericDatabase(SchemaDatabaseConnection schemaDatabaseConnection, String schema) {
        super(schemaDatabaseConnection, schema);
    }

    public GenericDatabase(SchemaDatabaseConnection schemaDatabaseConnection) {
        super(schemaDatabaseConnection);
    }
}
