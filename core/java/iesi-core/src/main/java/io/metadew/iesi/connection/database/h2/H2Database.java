package io.metadew.iesi.connection.database.h2;

import io.metadew.iesi.connection.database.SchemaDatabase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class H2Database extends SchemaDatabase {

    public H2Database(H2DatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    public H2Database(H2DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

}
