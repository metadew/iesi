package io.metadew.iesi.script.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.script.ScriptLog;

public class ExecutionLog {

    // Constructors
    public ExecutionLog() {
    }

    // Insert
    @SuppressWarnings("unused")
    public void setLog(ScriptLog scriptLog) {
        ObjectMapper mapper = new ObjectMapper();
        try {

//			for (MetadataRepositoryConfigurationBack metadataRepositoryConfiguration : this.getFrameworkExecution()
//					.getFrameworkControl().getMetadataRepositoryConfigurations()) {
//
//				if (metadataRepositoryConfiguration.getType().equalsIgnoreCase("elasticsearch")) {
//					metadataRepositoryConfiguration.getElasticsearchConnection()
//							.putStringEntity(mapper.writeValueAsString(scriptLog), this.getFrameworkExecution().getMetadataControl().getMonitorMetadataRepository().getMetadataTableConfiguration().getTableName("ScriptResults").toLowerCase(), scriptLog.getRun());
//				}
//
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}