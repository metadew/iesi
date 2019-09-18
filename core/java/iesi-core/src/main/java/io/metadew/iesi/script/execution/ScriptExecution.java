package io.metadew.iesi.script.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.script.action.fwk.FwkIncludeScript;
import io.metadew.iesi.script.operation.ActionSelectOperation;
import io.metadew.iesi.script.operation.RouteOperation;
import org.apache.logging.log4j.Level;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public abstract class ScriptExecution {
	private RootingStrategy rootingStrategy;
	private Script script;
	private ExecutionControl executionControl;
	private ExecutionMetrics executionMetrics;
	private Long processId;
	private boolean exitOnCompletion;
	private ScriptExecution parentScriptExecution;
	private String result;
	public Map<String, String> parameters;
	public Map<String, String> impersonations;
	private ActionSelectOperation actionSelectOperation;
	private String environment;

	public ScriptExecution(Script script, String environment, ExecutionControl executionControl,
						   ExecutionMetrics executionMetrics, Long processId, boolean exitOnCompletion,
						   ScriptExecution parentScriptExecution, Map<String, String> parameters, Map<String, String> impersonations,
						   ActionSelectOperation actionSelectOperation, RootingStrategy rootingStrategy) {
		this.script = script;
		this.environment = environment;
		this.executionControl = executionControl;
		this.executionMetrics = executionMetrics;
		this.processId = processId;
		this.exitOnCompletion = exitOnCompletion;
		this.parentScriptExecution = parentScriptExecution;
		this.parameters = parameters;
		this.impersonations = impersonations;
		this.actionSelectOperation = actionSelectOperation;
		this.rootingStrategy = rootingStrategy;
	}

	public void execute() {
		executionControl.setEnvName(environment);
		impersonations.forEach((key, value) -> executionControl.getExecutionRuntime().getImpersonationOperation().setImpersonation(key, value));

		rootingStrategy.prepareExecution(this);
		prepareExecution();

		List<Action> actionsToExecute = script.getActions();
		int actionIndex = 0;

		while (actionIndex < actionsToExecute.size()) {
			Action action = actionsToExecute.get(actionIndex);

			ActionExecution actionExecution = new ActionExecution(executionControl, this, action);

			if (!rootingStrategy.executionAllowed(actionSelectOperation, action)) {
				// TODO: log
				actionExecution.skip();
				continue;
			}

			if (action.getType().equalsIgnoreCase("fwk.route")) {
				executeFwkRouteAction(actionExecution);
				break;
			}

			if (action.getType().equalsIgnoreCase("fwk.startIteration")) {
				// Do not change - work in progress
			}

			IterationExecution iterationExecution = new IterationExecution();
			if (action.getIteration() != null && !action.getIteration().trim().isEmpty()) {
				iterationExecution.initialize(executionControl, actionExecution, action.getIteration());
			}

			while (iterationExecution.hasNext()) {
				actionExecution.initialize();
				actionExecution.execute(iterationExecution.getIterationInstance());
				int retryCounter = 1;

				while (retryCounter <= action.getRetries() && actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0) {
					if (action.getErrorStop()) {
						executionControl.logMessage(this, "action.error -> retries.ignore", Level.INFO);
						executionControl.setActionErrorStop(true);
						break;
					} else if (!iterationExecution.isIterationOff() && iterationExecution.getIterationOperation().getIteration().getInterrupt().equalsIgnoreCase("y")
							&& actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0) {
						break;
					}

					actionExecution.initialize();
					executionControl.logMessage(this, "action.retry." + retryCounter, Level.INFO);
					actionExecution.execute(iterationExecution.getIterationInstance());
					retryCounter++;
				}
			}


			if (action.getType().equalsIgnoreCase("fwk.includeScript")) {
				executeFwkIncludeAction(actionExecution, actionsToExecute, actionIndex);
			}

			if (action.getType().equalsIgnoreCase("fwk.exitScript")) {
				executionControl.logMessage(this, "script.exit", Level.INFO);
				executionControl.setScriptExit(true);
				break;
			}

			if (!actionExecution.isExecuted()) {
				executionMetrics.increaseWarningCount(1);
				executionControl.logMessage(this, "action.warning -> iteration.condition.block",
						Level.INFO);
			}

			if (actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0 && action.getErrorStop()) {
				executionControl.logMessage(this, "action.error -> script.stop", Level.INFO);
				executionControl.setActionErrorStop(true);
				break;
			}

			rootingStrategy.continueAction(actionSelectOperation, action);
			actionIndex++;
		}
		endExecution();
	}

	protected abstract void endExecution();

	protected abstract void prepareExecution() ;

	public RootingStrategy getRootingStrategy() {
		return rootingStrategy;
	}

	private void executeFwkIncludeAction(ActionExecution actionExecution, List<Action> actionsToExecute, int actionIndex) {
		ObjectMapper objectMapper = new ObjectMapper();
		FwkIncludeScript fwkIncludeScript = objectMapper.convertValue(actionExecution.getActionTypeExecution(), FwkIncludeScript.class);
		actionsToExecute.addAll(actionIndex, fwkIncludeScript.getScript().getActions());
	}

	private void executeFwkRouteAction(ActionExecution actionExecution) {
		actionExecution.execute(null);

		// Create future variables
		int threads = actionExecution.getActionControl().getActionRuntime().getRouteOperations().size();
		CompletionService<ScriptExecution> completionService = new ExecutorCompletionService<>(Executors.newFixedThreadPool(threads));
		Set<Future<ScriptExecution>> futureScriptExecutions = new HashSet<>();

		// Submit routes
		for (RouteOperation routeOperation : actionExecution.getActionControl().getActionRuntime()
				.getRouteOperations()) {

			Callable<ScriptExecution> callableScriptExecution = () -> new ScriptExecutionBuilder(true, true)
					.script(routeOperation.getScript())
					.executionControl(executionControl)
					.parentScriptExecution(parentScriptExecution)
					.executionMetrics(executionMetrics)
					.actionSelectOperation(new ActionSelectOperation(""))
					.exitOnCompletion(true)
					.build();

			futureScriptExecutions.add(completionService.submit(callableScriptExecution));
		}

		Future<ScriptExecution> completedFuture;
		ScriptExecution completedScriptExecution;
		while (futureScriptExecutions.size() > 0) {
			try {
				completedFuture = completionService.take();
				futureScriptExecutions.remove(completedFuture);

				completedScriptExecution = completedFuture.get();
				this.getExecutionMetrics()
						.mergeExecutionMetrics(completedScriptExecution.getExecutionMetrics());
			} catch (Exception e) {
				Throwable cause = e.getCause();
				this.getExecutionControl().logMessage(this, "route.error=" + cause, Level.INFO);
				continue;
			}
		}
	}

	public void traceDesignMetadata() {
		this.getExecutionControl().getExecutionTrace().setExecution(this);
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


	public ExecutionMetrics getExecutionMetrics() {
		return executionMetrics;
	}

	public Optional<ScriptExecution> getParentScriptExecution() {
		return Optional.ofNullable(parentScriptExecution);
	}

	public ActionSelectOperation getActionSelectOperation() {
		return actionSelectOperation;
	}

	public void setActionSelectOperation(ActionSelectOperation actionSelectOperation) {
		this.actionSelectOperation = actionSelectOperation;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public boolean isExitOnCompletion() {
		return exitOnCompletion;
	}

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public String getEnvironment() {
		return environment;
	}

}