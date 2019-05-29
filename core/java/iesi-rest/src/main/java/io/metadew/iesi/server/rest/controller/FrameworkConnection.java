package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.execution.FrameworkExecutionSettings;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Context;

public class FrameworkConnection {

	private static final FrameworkConnection instance = new FrameworkConnection();
	private FrameworkExecution frameworkExecution;

	public FrameworkConnection() {
		Context context = new Context();
		context.setName("restserver");
		context.setScope("");
		FrameworkExecutionContext frameworkExecutionContext = new FrameworkExecutionContext(context);
		FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
		frameworkInitializationFile.setName("");
		this.setFrameworkExecution(new FrameworkExecution(new FrameworkInstance(frameworkInitializationFile), frameworkExecutionContext, frameworkInitializationFile));
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public static FrameworkConnection getInstance() {
		return instance;
	}
}
