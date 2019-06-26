package io.metadew.iesi.script.action;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;

public class EvalVerifySingleField {

    private ActionExecution actionExecution;

    private FrameworkExecution frameworkExecution;

    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation databaseName;

    private ActionParameterOperation schemaName;

    private ActionParameterOperation tableName;

    private ActionParameterOperation fieldName;

    private ActionParameterOperation checkName;

    private ActionParameterOperation checkOperatorName;

    private ActionParameterOperation checkValue;

    private ActionParameterOperation connectionName;

    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Local
    private String sqlSuccess;

    private String sqlError;

    // Constructors
    public EvalVerifySingleField() {

    }

    public EvalVerifySingleField(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                 ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setDatabaseName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "database"));
        this.setSchemaName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "schema"));
        this.setTableName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "table"));
        this.setFieldName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "field"));
        this.setCheckName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "check"));
        this.setCheckOperatorName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "operator"));
        this.setCheckValue(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "value"));
        this.setConnectionName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("database")) {
                this.getDatabaseName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("schema")) {
                this.getSchemaName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("table")) {
                this.getTableName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("field")) {
                this.getFieldName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("check")) {
                this.getCheckName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("operator")) {
                this.getCheckOperatorName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("value")) {
                this.getCheckValue().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("database", this.getDatabaseName());
        this.getActionParameterOperationMap().put("schema", this.getSchemaName());
        this.getActionParameterOperationMap().put("table", this.getTableName());
        this.getActionParameterOperationMap().put("field", this.getFieldName());
        this.getActionParameterOperationMap().put("check", this.getCheckName());
        this.getActionParameterOperationMap().put("operator", this.getCheckOperatorName());
        this.getActionParameterOperationMap().put("value", this.getCheckValue());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }

    public boolean execute() {
        try {
            String databaseName = convertDatabaseName(getDatabaseName().getValue());
            String schemaName = convertSchemaName(getSchemaName().getValue());
            String tableName = convertTableName(getTableName().getValue());
            String fieldName = convertFieldName(getFieldName().getValue());
            String checkName = convertCheckName(getCheckName().getValue());
            String checkValue = convertCheckValue(getCheckValue().getValue());
            String checkOperatorName = convertCheckOperationName(getCheckOperatorName().getValue());
            String connectionName = convertConnectionName(getConnectionName().getValue());
            return verifySingleField(databaseName, schemaName, tableName, fieldName, checkName, checkName, checkValue, checkOperatorName, connectionName);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean verifySingleField(String databaseName, String schemaName, String tableName, String fieldName, String checkName, String checkName1, String checkValue, String checkOperatorName, String connectionName) throws SQLException {
        // Get Connection
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        Connection connection = connectionConfiguration.getConnection(connectionName, this.getExecutionControl().getEnvName()).get();
        ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
        Database database = connectionOperation.getDatabase(connection);

        // Run the action
        this.getTestQueries(schemaName, tableName, fieldName, checkName, checkValue);
        long successTotal = 0;
        long errorTotal = 0;
        CachedRowSet crs;

        // Success
        crs = database.executeQuery(this.getSqlSuccess());
        while (crs.next()) {
            successTotal = crs.getLong("RES_SUC");
        }
        crs.close();
        this.getActionExecution().getActionControl().logOutput("pass", Long.toString(successTotal));

        // Error
        crs = database.executeQuery(this.getSqlError());
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

    private String convertCheckOperationName(DataType checkOperationName) {
        if (checkOperationName instanceof Text) {
            return checkOperationName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for check operation name",
                    checkOperationName.getClass()), Level.WARN);
            return checkOperationName.toString();
        }
    }

    private String convertCheckValue(DataType checkValue) {
        if (checkValue instanceof Text) {
            return checkValue.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for check value",
                    checkValue.getClass()), Level.WARN);
            return checkValue.toString();
        }
    }

    private String convertCheckName(DataType checkName) {
        if (checkName instanceof Text) {
            return checkName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for check name",
                    checkName.getClass()), Level.WARN);
            return checkName.toString();
        }
    }

    private String convertTableName(DataType tableName) {
        if (tableName instanceof Text) {
            return tableName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for table name",
                    tableName.getClass()), Level.WARN);
            return tableName.toString();
        }
    }

    private String convertFieldName(DataType fieldName) {
        if (fieldName instanceof Text) {
            return fieldName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for field name",
                    fieldName.getClass()), Level.WARN);
            return fieldName.toString();
        }
    }

    private String convertSchemaName(DataType schemaName) {
        if (schemaName instanceof Text) {
            return schemaName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for schema name",
                    schemaName.getClass()), Level.WARN);
            return schemaName.toString();
        }
    }

    private String convertDatabaseName(DataType databaseName) {
        if (databaseName instanceof Text) {
            return databaseName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for database name",
                    databaseName.getClass()), Level.WARN);
            return databaseName.toString();
        }
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()), Level.WARN);
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

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
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

    public ActionParameterOperation getCheckName() {
        return checkName;
    }

    public void setCheckName(ActionParameterOperation checkName) {
        this.checkName = checkName;
    }

    public ActionParameterOperation getCheckValue() {
        return checkValue;
    }

    public void setCheckValue(ActionParameterOperation checkValue) {
        this.checkValue = checkValue;
    }

    public ActionParameterOperation getCheckOperatorName() {
        return checkOperatorName;
    }

    public void setCheckOperatorName(ActionParameterOperation checkOperatorName) {
        this.checkOperatorName = checkOperatorName;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

}