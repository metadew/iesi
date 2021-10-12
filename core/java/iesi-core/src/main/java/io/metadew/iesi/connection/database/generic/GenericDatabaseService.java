package io.metadew.iesi.connection.database.generic;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.DatabaseService;
import io.metadew.iesi.connection.database.IDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

import java.util.Optional;

public class GenericDatabaseService extends DatabaseService<GenericDatabase> implements IDatabaseService<GenericDatabase> {

    private static GenericDatabaseService instance;

    private static final String keyword = "db.generic";
    private static final String connectionUrlKey = "connectionURL";
    private static final String userKey = "user";
    private static final String passwordKey = "password";

    public static synchronized GenericDatabaseService getInstance() {
        if (instance == null) {
            instance = new GenericDatabaseService();
        }
        return instance;
    }

    private GenericDatabaseService() {}

    @Override
    public GenericDatabase getDatabase(Connection connection) {
        Optional<String> username = DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, userKey);
        Optional<String> password = DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, passwordKey);
        String connectionURL = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, connectionUrlKey);

        GenericDatabaseConnection genericDatabaseConnection = new GenericDatabaseConnection(
                connectionURL,
                username.orElse(null),
                password.orElse(null),
                null
        );
        return new GenericDatabase(genericDatabaseConnection);
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(GenericDatabase database) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(GenericDatabase database, String pattern) {
        return null;
    }

    @Override
    public String createQueryExtras(GenericDatabase database) {
        return null;
    }

    @Override
    public boolean addComments(GenericDatabase database) {
        return false;
    }

    @Override
    public String toQueryString(GenericDatabase database, MetadataField field) {
        return null;
    }

    @Override
    public Class<GenericDatabase> appliesTo() {
        return GenericDatabase.class;
    }
}
