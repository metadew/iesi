package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.configuration.action.performance.ActionPerformanceConfiguration;
import io.metadew.iesi.metadata.configuration.type.ActionTypeConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.script.configuration.IterationInstance;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.ComponentAttributeOperation;
import io.metadew.iesi.script.operation.ConditionOperation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ActionExecution {

	private final static Logger LOGGER = LogManager.getLogger();
	private final ActionPerformanceLogger actionPerformanceLogger;
	private final ActionTypeConfiguration actionTypeConfiguration;
	private ExecutionControl executionControl;
	private ActionControl actionControl;
	private ScriptExecution scriptExecution;
	private Action action;
	private Long processId;
	private ComponentAttributeOperation componentAttributeOperation;
	private Object actionTypeExecution;
	private boolean initialized = false;
	private boolean executed = false;
	private boolean childExecution = false;

	// Constructors
	public ActionExecution(ExecutionControl executionControl,
						   ScriptExecution scriptExecution, Action action) {
		this.executionControl = executionControl;
		this.scriptExecution = scriptExecution;
		this.action = action;
		this.actionPerformanceLogger = new ActionPerformanceLogger(new ActionPerformanceConfiguration());
		this.actionTypeConfiguration = new ActionTypeConfiguration();
	}

	// Methods
	public void initialize() {
		this.processId = executionControl.getNextProcessId();
		this.initialized = true;
		this.executed = false;
	}

	@SuppressWarnings("unchecked")
	public void execute(IterationInstance iterationInstance) {
		this.executed = true;

		LOGGER.info("action.name=" + action.getName());
		LOGGER.debug("action.prcid=" + processId);

		// Log Start
		executionControl.logStart(this);

		// Initialize control
		this.actionControl = new ActionControl(executionControl, this);
		actionControl.getActionRuntime().initActionCache(action.getName(), executionControl.getExecutionRuntime().getRunCacheFolderName());

		// Initialize iteration variables
		if (iterationInstance != null) {
			actionControl.getActionRuntime().setRuntimeParameter("iteration", "number", String.valueOf(iterationInstance.getIterationNumber()));
			actionControl.getActionRuntime().setRuntimeParameters("iteration", iterationInstance.getVariableMap());
		}

		try {
			// Set Attributes
			if (action.getComponent() != null && !action.getComponent().trim().equalsIgnoreCase("")) {
				this.setComponentAttributeOperation(new ComponentAttributeOperation(executionControl, this, action.getComponent().trim()));
			}

			String className = actionTypeConfiguration.getActionType(action.getType()).getClassName();
			LOGGER.debug("action.type=" + action.getType());

			Class classRef = Class.forName(className);

			Class[] initParams = { ExecutionControl.class, ScriptExecution.class, ActionExecution.class};
			Constructor constructor = classRef.getConstructor(initParams);
			Object[] initArgs = { executionControl, scriptExecution, this };
			Object instance = constructor.newInstance(initArgs);

			Method prepare = classRef.getDeclaredMethod("prepare");
			prepare.invoke(instance);

			// Check condition, execute by default
			boolean conditionResult = true;
			if (action.getCondition() != null && !action.getCondition().isEmpty() && !action.getCondition().equalsIgnoreCase("null")) {
				ConditionOperation conditionOperation = new ConditionOperation(this, action.getCondition());
				try {
					conditionResult = conditionOperation.evaluateCondition();
				} catch (Exception exception) {
					conditionResult = true;
					LOGGER.warn("action.condition=" + action.getCondition());
					LOGGER.warn("action.condition.error=" + exception.getMessage());
				}
			}

			// Execution
			if (conditionResult) {
				Method method = classRef.getDeclaredMethod("execute");
				LocalDateTime start = LocalDateTime.now();
				method.invoke(instance);
				actionPerformanceLogger.log(this, "action", start, LocalDateTime.now());

				HashMap<String, ActionParameterOperation> actionParameterOperationMap = null;
				for (Field field : classRef.getDeclaredFields()) {
					if (field.getName().equalsIgnoreCase("actionParameterOperationMap")) {
						Method getActionParameterOperationMap = classRef.getDeclaredMethod("getActionParameterOperationMap");
						actionParameterOperationMap = (HashMap<String, ActionParameterOperation>) getActionParameterOperationMap.invoke(instance);
					}
				}

				// Store runtime parameters for next action usage
				// A clone is needed since the iterator through the hashmap will remove the current item to avoid a ConcurrentModificationException
				HashMap<String, ActionParameterOperation> actionParameterOperationMapClone = (HashMap<String, ActionParameterOperation>) actionParameterOperationMap.clone();
				actionControl.getActionRuntime().setRuntimeParameters(actionParameterOperationMapClone);

				// Store actionTypeExecution
				this.actionTypeExecution = instance;

				// Trace function
				actionParameterOperationMapClone = (HashMap<String, ActionParameterOperation>) actionParameterOperationMap.clone();

				this.traceDesignMetadata(actionParameterOperationMapClone);

				// Evaluate error expected
				if (actionControl.getExecutionMetrics().getErrorCount() > 0) {
					if (action.getErrorExpected()) {
						actionControl.getExecutionMetrics().resetErrorCount();
						actionControl.getExecutionMetrics().increaseSuccessCount(1);
						LOGGER.info("action.status=ERROR:expected");
					}
				} else {
					if (action.getErrorExpected()) {
						actionControl.getExecutionMetrics().resetSuccessCount();
						actionControl.getExecutionMetrics().increaseErrorCount(1);
						LOGGER.info("action.status=SUCCESS:unexpected");
					}
				}

			} else {
				// Skip execution
				actionControl.increaseSkipCount();
				// TODO log output
			}

		} catch (Exception e) {
			StringWriter stackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTrace));
			LOGGER.info("action.error=" + e);
			LOGGER.debug("action.stacktrace=" + stackTrace);
			actionControl.increaseErrorCount();
		}
		executionControl.logEnd(this, scriptExecution);

	}

	public void skip() {
		LOGGER.info("action.name=" + action.getName());
		LOGGER.debug("action.id=" + action.getId());
		LOGGER.info("action.selection.skip");

		// Log Skip
		executionControl.logSkip(this);

		// Trace Design Metadata
		this.traceDesignMetadata(null);
	}

	public void traceDesignMetadata(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
		executionControl.getExecutionTrace().setExecution(this, actionParameterOperationMap);
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public ExecutionControl getExecutionControl() {
		return executionControl;
	}

	public void setExecutionControl(ExecutionControl executionControl) {
		this.executionControl = executionControl;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public ScriptExecution getScriptExecution() {
		return scriptExecution;
	}

	public void setScriptExecution(ScriptExecution scriptExecution) {
		this.scriptExecution = scriptExecution;
	}

	public ComponentAttributeOperation getComponentAttributeOperation() {
		return componentAttributeOperation;
	}

	public void setComponentAttributeOperation(ComponentAttributeOperation componentAttributeOperation) {
		this.componentAttributeOperation = componentAttributeOperation;
	}

	public ActionControl getActionControl() {
		return actionControl;
	}

	public void setActionControl(ActionControl actionControl) {
		this.actionControl = actionControl;
	}

	public Object getActionTypeExecution() {
		return actionTypeExecution;
	}

	public void setActionTypeExecution(Object actionTypeExecution) {
		this.actionTypeExecution = actionTypeExecution;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public boolean isChildExecution() {
		return childExecution;
	}

	public void setChildExecution(boolean childExecution) {
		this.childExecution = childExecution;
	}

	public ActionPerformanceLogger getActionPerformanceLogger() {
		return actionPerformanceLogger;
	}
}