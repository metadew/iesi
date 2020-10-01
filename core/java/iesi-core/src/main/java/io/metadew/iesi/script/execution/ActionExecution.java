package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.configuration.type.ActionTypeConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.configuration.IterationInstance;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.ComponentAttributeOperation;
import io.metadew.iesi.script.service.ConditionService;
import lombok.extern.log4j.Log4j2;

import javax.script.ScriptException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Log4j2
public class ActionExecution {

    private ExecutionControl executionControl;
    private ActionControl actionControl;
    private ScriptExecution scriptExecution;
    private Action action;
    private Long processId;
    private ComponentAttributeOperation componentAttributeOperation;
    private ActionTypeExecution actionTypeExecution;
    private boolean executed = false;

    // Constructors
    public ActionExecution(ExecutionControl executionControl,
                           ScriptExecution scriptExecution, Action action) {
        this.executionControl = executionControl;
        this.scriptExecution = scriptExecution;
        this.action = action;
    }

    // Methods
    public void initialize() {
        this.processId = executionControl.getNextProcessId();
        this.executed = false;
    }

    @SuppressWarnings("unchecked")
    public void execute(IterationInstance iterationInstance) throws InterruptedException {
        this.executed = true;

        log.info("action.name=" + action.getName());
        log.debug("action.prcid=" + processId);

        // Log Start
        executionControl.logStart(this);

        // Initialize control
        this.actionControl = new ActionControl(executionControl, this);
        actionControl.getActionRuntime().initActionCache(executionControl.getExecutionRuntime().getRunCacheFolderName());

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

            String className = ActionTypeConfiguration.getInstance().getActionType(action.getType()).getClassName();
            log.debug("action.type=" + action.getType());

            Class classRef = Class.forName(className);

            Class[] initParams = {ExecutionControl.class, ScriptExecution.class, ActionExecution.class};
            Constructor constructor = classRef.getConstructor(initParams);
            Object[] initArgs = {executionControl, scriptExecution, this};
            ActionTypeExecution instance = (ActionTypeExecution) constructor.newInstance(initArgs);
            // Store actionTypeExecution
            this.actionTypeExecution = instance;

            // Execution
            if (evaluateCondition(action.getCondition())) {
                instance.prepare();

                LocalDateTime start = LocalDateTime.now();
                instance.execute();
                ActionPerformanceLogger.getInstance().log(this, "action", start, LocalDateTime.now());

                HashMap<String, ActionParameterOperation> actionParameterOperationMap = instance.getActionParameterOperationMap();

                // Store runtime parameters for next action usage
                // A clone is needed since the iterator through the hashmap will remove the current item to avoid a ConcurrentModificationException
                HashMap<String, ActionParameterOperation> actionParameterOperationMapClone = (HashMap<String, ActionParameterOperation>) actionParameterOperationMap.clone();
                actionControl.getActionRuntime().setRuntimeParameters(actionParameterOperationMapClone);


                // Trace function
                actionParameterOperationMapClone = (HashMap<String, ActionParameterOperation>) actionParameterOperationMap.clone();

                this.traceDesignMetadata(actionParameterOperationMapClone);

                // Evaluate error expected
                if (actionControl.getExecutionMetrics().getErrorCount() > 0) {
                    if (action.getErrorExpected()) {
                        actionControl.getExecutionMetrics().resetErrorCount();
                        actionControl.getExecutionMetrics().increaseSuccessCount(1);
                        log.info("action.status=ERROR:expected");
                    }
                } else {
                    if (action.getErrorExpected()) {
                        actionControl.getExecutionMetrics().resetSuccessCount();
                        actionControl.getExecutionMetrics().increaseErrorCount(1);
                        log.info("action.status=SUCCESS:unexpected");
                    }
                }

            } else {
                // Skip execution
                actionControl.increaseSkipCount();
                // TODO log output
            }
            dummy();
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            actionControl.logOutput("action.error", e.getMessage());
            actionControl.logOutput("action.stacktrace", stackTrace.toString());
            log.info("action.error=" + e);
            log.debug("action.stacktrace=" + stackTrace);
            actionControl.increaseErrorCount();
        }
        actionControl.getActionRuntime().getRuntimeActionCacheConfiguration().shutdown();
        executionControl.logEnd(this, scriptExecution);
    }

    private boolean evaluateCondition(String condition) {
        if (condition == null || condition.isEmpty() || condition.equalsIgnoreCase("null")) {
            return true;
        } else {
            log.info("action.condition=" + condition);
            try {
                return ConditionService.getInstance()
                        .evaluateCondition(condition, executionControl.getExecutionRuntime(), this);
            } catch (ScriptException e) {
                log.warn("action.condition.error=" + e.getMessage());
                return false;
            }
        }
    }

    private void dummy() throws InterruptedException {
    }

    public void skip() {
        log.info("action.name=" + action.getName());
        log.debug("action.id=" + action.getMetadataKey().getActionId());
        log.info("action.selection.skip");

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

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public Long getProcessId() {
        return processId;
    }

    public ScriptExecution getScriptExecution() {
        return scriptExecution;
    }

    public Optional<ComponentAttributeOperation> getComponentAttributeOperation() {
        return Optional.ofNullable(componentAttributeOperation);
    }

    public void setComponentAttributeOperation(ComponentAttributeOperation componentAttributeOperation) {
        this.componentAttributeOperation = componentAttributeOperation;
    }

    public ActionControl getActionControl() {
        return actionControl;
    }

    public Object getActionTypeExecution() {
        return actionTypeExecution;
    }

    public boolean isExecuted() {
        return executed;
    }


}