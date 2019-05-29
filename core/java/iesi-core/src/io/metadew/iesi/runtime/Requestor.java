package io.metadew.iesi.runtime;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.RequestConfiguration;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestParameter;

public class Requestor {

	private static Requestor INSTANCE;
	private FrameworkInstance frameworkInstance;

	public Requestor(FrameworkInstance frameworkInstance) {
		this.setFrameworkInstance(frameworkInstance);
	}

	public synchronized static Requestor getInstance(FrameworkInstance frameworkInstance) {
		if (INSTANCE == null) {
			INSTANCE = new Requestor(frameworkInstance);
		}
		return INSTANCE;
	}

	public synchronized String submit(Request request) {
    	RequestConfiguration requestConfiguration = new RequestConfiguration(this.getFrameworkInstance());
    	
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

		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(requestConfiguration.getInsertStatement(request));
    	
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
