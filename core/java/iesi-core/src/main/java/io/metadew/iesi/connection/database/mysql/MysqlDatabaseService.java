package io.metadew.iesi.connection.database.mysql;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.definition.connection.Connection;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class MysqlDatabaseService extends SchemaDatabaseService<MysqlDatabase> implements ISchemaDatabaseService<MysqlDatabase> {

    private static final String KEYWORD = "db.mysql";
    private static final String USER_KEY = "user";
    private static final String PASSWORD_KEY = "password";
    private static final String CONNECTION_URL_KEY = "connectionURL";
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String DATABASE_KEY = "database";
    private static MysqlDatabaseService INSTANCE;

    private MysqlDatabaseService() {
    }

    public synchronized static MysqlDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MysqlDatabaseService();
        }
        return INSTANCE;
    }

    @Override
    public MysqlDatabase getDatabase(Connection connection) {
        String userName = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, USER_KEY);
        String userPassword = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, PASSWORD_KEY);
        MysqlDatabaseConnection mysqlDatabaseConnection;
        if (SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, CONNECTION_URL_KEY).isPresent()) {
            mysqlDatabaseConnection = new MysqlDatabaseConnection(
                    SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, CONNECTION_URL_KEY).get(),
                    userName,
                    userPassword,
                    ""
            );
            return new MysqlDatabase(mysqlDatabaseConnection);
        }
        String hostName = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, HOST_KEY);
        int port = Integer.parseInt(SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, PORT_KEY));
        String database = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, DATABASE_KEY);
        mysqlDatabaseConnection = new MysqlDatabaseConnection(
                hostName,
                port,
                database,
                userName,
                userPassword,
                ""
        );
        return new MysqlDatabase(mysqlDatabaseConnection);
    }

    @Override
    public String keyword() {
        return KEYWORD;
    }

    @Override
    public String getSystemTimestampExpression(MysqlDatabase mysqlDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(MysqlDatabase mysqlDatabase, String pattern) {
        return "select table_schema as \"OWNER\", table_name as \"TABLE_NAME\" from information_schema.tables where"
                + mysqlDatabase.getSchema().map(schema -> " table_schema = '" + schema + "' and").orElse("")
                + " table_name like '"
                + pattern
                + "%' order by table_name asc";
    }

    @Override
    public Optional<String> getPrimaryKeyConstraints(MysqlDatabase database, MetadataTable metadataTable) {
        Map<String, MetadataField> primaryKeyMetadataFields = metadataTable.getFields().entrySet().stream()
                .filter(entry -> entry.getValue().isPrimaryKey())
                .collect(Collectors.toMap((entry) -> "\"" + entry.getKey() + "\"", Map.Entry::getValue));
        if (primaryKeyMetadataFields.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of("CONSTRAINT pk_" + metadataTable.getName() + " PRIMARY KEY (" + String.join(", ", primaryKeyMetadataFields.keySet()) + ")");
        }
    }

    @Override
    public Optional<String> getUniqueConstraints(MetadataTable metadataTable) {
        Map<String, MetadataField> primaryKeyMetadataFields = metadataTable.getFields().entrySet().stream()
                .filter(entry -> entry.getValue().isUnique())
                .collect(Collectors.toMap((entry) -> "\"" + entry.getKey() + "\"", Map.Entry::getValue));
        if (primaryKeyMetadataFields.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of("CONSTRAINT uc_" + metadataTable.getName() + " UNIQUE  (" + String.join(", ", primaryKeyMetadataFields.keySet()) + ")");
        }
    }

    @Override
    public String createQueryExtras(MysqlDatabase mysqlDatabase) {
        return "";
    }

    @Override
    public boolean addComments(MysqlDatabase mysqlDatabase) {
        return false;
    }

    @Override
    public String toQueryString(MysqlDatabase mysqlDatabase, MetadataField field) {
        StringBuilder fieldQuery = new StringBuilder();
        // Data Types
        switch (field.getType()) {
            case STRING:
                if (field.getLength() > 255) {
                    fieldQuery.append("VARCHAR (255)");
                    break;
                }
                fieldQuery.append("VARCHAR (").append(field.getLength()).append(")");
                break;
            case CLOB:
                fieldQuery.append("LONGTEXT");
                break;
            case FLAG:
                fieldQuery.append("CHAR (").append(field.getLength()).append(")");
                break;
            case NUMBER:
                fieldQuery.append("NUMERIC");
                break;
            case TIMESTAMP:
                fieldQuery.append("TIMESTAMP");
                break;
        }

        // Default DtTimestamp
        if (field.isDefaultTimestamp()) {
            fieldQuery.append(" DEFAULT CURRENT_TIMESTAMP");
        }

        // Nullable
        if (!field.isNullable()) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public Class<MysqlDatabase> appliesTo() {
        return MysqlDatabase.class;
    }


    public String fieldNameToQueryString(MysqlDatabase database, String fieldName) {
        return "\"" + fieldName + "\"";
    }

}
