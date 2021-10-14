package io.metadew.iesi.connection.database.generic;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.DatabaseService;
import io.metadew.iesi.connection.database.IDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

import java.util.Optional;

public class GenericDatabaseService extends DatabaseService<GenericDatabase> implements IDatabaseService<GenericDatabase> {

    private static final String keyword = "db.generic";
    private static final String connectionUrlKey = "connectionURL";
    private static final String userKey = "user";
    private static final String passwordKey = "password";
    private static final String schemaKey = "schema";
    private static GenericDatabaseService instance;

    private GenericDatabaseService() {
    }

    public static synchronized GenericDatabaseService getInstance() {
        if (instance == null) {
            instance = new GenericDatabaseService();
        }
        return instance;
    }

    @Override
    public GenericDatabase getDatabase(Connection connection) {
        Optional<String> username = DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, userKey);
        Optional<String> password = DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, passwordKey);
        Optional<String> schema = DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, schemaKey);
        String connectionURL = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, connectionUrlKey);

        GenericDatabaseConnection genericDatabaseConnection = new GenericDatabaseConnection(
                connectionURL,
                username.orElse(null),
                password.orElse(null),
                null,
                schema.orElse(null)
        );
        return new GenericDatabase(genericDatabaseConnection);
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(GenericDatabase database) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public String getAllTablesQuery(GenericDatabase database, String pattern) {
        return "select table_schema as \"OWNER\", table_name as \"TABLE_NAME\" from information_schema.tables where"
                + database.getSchema().map(schema -> " table_schema = '" + schema + "' and").orElse("")
                + " table_name like '"
                + pattern
                + "%' order by table_name asc";
    }

    @Override
    public String createQueryExtras(GenericDatabase database) {
        return "";
    }

    @Override
    public boolean addComments(GenericDatabase database) {
        return false;
    }

    @Override
    public String toQueryString(GenericDatabase database, MetadataField field) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public Class<GenericDatabase> appliesTo() {
        return GenericDatabase.class;
    }
}
