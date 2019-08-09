package io.metadew.iesi.script.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.script.operation.ActionSelectOperation;

public class RouteScriptExecution extends ScriptExecution {

    public RouteScriptExecution(Script script, FrameworkExecution frameworkExecution, ExecutionControl executionControl, ExecutionMetrics executionMetrics, Long processId, boolean exitOnCompletion, ScriptExecution parentScriptExecution, String paramList, String paramFile, ActionSelectOperation actionSelectOperation, RootingStrategy rootingStrategy) {
        super(script, frameworkExecution, executionControl, executionMetrics, processId, exitOnCompletion, parentScriptExecution, paramList, paramFile, actionSelectOperation, rootingStrategy);
    }

    @Override
    protected void endExecution() {

    }

    @Override
    protected void prepareExecution() {

    }


//
//    if (!this.isRouteScript()) {
//        /*
//         * Start script execution Not applicable for routing executions
//         */
//        this.getExecutionControl().logMessage(this, "script.name=" + this.get().getName(), Level.INFO);
//        this.getExecutionControl().logMessage(this, "exec.env=" + this.getExecutionControl().getEnvName(),
//                Level.INFO);
//        this.getExecutionControl().logStart(this, this.getParentScriptExecution());
//
//        /*
//         * Initialize parameters. A parameter file has priority over a parameter list
//         */
//        if (!this.getParamFile().trim().equals("")) {
//            this.getExecutionControl().getExecutionRuntime().loadParamFiles(this, this.getParamFile());
//        }
//        if (!this.getParamList().trim().equals("")) {
//            this.getExecutionControl().getExecutionRuntime().loadParamList(this, this.getParamList());
//        }
//
//        /*
//         * Perform trace of the script design configuration
//         */
//        this.traceDesignMetadata();
//    }
//
//    /*
//     * Create new metrics object
//     */
//		this.setExecutionMetrics(new ExecutionMetrics());
//
//    /*
//     * Loop all actions inside the script
//     */
//    boolean execute = true;
//		this.setActions(this.get().getActions());
//		for (int i = 0; i < this.getActions().size(); i++) {
//        Action action = this.getActions().get(i);
//        // Check if the action needs to be executed
//        if (this.isRootScript()) {
//            if (!this.getActionSelectOperation().getExecutionStatus(action)) {
//                // skip execution
//                System.out.println("Skipping " + action.getName());
//                execute = false;
//            } else {
//                execute = true;
//            }
//        }
//
//        ActionExecution actionExecution = new ActionExecution(this.getFrameworkExecution(),
//                this.getExecutionControl(), this, action);
//        if (execute) {
//            // Route
//            if (action.getType().equalsIgnoreCase("fwk.route")) {
//                actionExecution.execute(null);
//
//                // Create future variables
//                int threads = actionExecution.getActionControl().getActionRuntime().getRouteOperations().size();
//                CompletionService<ScriptExecution> completionService = new ExecutorCompletionService(
//                        Executors.newFixedThreadPool(threads));
//                Set<Future<ScriptExecution>> futureScriptExecutions = new HashSet<Future<ScriptExecution>>();
//
//                // Submit routes
//                for (RouteOperation routeOperation : actionExecution.getActionControl().getActionRuntime()
//                        .getRouteOperations()) {
//                    Callable<ScriptExecution> callableScriptExecution = () -> {
//                        ScriptExecution scriptExecution = new ScriptExecution(this.getFrameworkExecution(),
//                                routeOperation.get());
//                        scriptExecution.initializeAsRouteExecution(this);
//                        scriptExecution.execute();
//                        return scriptExecution;
//                    };
//
//                    futureScriptExecutions.add(completionService.submit(callableScriptExecution));
//                }
//
//                Future<ScriptExecution> completedFuture;
//                ScriptExecution completedScriptExecution;
//                while (futureScriptExecutions.size() > 0) {
//                    try {
//                        completedFuture = completionService.take();
//                        futureScriptExecutions.remove(completedFuture);
//
//                        completedScriptExecution = completedFuture.get();
//                        this.getExecutionMetrics()
//                                .mergeExecutionMetrics(completedScriptExecution.getExecutionMetrics());
//                    } catch (Exception e) {
//                        Throwable cause = e.getCause();
//                        this.getExecutionControl().logMessage(this, "route.error=" + cause, Level.INFO);
//                        continue;
//                    }
//                }
//
//                break;
//            }
//
//            if (action.getType().equalsIgnoreCase("fwk.startIteration")) {
//                // Do not change - work in progress
//            }
//
//            // Initialize
//            actionExecution.initialize();
//
//            // Iteration
//            IterationExecution iterationExecution = new IterationExecution();
//            if (action.getIteration() != null && !action.getIteration().trim().isEmpty()) {
//                iterationExecution.initialize(this.getFrameworkExecution(), this.getExecutionControl(), actionExecution,
//                        action.getIteration());
//            }
//
//            // Get retry input
//            long retriesInput = 0;
//            long retriesLeft = 0;
//            try {
//                if (action.getRetries().isEmpty()) {
//                    retriesInput = 0;
//                    retriesLeft = 0;
//                } else {
//                    retriesInput = Long.parseLong(action.getRetries());
//                    retriesLeft = retriesInput + 1;
//                    this.getExecutionControl().logMessage(this, "action.retries.input=" + retriesInput, Level.DEBUG);
//                }
//            } catch (Exception e) {
//                retriesInput = 0;
//                retriesLeft = 0;
//                this.getExecutionControl().logMessage(this, "action.retries.error -> ignoring", Level.INFO);
//            }
//
//            // Execute with iterations and retries
//            while (iterationExecution.hasNext()) {
//
//                // Retry on Error
//                boolean retryOnError = true;
//                long retries = 0;
//
//                while (retryOnError) {
//                    if (retries > 0) {
//                        this.getExecutionControl().logMessage(this, "action.retry." + retries, Level.INFO);
//                    }
//
//                    if (iterationExecution.getIterationNumber() > 1 || retries > 0)
//                        actionExecution.initialize();
//
//                    actionExecution.execute(iterationExecution.getIterationInstance());
//
//                    if (!iterationExecution.isIterationOff()) {
//                        if (iterationExecution.getIterationOperation().getIteration().getInterrupt()
//                                .equalsIgnoreCase("y")) {
//                            if (actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0) {
//                                break;
//                            }
//                        }
//                    }
//
//                    if (actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0 && retriesLeft > 0) {
//                        if (action.getErrorStop().equalsIgnoreCase("y")) {
//                            this.getExecutionControl().logMessage(this, "action.error -> retries.ignore", Level.INFO);
//                            this.getExecutionControl().setActionErrorStop(true);
//                            break;
//                        }
//
//                        retriesLeft--;
//                        if (retriesLeft == 0) {
//                            retryOnError = false;
//                        } else {
//                            retryOnError = true;
//                            retries++;
//                        }
//                    } else {
//                        retryOnError = false;
//                    }
//                }
//            }
//
//            // Include script
//            if (action.getType().equalsIgnoreCase("fwk.includeScript")) {
//                ObjectMapper objectMapper = new ObjectMapper();
//                FwkIncludeScript fwkIncludeScript = objectMapper
//                        .convertValue(actionExecution.getActionTypeExecution(), FwkIncludeScript.class);
//
//                List<Action> includeActions = new ArrayList();
//                // Subselect the past actions including the include action itself
//                includeActions.addAll(this.getActions().subList(0, i + 1));
//                // Add the include script
//                includeActions.addAll(fwkIncludeScript.get().getActions());
//                // If not at the end of the script, add the remainder of actions
//                if (i < this.getActions().size() - 1) {
//                    includeActions.addAll(this.getActions().subList(i + 1, this.getActions().size()));
//                }
//
//                // Adjust the action list that is iterated over
//                this.setActions(includeActions);
//            }
//
//            // Error handling
//            // Check if iteration condition has not prevented execution
//            if (actionExecution.isExecuted()) {
//                if (actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0) {
//                    if (action.getErrorStop().equalsIgnoreCase("y")) {
//                        this.getExecutionControl().logMessage(this, "action.error -> script.stop", Level.INFO);
//                        this.getExecutionControl().setActionErrorStop(true);
//                        break;
//                    }
//                }
//            } else {
//                this.getExecutionMetrics().increaseWarningCount(1);
//                this.getExecutionControl().logMessage(this, "action.warning -> iteration.condition.block",
//                        Level.INFO);
//            }
//
//            // Exit script
//            if (action.getType().equalsIgnoreCase("fwk.exitScript")) {
//                this.getExecutionControl().logMessage(this, "script.exit", Level.INFO);
//                this.getExecutionControl().setScriptExit(true);
//                break;
//            }
//        } else {
//            actionExecution.skip();
//        }
//
//        // Set status if the next action needs to be executed
//        if (this.isRootScript()) {
//            this.getActionSelectOperation().setContinueStatus(action);
//
//        }
//    }
//
//		if (!this.isRouteScript()) {
//        /*
//         * Log script end and status
//         */
//        this.setResult(this.getExecutionControl().endExecution(this));
//
//        /*
//         * End the execution only in case of a root script
//         */
//        if (this.isRootScript()) {
//            this.getExecutionControl().terminate();
//            if (this.isExitOnCompletion()) {
//                this.getExecutionControl().endExecution();
//            }
//        } else {
//            // TODO: Review
//            executionControl.setActionErrorStop(false);
//        }
//    }
}
