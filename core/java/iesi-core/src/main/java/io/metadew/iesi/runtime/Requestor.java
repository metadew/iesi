package io.metadew.iesi.runtime;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.RequestConfiguration;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestParameter;
import io.metadew.iesi.server.execution.tools.ExecutionServerTools;

public class Requestor {

	private static Requestor INSTANCE;
	private FrameworkInstance frameworkInstance;

	public Requestor() {}

	public synchronized static Requestor getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Requestor();
		}
		return INSTANCE;
	}

	public void init(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

	public synchronized String submit(Request request) {
    	if (!ExecutionServerTools.isAlive()) {
    		throw new RuntimeException("framework.server.down");
    	}
		
		RequestConfiguration requestConfiguration = new RequestConfiguration();
    	
    	boolean exitOverwrite = false;
		for (RequestParameter requestParameter : request.getParameters()) {
			if (requestParameter.getType().equalsIgnoreCase("exit")) {
				requestParameter.setValue(Boolean.toString(false));
				exitOverwrite = true;
			}
		}
		if (!exitOverwrite) {
	    	request.getParameters().add(new RequestParameter("exit","flag",Boolean.toString(false)));
		}

		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeBatch(requestConfiguration.getInsertStatement(request));
    	
		return request.getId();
    }

	// Getters and setters
	public synchronized FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public synchronized void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}
