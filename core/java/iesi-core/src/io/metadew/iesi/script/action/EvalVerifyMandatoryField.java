package io.metadew.iesi.script.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

public class EvalVerifyMandatoryField {

	private ActionExecution actionExecution;

	private FrameworkExecution frameworkExecution;

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

	// Constructors
	public EvalVerifyMandatoryField() {

	}

	public EvalVerifyMandatoryField(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
		this.setEvaluationFieldName(
				new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
						this.getActionExecution(), this.getActionExecution().getAction().getType(), "evaluationField"));
		this.setEvaluationFieldValue(
				new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
						this.getActionExecution(), this.getActionExecution().getAction().getType(), "evaluationValue"));
		this.setMandatoryFlag(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "isMandatory"));
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
			} else if (actionParameter.getName().equalsIgnoreCase("evaluationfield")) {
				this.getEvaluationFieldName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("evaluationvalue")) {
				this.getEvaluationFieldValue().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("ismandatory")) {
				this.getMandatoryFlag().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("connection")) {
				this.getConnectionName().setInputValue(actionParameter.getValue());
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

	public boolean execute() {
		try {
			// Get Connection
			ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution());
			Connection connection = connectionConfiguration.getConnection(this.getConnectionName().getValue(),
					this.getExecutionControl().getEnvName()).get();
			ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
			DatabaseConnection databaseConnection = connectionOperation.getDatabaseConnection(connection);

			// Run the action
			this.getTestQueries();
			long successTotal = 0;
			long errorTotal = 0;
			CachedRowSet crs = null;

			// Success
			crs = databaseConnection.executeQuery(this.getSqlSuccess());
			while (crs.next()) {
				successTotal = crs.getLong("RES_SUC");
			}
			crs.close();
			this.getActionExecution().getActionControl().logOutput("pass", Long.toString(successTotal));

			// Error
			crs = databaseConnection.executeQuery(this.getSqlError());
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
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			this.getActionExecution().getActionControl().increaseErrorCount();

			this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
			this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

			return false;
		}

	}

	// Perform verification
	private boolean getTestQueries() {
		boolean resTestQueries = false;

		if (this.getMandatoryFlag().getValue().trim().equalsIgnoreCase("y")) {
			// field is always mandatory - target_field
			if ((this.getEvaluationFieldName().getValue() == null || this.getEvaluationFieldName().getValue().isEmpty())
					&& (this.getEvaluationFieldValue().getValue() == null
							|| this.getEvaluationFieldValue().getValue().isEmpty())) {
				if (this.getFieldName().getValue() != null && !this.getFieldName().getValue().isEmpty()) {
					resTestQueries = this.defineTestQueries("target_field");
				}
			}

			// Mandatory field check on evaluation column - evaluation_field
			if (this.getEvaluationFieldName().getValue() != null && !this.getEvaluationFieldName().getValue().isEmpty()
					&& (this.getEvaluationFieldValue().getValue() == null
							|| this.getEvaluationFieldValue().getValue().isEmpty())) {
				if (this.getFieldName().getValue() != null && !this.getFieldName().getValue().isEmpty()) {
					resTestQueries = this.defineTestQueries("evaluation_field");
				}
			}

			// Mandatory field check on evaluation column and value -
			// evaluation_value
			if (this.getEvaluationFieldName().getValue() != null && !this.getEvaluationFieldName().getValue().isEmpty()
					&& this.getEvaluationFieldValue().getValue() != null
					&& !this.getEvaluationFieldValue().getValue().isEmpty()) {
				if (this.getFieldName().getValue() != null && !this.getFieldName().getValue().isEmpty()) {
					resTestQueries = this.defineTestQueries("evaluation_value");
				}
			}
		}

		return resTestQueries;
	}

	private boolean defineTestQueries(String check_type) {

		boolean resTestQueries = true;
		if (check_type.equalsIgnoreCase("target_field")) {
			// Rule 1
			this.setSqlSuccess("select count(*) as \"RES_SUC\" from " + this.getSchemaName().getValue() + "."
					+ this.getTableName().getValue() + " where " + this.getFieldName().getValue()
					+ " is not null or trim(" + this.getFieldName().getValue() + ") <> ''");
			this.setSqlError("select count(*) as \"RES_ERR\" from " + this.getSchemaName().getValue() + "."
					+ this.getTableName().getValue() + " where " + this.getFieldName().getValue() + " is null or trim("
					+ this.getFieldName().getValue() + ") = ''");
		} else if (check_type.equalsIgnoreCase("evaluation_field")) {
			this.setSqlSuccess("select count(*) as \"RES_SUC\" from " + this.getSchemaName().getValue() + "."
					+ this.getTableName().getValue() + " where (" + this.getFieldName().getValue()
					+ " is not null or trim(" + this.getFieldName().getValue() + ") <> '') and ("
					+ this.getEvaluationFieldName().getValue() + " is not null or trim("
					+ this.getEvaluationFieldName().getValue() + ") <> '')");
			this.setSqlError("select count(*) as \"RES_ERR\" from " + this.getSchemaName().getValue() + "."
					+ this.getTableName().getValue() + " where (" + this.getFieldName().getValue() + " is null or trim("
					+ this.getFieldName().getValue() + ") = '') and (" + this.getEvaluationFieldName().getValue()
					+ " is not null or trim(" + this.getEvaluationFieldName().getValue() + ") <> '')");
		} else if (check_type.equalsIgnoreCase("evaluation_value")) {
			this.setSqlSuccess("select count(*) as \"RES_SUC\" from " + this.getSchemaName().getValue() + "."
					+ this.getTableName().getValue() + " where (" + this.getFieldName().getValue()
					+ " is not null or trim(" + this.getFieldName().getValue() + ") <> '') and "
					+ this.getEvaluationFieldName().getValue() + "='" + this.getEvaluationFieldValue().getValue()
					+ "'");
			this.setSqlError("select count(*) as \"RES_ERR\" from " + this.getSchemaName().getValue() + "."
					+ this.getTableName().getValue() + " where (" + this.getFieldName().getValue() + " is null or trim("
					+ this.getFieldName().getValue() + ") = '') and " + this.getEvaluationFieldName().getValue() + "='"
					+ this.getEvaluationFieldValue().getValue() + "'");
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