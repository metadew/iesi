package io.metadew.iesi.script.execution;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.ScriptLog;

public class ExecutionLog {

	private FrameworkExecution frameworkExecution;

	// Constructors
	public ExecutionLog(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public void setLog(ScriptLog scriptLog) {
		ObjectMapper mapper = new ObjectMapper();
		try {

			for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : this.getFrameworkExecution()
					.getFrameworkControl().getMetadataRepositoryConfigurationList()) {

				if (metadataRepositoryConfiguration.getType().equalsIgnoreCase("elasticsearch")) {
					metadataRepositoryConfiguration.getElasticsearchConnection()
							.putStringEntity(mapper.writeValueAsString(scriptLog), this.getFrameworkExecution().getMetadataControl().getMonitorRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ScriptResults").toLowerCase(), scriptLog.getRun());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}