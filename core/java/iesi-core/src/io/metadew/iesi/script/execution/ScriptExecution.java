package io.metadew.iesi.script.execution;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Level;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.script.operation.ActionSelectOperation;
import io.metadew.iesi.script.operation.RouteOperation;

public class ScriptExecution
{

	private Script script;

	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	private ExecutionMetrics executionMetrics;

	private Long processId;

	private boolean rootScript = true;

	private boolean routeScript = false;

	private boolean asynchronously = false;

	private boolean exitOnCompletion = true;

	private ScriptExecution parentScriptExecution;

	private String result;

	private String paramList = "";

	private String paramFile = "";

	private ActionSelectOperation actionSelectOperation;

	// Constructors
	public ScriptExecution()
	{

	}

	public ScriptExecution(FrameworkExecution frameworkExecution, Script script) {
		this.setScript(script);
		this.setFrameworkExecution(frameworkExecution);
	}

	// Methods
	public boolean initializeAsRootScript(String envName) {
		this.setExecutionControl(new ExecutionControl(this.getFrameworkExecution()));
		this.getExecutionControl().setEnvName(envName);
		this.setParentScriptExecution(this.getRootScriptExecution());
		this.setRootScript(true);
		this.setRouteScript(false);
		return true;
	}

	private ScriptExecution getRootScriptExecution()
	{
		ScriptExecution scriptExecution = new ScriptExecution();
		scriptExecution.setProcessId(0L);
		return scriptExecution;
	}

	public boolean initializeAsNonRootExecution(ExecutionControl executionControl, ScriptExecution parentScriptExecution)
	{
		this.setExecutionControl(executionControl);
		this.setParentScriptExecution(parentScriptExecution);
		this.setRootScript(false);
		this.setRouteScript(false);
		return true;
	}

	public boolean initializeAsRouteExecution(ScriptExecution currentScriptExecution)
	{
		this.setExecutionControl(currentScriptExecution.getExecutionControl());
		// tbd
		this.setParentScriptExecution(currentScriptExecution.getParentScriptExecution());
		this.setRootScript(currentScriptExecution.isRootScript());
		this.setActionSelectOperation(currentScriptExecution.getActionSelectOperation());
		this.setExecutionMetrics(currentScriptExecution.getExecutionMetrics()); // ?
		this.setRouteScript(true);
		return true;
	}

	public void setImpersonations(String impersonationName, String impersonationCustom)
	{
		/*
		 * Apply impersonation. The custom input takes priority over the profile
		 */
		if (!impersonationName.equals(""))
		{
			this.getExecutionControl().getExecutionRuntime().setImpersonationName(impersonationName);
		}
		if (!impersonationCustom.equals(""))
		{
			this.getExecutionControl().getExecutionRuntime().setImpersonationCustom(impersonationCustom);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void execute()
	{
		if (!this.isRouteScript())
		{
			/*
			 * Start script execution Not applicable for routing executions
			 */
			this.getExecutionControl().logMessage(this, "script.name=" + this.getScript().getName(), Level.INFO);
			this.getExecutionControl().logMessage(this, "exec.env=" + this.getExecutionControl().getEnvName(), Level.INFO);
			this.getExecutionControl().logStart(this, this.getParentScriptExecution());
			this.setProcessId(this.getExecutionControl().getProcessId());

			/*
			 * Initialize parameters. A parameter file has priority over a parameter list
			 */
			if (!this.getParamFile().trim().equals(""))
			{
				this.getExecutionControl().getExecutionRuntime().loadParamFiles(this.getParamFile());
			}
			if (!this.getParamList().trim().equals(""))
			{
				this.getExecutionControl().getExecutionRuntime().loadParamList(this.getParamList());
			}

			/*
			 * Perform trace of the script design configuration
			 */
			this.traceDesignMetadata();
		}

		/*
		 * Create new metrics object
		 */
		this.setExecutionMetrics(new ExecutionMetrics());

		/*
		 * Loop all actions inside the script
		 */
		boolean execute = true;
		for (Action action : this.getScript().getActions())
		{
			// Check if the action needs to be executed
			if (this.isRootScript())
			{
				if (!this.getActionSelectOperation().getExecutionStatus(action))
				{
					// skip execution
					execute = false;
				}
				else
				{
					execute = true;
				}
			}

			ActionExecution actionExecution = new ActionExecution(this.getFrameworkExecution(), this.getExecutionControl(),
					this, action);
			if (execute) {
				// Route
				if (action.getType().equals("fwk.route")) {
					actionExecution.execute();

					// Create future variables
					int threads = actionExecution.getActionControl().getActionRuntime().getRouteOperations().size();
					CompletionService<ScriptExecution> completionService = new ExecutorCompletionService(
								Executors.newFixedThreadPool(threads));
					Set<Future<ScriptExecution>> futureScriptExecutions = new HashSet<Future<ScriptExecution>>();

					// Submit routes
					for (RouteOperation routeOperation : actionExecution.getActionControl().getActionRuntime().getRouteOperations())
					{
						Callable<ScriptExecution> callableScriptExecution = () -> {
							ScriptExecution scriptExecution = new ScriptExecution(this.getFrameworkExecution(),
									routeOperation.getScript());
							scriptExecution.initializeAsRouteExecution(this);
							scriptExecution.execute();
							return scriptExecution;
						};

						futureScriptExecutions.add(completionService.submit(callableScriptExecution));
					}

					Future<ScriptExecution> completedFuture;
					ScriptExecution completedScriptExecution;
					while (futureScriptExecutions.size() > 0)
					{
						try
						{
							completedFuture = completionService.take();
							futureScriptExecutions.remove(completedFuture);

							completedScriptExecution = completedFuture.get();
							this.getExecutionMetrics().mergeExecutionMetrics(completedScriptExecution.getExecutionMetrics());
						}
						catch (Exception e)
						{
							Throwable cause = e.getCause();
							this.getExecutionControl().logMessage(this, "route.error=" + cause, Level.INFO);
							continue;
						}
					}

					break;
				}
				
				if (action.getType().equalsIgnoreCase("fwk.startIteration")) {
					
					
				}

				// Initialize
				actionExecution.initialize();
				
				// Iteration
				IterationExecution iterationExecution = new IterationExecution();
				if (!action.getIteration().trim().isEmpty()) {
					iterationExecution.initialize(this.getFrameworkExecution(), this.getExecutionControl(),
							this.getExecutionControl().getExecutionRuntime()
									.getIterationOperation(action.getIteration()));
				}
				
				while (iterationExecution.hasNext()) {
					if (iterationExecution.getIterationNumber() > 1) actionExecution.initialize();
					actionExecution.execute();
				}
				
				// Stop on Error
				if (action.getErrorStop().equalsIgnoreCase("y") && action.getErrorExpected().equalsIgnoreCase("n")
							&& actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0)
				{
					this.getExecutionControl().logMessage(this, "action.error -> script.stop", Level.INFO);
					this.getExecutionControl().setActionErrorStop(true);
					break;
				}

			}
			else
			{
				actionExecution.skip();
			}

			// Set status if the next action needs to be executed
			if (this.isRootScript())
			{
				this.getActionSelectOperation().setContinueStatus(action);
			}

		}

		if (!this.isRouteScript())
		{
			/*
			 * Log script end and status
			 */
			this.setResult(this.getExecutionControl().logEnd(this));

			/*
			 * End the execution only in case of a root script
			 */
			if (this.isRootScript())
			{
				this.getExecutionControl().terminate();
				if (this.isExitOnCompletion())
				{
					this.getExecutionControl().endExecution();
				}
			}
		}
	}

	public void traceDesignMetadata()
	{
		this.getExecutionControl().getExecutionTrace().setExecution(this, this.getParentScriptExecution());
	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public ExecutionControl getExecutionControl()
	{
		return executionControl;
	}

	public void setExecutionControl(ExecutionControl executionControl)
	{
		this.executionControl = executionControl;
	}

	public String getParamList()
	{
		return paramList;
	}

	public void setParamList(String paramList)
	{
		this.paramList = paramList;
	}

	public String getParamFile()
	{
		return paramFile;
	}

	public void setParamFile(String paramFile) {
		this.paramFile = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(paramFile);
	}

	public Long getProcessId()
	{
		return processId;
	}

	public void setProcessId(Long processId)
	{
		this.processId = processId;
	}

	public boolean isRootScript()
	{
		return rootScript;
	}

	public void setRootScript(boolean rootScript)
	{
		this.rootScript = rootScript;
	}

	public ExecutionMetrics getExecutionMetrics()
	{
		return executionMetrics;
	}

	public void setExecutionMetrics(ExecutionMetrics executionMetrics)
	{
		this.executionMetrics = executionMetrics;
	}

	public ScriptExecution getParentScriptExecution()
	{
		return parentScriptExecution;
	}

	public void setParentScriptExecution(ScriptExecution parentScriptExecution)
	{
		this.parentScriptExecution = parentScriptExecution;
	}

	public ActionSelectOperation getActionSelectOperation()
	{
		return actionSelectOperation;
	}

	public void setActionSelectOperation(ActionSelectOperation actionSelectOperation)
	{
		this.actionSelectOperation = actionSelectOperation;
	}

	public String getResult()
	{
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

	public boolean isAsynchronously()
	{
		return asynchronously;
	}

	public void setAsynchronously(boolean asynchronously)
	{
		this.asynchronously = asynchronously;
	}

	public boolean isExitOnCompletion()
	{
		return exitOnCompletion;
	}

	public void setExitOnCompletion(boolean exitOnCompletion)
	{
		this.exitOnCompletion = exitOnCompletion;
	}

	public Script getScript()
	{
		return script;
	}

	public void setScript(Script script)
	{
		this.script = script;
	}

	public boolean isRouteScript()
	{
		return routeScript;
	}

	public void setRouteScript(boolean routeScript)
	{
		this.routeScript = routeScript;
	}

}