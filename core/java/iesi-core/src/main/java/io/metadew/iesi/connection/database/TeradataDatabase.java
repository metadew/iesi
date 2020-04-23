package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.teradata.TeradataDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TeradataDatabase extends Database {


    public TeradataDatabase(TeradataDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

}
