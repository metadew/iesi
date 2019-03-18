package io.metadew.iesi.metadata.operation;

import java.io.File;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.operation.FrameworkPluginOperation;

public class TypeConfigurationOperation {

	public TypeConfigurationOperation() {

	}

	public static String getTypeConfigurationFile(FrameworkExecution frameworkExecution, String dataObjectType,
			String typeName) {
		String configurationObject = dataObjectType + File.separator + typeName + ".json";
		String conf = frameworkExecution.getFrameworkConfiguration().getFolderConfiguration()
				.getFolderAbsolutePath("metadata.conf") + File.separator + configurationObject;
		
		if (!FileTools.exists(conf)) {
			FrameworkPluginOperation frameworkPluginOperation = new FrameworkPluginOperation(frameworkExecution);
			if (frameworkPluginOperation.verifyPlugins(configurationObject)) {
				conf = frameworkPluginOperation.getPluginConfigurationFile();
			} else {
				throw new RuntimeException("action.type.notfound");
			}
		}

		return conf;
	}

}