package io.metadew.iesi.script.execution;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.RuntimeActionCacheConfiguration;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.RouteOperation;

public class ActionRuntime {

	private FrameworkExecution frameworkExecution;
	private RuntimeActionCacheConfiguration runtimeActionCacheConfiguration;
	private String runCacheFolderName;
	private String runId;
	private Long processId;
	private List<RouteOperation> routeOperations;

	public ActionRuntime(FrameworkExecution frameworkExecution, String runId, long processId) {
		this.setFrameworkExecution(frameworkExecution);
		this.setRunId(runId);
		this.setProcessId(processId);
	}

	// Methods
	public void initActionCache(String actionName, String runCacheFolderName) {
		this.setRunCacheFolderName(runCacheFolderName + File.separator + this.getProcessId());
		FolderTools.createFolder(this.getRunCacheFolderName());
		this.setRuntimeActionCacheConfiguration(
				new RuntimeActionCacheConfiguration(this.getFrameworkExecution(), this.getRunCacheFolderName()));
	}

	@SuppressWarnings("rawtypes")
	public void setRuntimeParameters(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
		Iterator iterator = actionParameterOperationMap.entrySet().iterator();
		ObjectMapper objectMapper = new ObjectMapper();
		while (iterator.hasNext()) {
			Map.Entry pair = (Map.Entry) iterator.next();
			ActionParameterOperation actionParameterOperation = objectMapper.convertValue(pair.getValue(),
					ActionParameterOperation.class);

			// Handle null values when parameter has not been set
			if (actionParameterOperation == null)
				continue;

			this.getRuntimeActionCacheConfiguration().setRuntimeCache(this.getRunId(),"param",
					actionParameterOperation.getName(), actionParameterOperation.getValue());
			iterator.remove(); // avoids a ConcurrentModificationException
		}

	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public String getRunCacheFolderName() {
		return runCacheFolderName;
	}

	public void setRunCacheFolderName(String runCacheFolderName) {
		this.runCacheFolderName = runCacheFolderName;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public RuntimeActionCacheConfiguration getRuntimeActionCacheConfiguration() {
		return runtimeActionCacheConfiguration;
	}

	public void setRuntimeActionCacheConfiguration(RuntimeActionCacheConfiguration runtimeActionCacheConfiguration) {
		this.runtimeActionCacheConfiguration = runtimeActionCacheConfiguration;
	}

	public List<RouteOperation> getRouteOperations() {
		return routeOperations;
	}

	public void setRouteOperations(List<RouteOperation> routeOperations) {
		this.routeOperations = routeOperations;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}
}