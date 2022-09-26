package io.metadew.iesi.connection.database.generic;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.DatabaseService;
import io.metadew.iesi.connection.database.IDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

import java.util.Optional;

public class GenericDatabaseService extends DatabaseService<GenericDatabase> implements IDatabaseService<GenericDatabase> {

    private static final String KEYWORD = "db.generic";
    private static final String CONNECTION_URL_KEY = "connectionURL";
    private static final String USER_KEY = "user";
    private static final String PASSWORD_KEY = "password";
    private static final String SCHEMA_KEY = "schema";
    private static final String INIT_SQL = "initSql";
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
        Optional<String> username = SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, USER_KEY);
        Optional<String> password = SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, PASSWORD_KEY);
        Optional<String> schema = SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, SCHEMA_KEY);
        String connectionURL = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, CONNECTION_URL_KEY);
        Optional<String> initSql = SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, INIT_SQL);

        GenericDatabaseConnection genericDatabaseConnection = new GenericDatabaseConnection(
                connectionURL,
                username.orElse(null),
                password.orElse(null),
                initSql.orElse(null),
                schema.orElse(null)
        );
        return new GenericDatabase(genericDatabaseConnection);
    }

    @Override
    public String keyword() {
        return KEYWORD;
    }

    @Override
    public String getSystemTimestampExpression(GenericDatabase database) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public String getAllTablesQuery(GenericDatabase database, String pattern) {
        throw new UnsupportedOperationException("Unsupported Operation");
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
