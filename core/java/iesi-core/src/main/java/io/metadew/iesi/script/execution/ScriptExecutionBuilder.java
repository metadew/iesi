package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.script.ScriptExecutionBuildException;
import io.metadew.iesi.script.operation.ActionSelectOperation;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ScriptExecutionBuilder {

    private final boolean root;
    private final boolean route;
    private Script script;
    private ExecutionControl executionControl;
    private ExecutionMetrics executionMetrics;
    private Long processId;
    private boolean exitOnCompletion = true;
    private ScriptExecution parentScriptExecution;
    private String paramList = "";
    private String paramFile = "";
    private ActionSelectOperation actionSelectOperation;
    private String environment;

    public ScriptExecutionBuilder(boolean root, boolean route) {
        this.root = root;
        this.route = route;
    }

    public ScriptExecutionBuilder script(Script script) {
        this.script = script;
        return this;
    }
    
    
    public ScriptExecutionBuilder environment(String environment) {
        this.environment = environment;
        return this;
    }

    public ScriptExecutionBuilder executionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
        return this;
    }

    public ScriptExecutionBuilder executionMetrics(ExecutionMetrics executionMetrics) {
        this.executionMetrics = executionMetrics;
        return this;
    }

    public ScriptExecutionBuilder processId(Long processId) {
        this.processId = processId;
        return this;
    }

    public ScriptExecutionBuilder exitOnCompletion(boolean exitOnCompletion) {
        this.exitOnCompletion = exitOnCompletion;
        return this;
    }

    public ScriptExecutionBuilder parentScriptExecution(ScriptExecution parentScriptExecution) {
        this.parentScriptExecution = parentScriptExecution;
        return this;
    }

    public ScriptExecutionBuilder paramList(String paramList) {
        this.paramList = paramList;
        return this;
    }

    public ScriptExecutionBuilder paramFile(String paramFile) {
        this.paramFile = paramFile;
        return this;
    }

    public ScriptExecutionBuilder actionSelectOperation(ActionSelectOperation actionSelectOperation) {
        this.actionSelectOperation = actionSelectOperation;
        return this;
    }

    public ScriptExecution build() throws ScriptExecutionBuildException {
        if (route) {
            return buildRouteScriptExecution();
        } else {
            return buildNonRouteScriptExecution();
        }
    }

    private ScriptExecution buildNonRouteScriptExecution() throws ScriptExecutionBuildException {
        if (root) {
            try {
                ExecutionControl executionControl = new ExecutionControl();
                return new NonRouteScriptExecution(
                        getScript().orElseThrow(() -> new ScriptExecutionBuildException("No script supplied to script execution builder")),
                        executionControl,
                        new ExecutionMetrics(),
                        executionControl.getNewProcessId(),
                        getExitOnCompletion().orElseThrow(() -> new ScriptExecutionBuildException("No exit on completion flag supplied to script execution builder")),
                        getParentScriptExecution().orElse(null),
                        getParamList().orElse(""),
                        getParamFile().orElse(""),
                        getActionSelectOperation().orElseThrow(() -> new ScriptExecutionBuildException("No action selection supplied to script execution builder")),
                        new RootStrategy(getEnvironment().orElseThrow(() -> new ScriptExecutionBuildException("No environment supplied to root script execution builder")))
                );
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new ScriptExecutionBuildException(e);
            }
        } else {
            ActionSelectOperation actionSelectOperation = new ActionSelectOperation("");
            return new NonRouteScriptExecution(
                    getScript().orElseThrow(() -> new ScriptExecutionBuildException("No script supplied to script execution builder")),
                    executionControl,
                    new ExecutionMetrics(),
                    executionControl.getNewProcessId(),
                    getExitOnCompletion().orElseThrow(() -> new ScriptExecutionBuildException("No exit on completion flag supplied to script execution builder")),
                    getParentScriptExecution().orElse(null),
                    getParamList().orElse(""),
                    getParamFile().orElse(""),
                    actionSelectOperation,
                    new NonRootStrategy()
            );
        }
    }

    private ScriptExecution buildRouteScriptExecution() throws ScriptExecutionBuildException {
        try {
            ExecutionControl executionControl = root ? new ExecutionControl() : getExecutionControl().orElseThrow(() -> new ScriptExecutionBuildException("No execution control supplied to route script execution builder"));
            return new RouteScriptExecution (
                    getScript().orElseThrow(() -> new ScriptExecutionBuildException("No script supplied to route script execution builder")),
                    executionControl,
                    getExecutionMetrics().orElseThrow(() -> new ScriptExecutionBuildException("No execution metrics supplied to route script execution builder")),
                    executionControl.getNewProcessId(),
                    getExitOnCompletion().orElseThrow(() -> new ScriptExecutionBuildException("No exit on completion flag supplied to route script execution builder")),
                    getParentScriptExecution().orElse(null),
                    getParamList().orElse(""),
                    getParamFile().orElse(""),
                    getActionSelectOperation().orElseThrow(() -> new ScriptExecutionBuildException("No action selection supplied to route script execution builder")),
                    root ? new RootStrategy(getEnvironment().orElseThrow(() -> new ScriptExecutionBuildException("No environment supplied to root script execution builder"))) : new NonRootStrategy()
            );
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new ScriptExecutionBuildException(e);
        }
    }

    public Optional<Script> getScript() {
        return  Optional.ofNullable(script);
    }

    public Optional<ExecutionControl> getExecutionControl() {
        return Optional.ofNullable(executionControl);
    }

    public Optional<ExecutionMetrics> getExecutionMetrics() {
        return Optional.ofNullable(executionMetrics);
    }

    public Optional<Long> getProcessId() {
        return Optional.ofNullable(processId);
    }

    public Optional<Boolean> getExitOnCompletion() {
        return Optional.of(exitOnCompletion);
    }

    public Optional<ScriptExecution> getParentScriptExecution() {
        return Optional.ofNullable(parentScriptExecution);
    }

    public Optional<String> getParamList() {
        return Optional.ofNullable(paramList);
    }

    public Optional<String> getParamFile() {
        return Optional.ofNullable(paramFile);
    }

    public Optional<ActionSelectOperation> getActionSelectOperation() {
        return Optional.ofNullable(actionSelectOperation);
    }

    public Optional<String> getEnvironment() {
        return Optional.ofNullable(environment);
    }
}
