package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;

public class EvalVerifySingleField extends ActionTypeExecution {

    private final static String DATABASE_KEY = "database";
    private final static String SCHEMA_KEY = "schema";
    private final static String TABLE_KEY = "table";
    private final static String FIELD_KEY = "field";
    private final static String CHECK_KEY = "check";
    private final static String OPERATOR_KEY = "operator";
    private final static String VALUE_KEY = "value";
    private final static String CONNECTION_KEY = "connection";

    private static final Logger LOGGER = LogManager.getLogger();

    // Local
    private String sqlSuccess;

    private String sqlError;


    public EvalVerifySingleField(ExecutionControl executionControl,
                                 ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }


    public void prepare() {
    }

    protected boolean executeAction() throws SQLException, InterruptedException {
        String databaseName = convertDatabaseName(getParameterResolvedValue(DATABASE_KEY));
        String schemaName = convertSchemaName(getParameterResolvedValue(SCHEMA_KEY));
        String tableName = convertTableName(getParameterResolvedValue(TABLE_KEY));
        String fieldName = convertFieldName(getParameterResolvedValue(FIELD_KEY));
        String checkName = convertCheckName(getParameterResolvedValue(CHECK_KEY));
        String checkValue = convertCheckValue(getParameterResolvedValue(VALUE_KEY));
        String checkOperatorName = convertCheckOperationName(getParameterResolvedValue(OPERATOR_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_KEY));
        Connection connection = ConnectionConfiguration.getInstance()
                .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .get();

        Database database = DatabaseHandler.getInstance().getDatabase(connection);
        // Run the action
        this.getTestQueries(schemaName, tableName, fieldName, checkName, checkValue);
        long successTotal = 0;
        long errorTotal = 0;
        CachedRowSet crs;

        // Success
        crs = DatabaseHandler.getInstance().executeQuery(database, this.getSqlSuccess());
        while (crs.next()) {
            successTotal = crs.getLong("RES_SUC");
        }
        crs.close();
        this.getActionExecution().getActionControl().logOutput("pass", Long.toString(successTotal));

        // Error
        crs = DatabaseHandler.getInstance().executeQuery(database, this.getSqlError());
        while (crs.next()) {
            errorTotal = crs.getLong("RES_ERR");
        }
        crs.close();
        this.getActionExecution().getActionControl().logOutput("fail", Long.toString(errorTotal));

        // Evaluation
        if (errorTotal == 0) {
            this.getActionExecution().getActionControl().increaseSuccessCount();
            return true;
        } else {
            this.getActionExecution().getActionControl().increaseErrorCount();
            return true;
        }
    }

    @Override
    protected String getKeyword() {
        return "eval.verifySingleField";
    }

    private String convertCheckOperationName(DataType checkOperationName) {
        if (checkOperationName instanceof Text) {
            return checkOperationName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for check operation name",
                    checkOperationName.getClass()));
            return checkOperationName.toString();
        }
    }

    private String convertCheckValue(DataType checkValue) {
        if (checkValue instanceof Text) {
            return checkValue.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for check value",
                    checkValue.getClass()));
            return checkValue.toString();
        }
    }

    private String convertCheckName(DataType checkName) {
        if (checkName instanceof Text) {
            return checkName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for check name",
                    checkName.getClass()));
            return checkName.toString();
        }
    }

    private String convertTableName(DataType tableName) {
        if (tableName instanceof Text) {
            return tableName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for table name",
                    tableName.getClass()));
            return tableName.toString();
        }
    }

    private String convertFieldName(DataType fieldName) {
        if (fieldName instanceof Text) {
            return fieldName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for field name",
                    fieldName.getClass()));
            return fieldName.toString();
        }
    }

    private String convertSchemaName(DataType schemaName) {
        if (schemaName instanceof Text) {
            return schemaName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for schema name",
                    schemaName.getClass()));
            return schemaName.toString();
        }
    }

    private String convertDatabaseName(DataType databaseName) {
        if (databaseName instanceof Text) {
            return databaseName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for database name",
                    databaseName.getClass()));
            return databaseName.toString();
        }
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }


    // Perform verification
    private boolean getTestQueries(String schemaName, String tableName, String fieldName, String checkName, String checkValue) {
        boolean resTestQueries = false;

        resTestQueries = this.defineTestQueries(schemaName, tableName, fieldName, checkName, checkValue);
        return resTestQueries;
    }

    private boolean defineTestQueries(String schemaName, String tableName, String fieldName, String checkName, String checkValue) {

        boolean resTestQueries = true;

        if (checkName.equalsIgnoreCase("default")) {
            // Default
            this.setSqlSuccess("select count(*) \"RES_SUC\" from " + schemaName + "."
                    + tableName + " where " + fieldName + " = '"
                    + checkValue + "'");
            this.setSqlError("select count(*) \"RES_ERR\" from " + schemaName + "."
                    + tableName + " where " + fieldName + " <> '"
                    + checkValue + "'");
        } else if (checkName.equalsIgnoreCase("nn")) {
            // NN Non Nullable
            this.setSqlSuccess("select count(*) as \"RES_SUC\" from " + schemaName + "."
                    + tableName + " where " + fieldName + " is not null");
            this.setSqlError("select count(*) as \"RES_ERR\" from " + schemaName + "."
                    + tableName + " where " + fieldName + " is null");
        } else if (checkName.equalsIgnoreCase("unique")) {
            // Unique
            this.setSqlSuccess("select count(*) as \"RES_SUC\" from (select " + fieldName
                    + " from " + schemaName + "." + tableName + " group by "
                    + fieldName + " having count(*) = 1) is_unique;");
            this.setSqlError("select count(*) as \"RES_ERR\" from (select " + fieldName + " from "
                    + schemaName + "." + tableName + " group by "
                    + fieldName + " having count(*) > 1) not_unique;");
        } else {
            resTestQueries = false;
        }

        return resTestQueries;
    }

    public String getSqlSuccess() {
        return sqlSuccess;
    }

    public void setSqlSuccess(String sqlSuccess) {
        this.sqlSuccess = sqlSuccess;
    }

    public String getSqlError() {
        return sqlError;
    }

    public void setSqlError(String sqlError) {
        this.sqlError = sqlError;
    }

}