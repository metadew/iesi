package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;


public class EvalVerifyMandatoryField {

    private ActionExecution actionExecution;

    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation databaseName;

    private ActionParameterOperation schemaName;

    private ActionParameterOperation tableName;

    private ActionParameterOperation fieldName;

    private ActionParameterOperation evaluationFieldName;

    private ActionParameterOperation evaluationFieldValue;

    private ActionParameterOperation mandatoryFlag;

    private ActionParameterOperation connectionName;

    // Local
    private String sqlSuccess;

    private String sqlError;

    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public EvalVerifyMandatoryField() {

    }

    public EvalVerifyMandatoryField(ExecutionControl executionControl,
                                    ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() throws Exception {
        // Reset Parameters
        this.setDatabaseName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "database"));
        this.setSchemaName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "schema"));
        this.setTableName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "table"));
        this.setFieldName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "field"));
        this.setEvaluationFieldName(
                new ActionParameterOperation(this.getExecutionControl(),
                        this.getActionExecution(), this.getActionExecution().getAction().getType(), "evaluationField"));
        this.setEvaluationFieldValue(
                new ActionParameterOperation(this.getExecutionControl(),
                        this.getActionExecution(), this.getActionExecution().getAction().getType(), "evaluationValue"));
        this.setMandatoryFlag(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "isMandatory"));
        this.setConnectionName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("database")) {
                this.getDatabaseName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("schema")) {
                this.getSchemaName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("table")) {
                this.getTableName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("field")) {
                this.getFieldName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("evaluationfield")) {
                this.getEvaluationFieldName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("evaluationvalue")) {
                this.getEvaluationFieldValue().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("ismandatory")) {
                this.getMandatoryFlag().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("database", this.getDatabaseName());
        this.getActionParameterOperationMap().put("schema", this.getSchemaName());
        this.getActionParameterOperationMap().put("table", this.getTableName());
        this.getActionParameterOperationMap().put("field", this.getFieldName());
        this.getActionParameterOperationMap().put("evaluationField", this.getEvaluationFieldName());
        this.getActionParameterOperationMap().put("evaluationValue", this.getEvaluationFieldValue());
        this.getActionParameterOperationMap().put("isMandatory", this.getMandatoryFlag());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }

    public boolean execute() throws InterruptedException {
        try {
            String databaseName = convertDatabaseName(getDatabaseName().getValue());
            String schemaName = convertSchemaName(getSchemaName().getValue());
            String tableName = convertTableName(getTableName().getValue());
            String fieldName = convertFieldName(getFieldName().getValue());
            String evaluationFieldName = convertEvaluationFieldName(getEvaluationFieldName().getValue());
            String evaluationFieldValue = convertEvaluationFieldValue(getEvaluationFieldValue().getValue());
            boolean isMandatory = convertIsMandatory(getMandatoryFlag().getValue());
            String connectionName = convertConnectionName(getConnectionName().getValue());
            return verifyMandatoryField(databaseName, schemaName, tableName, fieldName, evaluationFieldName, evaluationFieldValue, isMandatory, connectionName);
        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean verifyMandatoryField(String databaseName, String schemaName, String tableName, String fieldName, String evaluationFieldName, String evaluationFieldValue, boolean isMandatory, String connectionName) throws SQLException, InterruptedException {
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
            return true;
        }
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

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public ActionParameterOperation getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(ActionParameterOperation databaseName) {
        this.databaseName = databaseName;
    }

    public ActionParameterOperation getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(ActionParameterOperation schemaName) {
        this.schemaName = schemaName;
    }

    public ActionParameterOperation getTableName() {
        return tableName;
    }

    public void setTableName(ActionParameterOperation tableName) {
        this.tableName = tableName;
    }

    public ActionParameterOperation getFieldName() {
        return fieldName;
    }

    public void setFieldName(ActionParameterOperation fieldName) {
        this.fieldName = fieldName;
    }

    public ActionParameterOperation getEvaluationFieldName() {
        return evaluationFieldName;
    }

    public void setEvaluationFieldName(ActionParameterOperation evaluationFieldName) {
        this.evaluationFieldName = evaluationFieldName;
    }

    public ActionParameterOperation getEvaluationFieldValue() {
        return evaluationFieldValue;
    }

    public void setEvaluationFieldValue(ActionParameterOperation evaluationFieldValue) {
        this.evaluationFieldValue = evaluationFieldValue;
    }

    public ActionParameterOperation getMandatoryFlag() {
        return mandatoryFlag;
    }

    public void setMandatoryFlag(ActionParameterOperation mandatoryFlag) {
        this.mandatoryFlag = mandatoryFlag;
    }

    public ActionParameterOperation getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(ActionParameterOperation connectionName) {
        this.connectionName = connectionName;
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

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

}