package io.metadew.iesi.connection.database.teradata;

import io.metadew.iesi.connection.database.Database;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TeradataDatabase extends Database {


    public TeradataDatabase(TeradataDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

}
