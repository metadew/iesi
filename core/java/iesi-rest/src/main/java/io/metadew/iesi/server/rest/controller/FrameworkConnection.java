package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.instance.FrameworkInstance;

public class FrameworkConnection {

	private static final FrameworkConnection instance = new FrameworkConnection();
	private FrameworkInstance frameworkInstance;

	public FrameworkConnection() {
		FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
		frameworkInitializationFile.setName("");
		this.setFrameworkInstance(new FrameworkInstance(frameworkInitializationFile));
	}

	public static FrameworkConnection getInstance() {
		return instance;
	}

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}
}
