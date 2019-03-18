package io.metadew.iesi.script.operation;

import java.util.HashMap;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.ComponentParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;

/**
 * Operation that manages http requests that have been defined as components.
 * 
 * @author peter.billen
 *
 */
public class RequestOperation
{

	private FrameworkExecution frameworkExecution;

	private ExecutionControl executionControl;

	private ActionExecution actionExecution;

	private String requestName;

	private Component request;

	// parameters
	private RequestParameterOperation url;

	private HashMap<String, RequestParameterOperation> headerMap;

	private HashMap<String, RequestParameterOperation> queryParamMap;

	private HashMap<String, RequestParameterOperation> requestParameterOperationMap;

	// Constructors
	public RequestOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ActionExecution actionExecution,
				String requestName)
	{
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setActionExecution(actionExecution);
		this.setRequestParameterOperationMap(new HashMap<String, RequestParameterOperation>());
		this.setRequestName(requestName);
		this.getRequestConfiguration();
	}

	private void getRequestConfiguration()
	{
		ComponentConfiguration componentConfiguration = new ComponentConfiguration(this.getFrameworkExecution());
		this.setRequest(componentConfiguration.getComponent(this.getRequestName()));

		// Reset parameters
		this.setUrl(new RequestParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
					this.getRequest().getAttributes(), "url"));
		this.setHeaderMap(new HashMap<String, RequestParameterOperation>());
		this.setQueryParamMap(new HashMap<String, RequestParameterOperation>());

		// Get Parameters
		for (ComponentParameter componentParameter : this.getRequest().getParameters())
		{
			if (componentParameter.getName().equalsIgnoreCase("url"))
			{
				this.getUrl().setInputValue(componentParameter.getValue());
			}
			else if (componentParameter.getName().toLowerCase().startsWith("header"))
			{
				RequestParameterOperation requestParameterOperation = new RequestParameterOperation(this.getFrameworkExecution(),
							this.getExecutionControl(), this.getActionExecution(), this.getRequest().getAttributes(),
							componentParameter.getName());
				requestParameterOperation.setInputValue(componentParameter.getValue());
				this.getHeaderMap().put(componentParameter.getName(), requestParameterOperation);
			}
			else if (componentParameter.getName().toLowerCase().startsWith("queryparam"))
			{
				RequestParameterOperation requestParameterOperation = new RequestParameterOperation(this.getFrameworkExecution(),
							this.getExecutionControl(), this.getActionExecution(), this.getRequest().getAttributes(),
							componentParameter.getName());
				requestParameterOperation.setInputValue(componentParameter.getValue());
				this.getQueryParamMap().put(componentParameter.getName(), requestParameterOperation);
			}
		}

		// Create parameter list
		this.getRequestParameterOperationMap().put("url", this.getUrl());

	}

	// Getters and setters
	public FrameworkExecution getFrameworkExecution()
	{
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution)
	{
		this.frameworkExecution = frameworkExecution;
	}

	public String getRequestName()
	{
		return requestName;
	}

	public void setRequestName(String requestName)
	{
		this.requestName = requestName;
	}

	public Component getRequest()
	{
		return request;
	}

	public void setRequest(Component request)
	{
		this.request = request;
	}

	public RequestParameterOperation getUrl()
	{
		return url;
	}

	public void setUrl(RequestParameterOperation url)
	{
		this.url = url;
	}

	public ExecutionControl getExecutionControl()
	{
		return executionControl;
	}

	public void setExecutionControl(ExecutionControl executionControl)
	{
		this.executionControl = executionControl;
	}

	public ActionExecution getActionExecution()
	{
		return actionExecution;
	}

	public void setActionExecution(ActionExecution actionExecution)
	{
		this.actionExecution = actionExecution;
	}

	public HashMap<String, RequestParameterOperation> getRequestParameterOperationMap()
	{
		return requestParameterOperationMap;
	}

	public void setRequestParameterOperationMap(HashMap<String, RequestParameterOperation> requestParameterOperationMap)
	{
		this.requestParameterOperationMap = requestParameterOperationMap;
	}

	public HashMap<String, RequestParameterOperation> getHeaderMap()
	{
		return headerMap;
	}

	public void setHeaderMap(HashMap<String, RequestParameterOperation> headerMap)
	{
		this.headerMap = headerMap;
	}

	public HashMap<String, RequestParameterOperation> getQueryParamMap()
	{
		return queryParamMap;
	}

	public void setQueryParamMap(HashMap<String, RequestParameterOperation> queryParamMap)
	{
		this.queryParamMap = queryParamMap;
	}

}