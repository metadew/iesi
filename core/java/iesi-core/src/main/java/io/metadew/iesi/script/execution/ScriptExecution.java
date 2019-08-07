package io.metadew.iesi.script.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.script.action.FwkIncludeScript;
import io.metadew.iesi.script.operation.ActionSelectOperation;
import io.metadew.iesi.script.operation.RouteOperation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

public abstract class ScriptExecution {
	private RootingStrategy rootingStrategy;
	private Script script;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private ExecutionMetrics executionMetrics;
	private Long processId;
	private boolean exitOnCompletion = true;
	private ScriptExecution parentScriptExecution;
	private String result;
	private String paramList = "";
	private String paramFile = "";
	private ActionSelectOperation actionSelectOperation;
	private Marker SCRIPT;

	public ScriptExecution(Script script, FrameworkExecution frameworkExecution, ExecutionControl executionControl,
						   ExecutionMetrics executionMetrics, Long processId, boolean exitOnCompletion,
						   ScriptExecution parentScriptExecution, String paramList, String paramFile,
						   ActionSelectOperation actionSelectOperation, RootingStrategy rootingStrategy) {
		this.script = script;
		this.frameworkExecution = frameworkExecution;
		this.executionControl = executionControl;
		this.executionMetrics = executionMetrics;
		this.processId = processId;
		this.exitOnCompletion = exitOnCompletion;
		this.parentScriptExecution = parentScriptExecution;
		this.paramList = paramList;
		this.paramFile = paramFile;
		this.actionSelectOperation = actionSelectOperation;
		this.rootingStrategy = rootingStrategy;
	}

	public void setImpersonations(String impersonationName, String impersonationCustom) {
		/*
		 * Apply impersonation. The custom input takes priority over the profile
		 */
		if (!impersonationName.equals("")) {
			this.getExecutionControl().getExecutionRuntime().setImpersonationName(impersonationName);
		}
		if (!impersonationCustom.equals("")) {
			this.getExecutionControl().getExecutionRuntime().setImpersonationCustom(impersonationCustom);
		}
	}

	public void execute() {
		rootingStrategy.prepareExecution(this);
		prepareExecution();

		List<Action> actionsToExecute = script.getActions();
		int actionIndex = 0;

		while (actionIndex < actionsToExecute.size()) {
			Action action = actionsToExecute.get(actionIndex);

			ActionExecution actionExecution = new ActionExecution(frameworkExecution, executionControl, this, action);

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
				iterationExecution.initialize(frameworkExecution, executionControl, actionExecution, action.getIteration());
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

	protected abstract void prepareExecution();

	public RootingStrategy getRootingStrategy() {
		return rootingStrategy;
	}

//		/*
//		 * Loop all actions inside the script
//		 */
//		boolean execute = true;
//		this.setActions(this.getScript().getActions());
//		for (int i = 0; i < this.getActions().size(); i++) {
//			Action action = this.getActions().get(i);
//			// Check if the action needs to be executed
//			if (this.isRootScript()) {
//				if (!this.getActionSelectOperation().getExecutionStatus(action)) {
//					// skip execution
//					System.out.println("Skipping " + action.getName());
//					execute = false;
//				} else {
//					execute = true;
//				}
//			}
//
//			ActionExecution actionExecution = new ActionExecution(this.getFrameworkExecution(),
//					this.getExecutionControl(), this, action);
//			if (execute) {
//				// Route
//				if (action.getType().equalsIgnoreCase("fwk.route")) {
//					executeFwkRouteAction(actionExecution);
//					break;
//				}
//
//				if (action.getType().equalsIgnoreCase("fwk.startIteration")) {
//					// Do not change - work in progress
//				}
//
//				// Initialize
//				actionExecution.initialize();
//
//				// Iteration
//				IterationExecution iterationExecution = new IterationExecution();
//				if (action.getIteration() != null && !action.getIteration().trim().isEmpty()) {
//					iterationExecution.initialize(frameworkExecution, executionControl, actionExecution, action.getIteration());
//				}
//
//
//
//
//
//
//				// Get retry input
//				long retriesInput = 0;
//				long retriesLeft = 0;
//				try {
//					if (action.getRetries().isEmpty()) {
//						retriesInput = 0;
//						retriesLeft = 0;
//					} else {
//						retriesInput = Long.parseLong(action.getRetries());
//						retriesLeft = retriesInput + 1;
//						this.getExecutionControl().logMessage(this, "action.retries.input=" + retriesInput, Level.DEBUG);
//					}
//				} catch (Exception e) {
//					retriesInput = 0;
//					retriesLeft = 0;
//					this.getExecutionControl().logMessage(this, "action.retries.error -> ignoring", Level.INFO);
//				}
//
//				// Execute with iterations and retries
//				while (iterationExecution.hasNext()) {
//
//					// Retry on Error
//					boolean retryOnError = true;
//					long retries = 0;
//
//					while (retryOnError) {
//						if (retries > 0) {
//							this.getExecutionControl().logMessage(this, "action.retry." + retries, Level.INFO);
//						}
//
//						if (iterationExecution.getIterationNumber() > 1 || retries > 0)
//							actionExecution.initialize();
//
//						actionExecution.execute(iterationExecution.getIterationInstance());
//
//						if (!iterationExecution.isIterationOff()) {
//							if (iterationExecution.getIterationOperation().getIteration().getInterrupt()
//									.equalsIgnoreCase("y")) {
//								if (actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0) {
//									break;
//								}
//							}
//						}
//
//						if (actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0 && retriesLeft > 0) {
//							if (action.getErrorStop().equalsIgnoreCase("y")) {
//								this.getExecutionControl().logMessage(this, "action.error -> retries.ignore", Level.INFO);
//								this.getExecutionControl().setActionErrorStop(true);
//								break;
//							}
//
//							retriesLeft--;
//							if (retriesLeft == 0) {
//								retryOnError = false;
//							} else {
//								retryOnError = true;
//								retries++;
//							}
//						} else {
//							retryOnError = false;
//						}
//					}
//				}
//
//				// Include script
//				if (action.getType().equalsIgnoreCase("fwk.includeScript")) {
//					ObjectMapper objectMapper = new ObjectMapper();
//					FwkIncludeScript fwkIncludeScript = objectMapper
//							.convertValue(actionExecution.getActionTypeExecution(), FwkIncludeScript.class);
//
//					List<Action> includeActions = new ArrayList();
//					// Subselect the past actions including the include action itself
//					includeActions.addAll(this.getActions().subList(0, i + 1));
//					// Add the include script
//					includeActions.addAll(fwkIncludeScript.getScript().getActions());
//					// If not at the end of the script, add the remainder of actions
//					if (i < this.getActions().size() - 1) {
//						includeActions.addAll(this.getActions().subList(i + 1, this.getActions().size()));
//					}
//
//					// Adjust the action list that is iterated over
//					this.setActions(includeActions);
//				}
//
//				// Error handling
//				// Check if iteration condition has not prevented execution
//				if (actionExecution.isExecuted()) {
//					if (actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0) {
//						if (action.getErrorStop().equalsIgnoreCase("y")) {
//							this.getExecutionControl().logMessage(this, "action.error -> script.stop", Level.INFO);
//							this.getExecutionControl().setActionErrorStop(true);
//							break;
//						}
//					}
//				} else {
//					this.getExecutionMetrics().increaseWarningCount(1);
//					this.getExecutionControl().logMessage(this, "action.warning -> iteration.condition.block",
//							Level.INFO);
//				}
//
//				// Exit script
//				if (action.getType().equalsIgnoreCase("fwk.exitScript")) {
//					this.getExecutionControl().logMessage(this, "script.exit", Level.INFO);
//					this.getExecutionControl().setScriptExit(true);
//					break;
//				}
//			} else {
//				actionExecution.skip();
//			}
//
//			// Set status if the next action needs to be executed
//			if (this.isRootScript()) {
//				this.getActionSelectOperation().setContinueStatus(action);
//
//			}
//		}
//
//		if (!this.isRouteScript()) {
//			/*
//			 * Log script end and status
//			 */
//			this.setResult(this.getExecutionControl().endExecution(this));
//
//			/*
//			 * End the execution only in case of a root script
//			 */
//			if (this.isRootScript()) {
//				this.getExecutionControl().terminate();
//				if (this.isExitOnCompletion()) {
//					this.getExecutionControl().endExecution();
//				}
//			} else {
//                // TODO: Review
//                executionControl.setActionErrorStop(false);
//            }
//		}
//
//	}

	private void executeFwkIncludeAction(ActionExecution actionExecution, List<Action> actionsToExecute, int actionIndex) {
		ObjectMapper objectMapper = new ObjectMapper();
		FwkIncludeScript fwkIncludeScript = objectMapper
				.convertValue(actionExecution.getActionTypeExecution(), FwkIncludeScript.class);
		actionsToExecute.addAll(actionIndex, fwkIncludeScript.getScript().getActions());
	}

	private void executeFwkRouteAction(ActionExecution actionExecution) {
		actionExecution.execute(null);

		// Create future variables
		int threads = actionExecution.getActionControl().getActionRuntime().getRouteOperations().size();
		CompletionService<ScriptExecution> completionService = new ExecutorCompletionService(Executors.newFixedThreadPool(threads));
		Set<Future<ScriptExecution>> futureScriptExecutions = new HashSet<>();

		// Submit routes
		for (RouteOperation routeOperation : actionExecution.getActionControl().getActionRuntime()
				.getRouteOperations()) {

			Callable<ScriptExecution> callableScriptExecution = () -> new ScriptExecutionBuilder(true, true)
					.frameworkExecution(frameworkExecution)
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

	public String getParamList() {
		return paramList;
	}

	public void setParamList(String paramList) {
		this.paramList = paramList;
	}

	public String getParamFile() {
		return paramFile;
	}

	public void setParamFile(String paramFile) {
		this.paramFile = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(paramFile);
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

	public void setExecutionMetrics(ExecutionMetrics executionMetrics) {
		this.executionMetrics = executionMetrics;
	}

	public Optional<ScriptExecution> getParentScriptExecution() {
		return Optional.ofNullable(parentScriptExecution);
	}

	public void setParentScriptExecution(ScriptExecution parentScriptExecution) {
		this.parentScriptExecution = parentScriptExecution;
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

    public void setExitOnCompletion(boolean exitOnCompletion) {
        this.exitOnCompletion = exitOnCompletion;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

}