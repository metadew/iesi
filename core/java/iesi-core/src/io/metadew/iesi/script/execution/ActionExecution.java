package io.metadew.iesi.script.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ActionPerformanceConfiguration;
import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.script.configuration.IterationInstance;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.ComponentAttributeOperation;
import io.metadew.iesi.script.operation.ConditionOperation;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ActionExecution {

	private final ActionPerformanceLogger actionPerformanceLogger;
	private FrameworkExecution frameworkExecution;
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
	public ActionExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
			ScriptExecution scriptExecution, Action action) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setScriptExecution(scriptExecution);
		this.setAction(action);
		this.actionPerformanceLogger = new ActionPerformanceLogger(new ActionPerformanceConfiguration(frameworkExecution.getFrameworkInstance().getMetadataControl()));
	}

	// Methods
	public void initialize() {
		this.setProcessId(this.getExecutionControl().getNewProcessId());
		this.setInitialized(true);
		this.setExecuted(false);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(IterationInstance iterationInstance) {
		this.setExecuted(true);

		this.getExecutionControl().logMessage(this, "action.name=" + this.getAction().getName(), Level.INFO);
		this.getExecutionControl().logMessage(this, "action.id=" + this.getAction().getId(), Level.DEBUG);

		// Log Start
		this.getExecutionControl().logStart(this);

		// Initialize control
		this.setActionControl(new ActionControl(this.getFrameworkExecution(), this.getExecutionControl(), this));
		this.getActionControl().getActionRuntime().initActionCache(this.getAction().getName(),
				this.getExecutionControl().getExecutionRuntime().getRunCacheFolderName());

		// Initialize iteration variables
		if (iterationInstance != null) {
			this.getActionControl().getActionRuntime().setRuntimeParameter("iteration", "number",
					String.valueOf(iterationInstance.getIterationNumber()));
			this.getActionControl().getActionRuntime().setRuntimeParameters("iteration",
					iterationInstance.getVariableMap());
			;
		}

		try {
			// Set Attributes
			if (this.getAction().getComponent() != null
					&& !this.getAction().getComponent().trim().equalsIgnoreCase("")) {
				this.setComponentAttributeOperation(new ComponentAttributeOperation(this.getFrameworkExecution(),
						this.getExecutionControl(), this, this.getAction().getComponent().trim()));
			}

			String className = this.getFrameworkExecution().getFrameworkConfiguration().getActionTypeConfiguration()
					.getActionTypeClass(this.getAction().getType());
			this.getExecutionControl().logMessage(this, "action.type=" + this.getAction().getType(), Level.DEBUG);

			Class classRef = Class.forName(className);
			Object instance = classRef.newInstance();

			Class initParams[] = { FrameworkExecution.class, ExecutionControl.class, ScriptExecution.class,
					ActionExecution.class };
			Method init = classRef.getDeclaredMethod("init", initParams);
			Object[] initArgs = { this.getFrameworkExecution(), this.getExecutionControl(), this.getScriptExecution(),
					this };
			init.invoke(instance, initArgs);

			Method prepare = classRef.getDeclaredMethod("prepare");
			prepare.invoke(instance);

			// Check condition, execute by default
			boolean conditionResult = true;
			if (this.getAction().getCondition() != null && !this.getAction().getCondition().isEmpty()
					&& !this.getAction().getCondition().equalsIgnoreCase("null")) {
				ConditionOperation conditionOperation = new ConditionOperation(this, this.getAction().getCondition());
				try {
					conditionResult = conditionOperation.evaluateCondition();
				} catch (Exception exception) {
					conditionResult = true;
					this.getExecutionControl().logMessage(this, "action.condition=" + this.getAction().getCondition(),
							Level.WARN);
					this.getExecutionControl().logMessage(this, "action.condition.error=" + exception.getMessage(),
							Level.WARN);
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
						Method getActionParameterOperationMap = classRef
								.getDeclaredMethod("getActionParameterOperationMap");
						actionParameterOperationMap = (HashMap<String, ActionParameterOperation>) getActionParameterOperationMap
								.invoke(instance);
					}
				}

				// Store runtime parameters for next action usage
				// A clone is needed since the iterator through the hashmap will remove the current item to avoid a ConcurrentModificationException
				HashMap<String, ActionParameterOperation> actionParameterOperationMapClone = null;
				actionParameterOperationMapClone = (HashMap<String, ActionParameterOperation>) actionParameterOperationMap.clone();
				this.getActionControl().getActionRuntime().setRuntimeParameters(actionParameterOperationMapClone);

				// Store actionTypeExecution
				this.setActionTypeExecution(instance);

				// Trace function
				actionParameterOperationMapClone = (HashMap<String, ActionParameterOperation>) actionParameterOperationMap.clone();
				this.traceDesignMetadata(actionParameterOperationMapClone);

				// Evaluate error expected
				if (this.getActionControl().getExecutionMetrics().getErrorCount() > 0) {
					if (this.getAction().getErrorExpected().equalsIgnoreCase("y")) {
						this.getActionControl().getExecutionMetrics().resetErrorCount();
						this.getActionControl().getExecutionMetrics().increaseSuccessCount(1);
						this.getExecutionControl().logMessage(this, "action.status=ERROR:expected", Level.INFO);
					}
				} else {
					if (this.getAction().getErrorExpected().equalsIgnoreCase("y")) {
						this.getActionControl().getExecutionMetrics().resetSuccessCount();
						this.getActionControl().getExecutionMetrics().increaseErrorCount(1);
						this.getExecutionControl().logMessage(this, "action.status=SUCCESS:unexpected", Level.INFO);
					}
				}

			} else {
				// Skip execution
				this.getActionControl().increaseSkipCount();
				// TODO log output
			}

		} catch (Exception e) {
			StringWriter stackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTrace));
			this.getActionControl().increaseErrorCount();
			this.getExecutionControl().logMessage(this, "action.error=" + e, Level.INFO);
			this.getExecutionControl().logMessage(this, "action.stacktrace=" + stackTrace, Level.DEBUG);
		} finally {
			// Log End
			this.getExecutionControl().logEnd(this, this.getScriptExecution());
		}

	}

	public void skip() {
		this.getExecutionControl().logMessage(this, "action.name=" + this.getAction().getName(), Level.INFO);
		this.getExecutionControl().logMessage(this, "action.id=" + this.getAction().getId(), Level.DEBUG);
		this.getExecutionControl().logMessage(this, "action.selection.skip", Level.INFO);

		// Log Skip
		this.getExecutionControl().logSkip(this);

		// Trace Design Metadata
		this.traceDesignMetadata(null);
	}

	public void traceDesignMetadata(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
		this.getExecutionControl().getExecutionTrace().setExecution(this.getScriptExecution(), this,
				actionParameterOperationMap);
	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
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