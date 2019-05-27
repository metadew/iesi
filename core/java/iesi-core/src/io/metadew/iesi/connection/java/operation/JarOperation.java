package io.metadew.iesi.connection.java.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.java.tools.JarTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.JavaArchive;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class JarOperation {

	private FrameworkExecution frameworkExecution;

	public JarOperation() {
	}

	public JavaArchive getJavaArchiveDefinition(String fileName) {
		JavaArchive javaArchive = null;
		try {
			File file = new File(fileName);
			URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { file.toURI().toURL() },
					JarTools.class.getClassLoader());
			
			javaArchive = JarTools.getJavaArchiveDefinition(fileName, urlClassLoader);
			ObjectMapper mapper = new ObjectMapper();
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(javaArchive));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return javaArchive;
	}
	
	public void storeJavaArchiveDefinition(String connectionName) {

	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}