package io.metadew.iesi.runtime;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.launch.operation.ScriptLaunchOperation;
import io.metadew.iesi.metadata.definition.Request;

public class Executor {

	private static Executor INSTANCE;
	private FrameworkInstance frameworkInstance;
	
    public Executor(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public synchronized static Executor getInstance(FrameworkInstance frameworkInstance) {
        if (INSTANCE == null) {
            INSTANCE = new Executor(frameworkInstance);
        }
        return INSTANCE;
    }

    public synchronized String execute(Request request) {
    	if (request.getType() != null) {
    		switch (request.getType()) {
    		case "script":
    			ScriptLaunchOperation.execute(this.getFrameworkInstance(), request);
    			break;
    		default:
    			throw new RuntimeException("Request type is not supported");
    		}
    	} else {
    		throw new RuntimeException("Empty request submitted for execution");
    	}
    	
    	return "";
    }
    
    // Getters and setters
	public synchronized FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public synchronized void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}


}
