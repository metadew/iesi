//package io.metadew.iesi.script.operation;
//
//import java.io.File;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import io.metadew.iesi.connection.database.SqliteDatabaseConnection;
//import io.metadew.iesi.connection.database.TemporaryDatabaseConnection;
//import io.metadew.iesi.connection.tools.FolderTools;
//import io.metadew.iesi.framework.execution.FrameworkExecution;
//
///**
// * Operation to manage stage items that have been defined in the script.
// *
// * @author peter.billen
// *
// */
//public class StageOperation {
//
//	private FrameworkExecution frameworkExecution;
//	private TemporaryDatabaseConnection stageConnection;
//	private String stageName;
//
//
//	// Constructors
//	public StageOperation(FrameworkExecution frameworkExecution, String stageName) {
//		this.setFrameworkExecution(frameworkExecution);
//		this.setStageName(stageName);
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		String stageFolderName = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.tmp") + File.separator +  "stage";
//		FolderTools.createFolder(stageFolderName);
//		String stageFileName = this.getStageName() + ".db3";
//		SqliteDatabaseConnection dcSQConnection = new SqliteDatabaseConnection(stageFolderName + File.separator + stageFileName);
//		this.setStageConnection(objectMapper.convertValue(dcSQConnection, TemporaryDatabaseConnection.class));
//	}
//
//
//	// Getters and setters
//	public FrameworkExecution getFrameworkExecution() {
//		return frameworkExecution;
//	}
//
//
//	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
//		this.frameworkExecution = frameworkExecution;
//	}
//
//
//	public TemporaryDatabaseConnection getStageConnection() {
//		return stageConnection;
//	}
//
//
//	public void setStageConnection(TemporaryDatabaseConnection stageConnection) {
//		this.stageConnection = stageConnection;
//	}
//
//
//	public String getStageName() {
//		return stageName;
//	}
//
//
//	public void setStageName(String stageName) {
//		this.stageName = stageName;
//	}
//
//}