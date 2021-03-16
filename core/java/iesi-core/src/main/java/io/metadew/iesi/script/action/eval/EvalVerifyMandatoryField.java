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


public class EvalVerifyMandatoryField extends ActionTypeExecution {

        private static final String DATABASE_KEY = "database";
        private static final String SCHEMA_KEY = "schema";
        private static final String TABLE_KEY = "table";
        private static final String FIELD_KEY = "field";
        private static final String EVALUATION_FIELD_KEY = "evaluationField";
        private static final String EVALUATION_VALUE_KEY = "evaluationValue";
        private static final String IS_MANDATORY_KEY = "isMandatory";
        private static final String CONNECTION_KEY = "connection";

    // Local
    private String sqlSuccess;

    private String sqlError;

    private static final Logger LOGGER = LogManager.getLogger();

    public EvalVerifyMandatoryField(ExecutionControl executionControl,
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
        String evaluationFieldName = convertEvaluationFieldName(getParameterResolvedValue(EVALUATION_FIELD_KEY));
        String evaluationFieldValue = convertEvaluationFieldValue(getParameterResolvedValue(EVALUATION_VALUE_KEY));
        boolean isMandatory = convertIsMandatory(getParameterResolvedValue(IS_MANDATORY_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_KEY));
        // Get Connection
        Connection connection = ConnectionConfiguration.getInstance().get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName())).get();
        Database database = DatabaseHandler.getInstance().getDatabase(connection);
        // Run the action
        this.getTestQueries(schemaName, tableName, fieldName, evaluationFieldName, evaluationFieldValue, isMandatory);
        long successTotal = 0;
        long errorTotal = 0;
        CachedRowSet cachedRowSet;

        // Success
        cachedRowSet = DatabaseHandler.getInstance().executeQuery(database, this.getSqlSuccess());
        while (cachedRowSet.next()) {
            successTotal = cachedRowSet.getLong("RES_SUC");
        }
        cachedRowSet.close();
        this.getActionExecution().getActionControl().logOutput("pass", Long.toString(successTotal));

        // Error
        cachedRowSet = DatabaseHandler.getInstance().executeQuery(database, this.getSqlError());
        while (cachedRowSet.next()) {
            errorTotal = cachedRowSet.getLong("RES_ERR");
        }
        cachedRowSet.close();
        this.getActionExecution().getActionControl().logOutput("fail", Long.toString(errorTotal));

        // Evaluation
        if (errorTotal == 0) {
            this.getActionExecution().getActionControl().increaseSuccessCount();
            return true;
        } else {
            this.getActionExecution().getActionControl().increaseErrorCount();
            return false;
        }
    }

    @Override
    protected String getKeyword() {
        return "eval.verifyMandatoryField";
    }

    private boolean convertIsMandatory(DataType isMandatory) {
        if (isMandatory instanceof Text) {
            return isMandatory.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for mandatory flag",
                    isMandatory.getClass()));
            return false;
        }
    }

    private String convertEvaluationFieldValue(DataType evaluationFieldValue) {
        if (evaluationFieldValue instanceof Text) {
            return evaluationFieldValue.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for evaluation field value",
                    evaluationFieldValue.getClass()));
            return evaluationFieldValue.toString();
        }
    }

    private String convertEvaluationFieldName(DataType evaluatonFieldName) {
        if (evaluatonFieldName instanceof Text) {
            return evaluatonFieldName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for evaluation field name",
                    evaluatonFieldName.getClass()));
            return evaluatonFieldName.toString();
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
    private boolean getTestQueries(String schemaName, String tableName, String fieldName, String evaluationFieldName, String evaluationFieldValue, boolean mandatory) {
        boolean resTestQueries = false;

        if (mandatory) {
            // field is always mandatory - target_field
            if (evaluationFieldName.isEmpty() && evaluationFieldValue.isEmpty()) {
                if (!fieldName.isEmpty()) {
                    resTestQueries = this.defineTestQueries("target_field", schemaName, tableName, fieldName, evaluationFieldName, evaluationFieldValue);
                }
            } else if (!evaluationFieldName.isEmpty() && evaluationFieldValue.isEmpty()) {
                if (!fieldName.isEmpty()) {
                    resTestQueries = this.defineTestQueries("evaluation_field", schemaName, tableName, fieldName, evaluationFieldName, evaluationFieldValue);
                }
            } else if (!evaluationFieldName.isEmpty() && !evaluationFieldValue.isEmpty()) {
                if (!fieldName.isEmpty()) {
                    resTestQueries = this.defineTestQueries("evaluation_value", schemaName, tableName, fieldName, evaluationFieldName, evaluationFieldValue);
                }
            }
        }

        return resTestQueries;
    }

    private boolean defineTestQueries(String checkType, String schemaName, String tableName, String fieldName, String evaluationFieldName, String evaluationFieldValue) {

        boolean resTestQueries = true;
        if (checkType.equalsIgnoreCase("target_field")) {
            // Rule 1
            this.setSqlSuccess("select count(*) as \"RES_SUC\" from " + schemaName + "."
                    + tableName + " where " + fieldName
                    + " is not null or trim(" + fieldName + ") <> ''");
            this.setSqlError("select count(*) as \"RES_ERR\" from " + schemaName + "."
                    + tableName + " where " + fieldName + " is null or trim("
                    + fieldName + ") = ''");
        } else if (checkType.equalsIgnoreCase("evaluation_field")) {
            this.setSqlSuccess("select count(*) as \"RES_SUC\" from " + schemaName + "."
                    + tableName + " where (" + fieldName
                    + " is not null or trim(" + fieldName + ") <> '') and ("
                    + evaluationFieldName + " is not null or trim("
                    + evaluationFieldName + ") <> '')");
            this.setSqlError("select count(*) as \"RES_ERR\" from " + schemaName + "."
                    + tableName + " where (" + fieldName + " is null or trim("
                    + fieldName + ") = '') and (" + evaluationFieldName
                    + " is not null or trim(" + evaluationFieldName + ") <> '')");
        } else if (checkType.equalsIgnoreCase("evaluation_value")) {
            this.setSqlSuccess("select count(*) as \"RES_SUC\" from " + schemaName + "."
                    + tableName + " where (" + fieldName
                    + " is not null or trim(" + fieldName + ") <> '') and "
                    + evaluationFieldName + "='" + evaluationFieldValue
                    + "'");
            this.setSqlError("select count(*) as \"RES_ERR\" from " + schemaName + "."
                    + tableName + " where (" + fieldName + " is null or trim("
                    + fieldName + ") = '') and " + evaluationFieldName + "='"
                    + evaluationFieldValue + "'");
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