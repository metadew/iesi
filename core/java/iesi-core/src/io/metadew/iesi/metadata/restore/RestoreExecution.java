package io.metadew.iesi.metadata.restore;

import io.metadew.iesi.connection.FileConnection;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class RestoreExecution {

	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private Long processId;

	// Constructors
	public RestoreExecution() {
		// Create the framework instance
		FrameworkInstance frameworkInstance = new FrameworkInstance();

		// Create the framework execution
		Context context = new Context();
		context.setName("restore");
		context.setScope("");
		this.setFrameworkExecution(new FrameworkExecution(frameworkInstance, new FrameworkExecutionContext(context), null));
		this.setExecutionControl(new ExecutionControl(this.getFrameworkExecution()));
	}

	// Methods
	@SuppressWarnings("rawtypes")
	public void execute(String path) {
		// Log Start
		this.getExecutionControl().logStart(this);
		this.setProcessId(0L);

		// Verify input parameters
		if (FolderTools.isFolder(path) ) {
			this.getFrameworkExecution().getFrameworkLog().log("restore.error.path.isFolder", Level.DEBUG);
			// Get source configuration
			@SuppressWarnings("unchecked")
			List<FileConnection> fileConnectionList = FolderTools.getConnectionsInFolder(path, "all", "", new ArrayList());
			for (FileConnection fileConnection : fileConnectionList) {
				RestoreTargetOperation restoreTargetOperation = new RestoreTargetOperation(this.getFrameworkExecution(), this.getExecutionControl());
				restoreTargetOperation.execute(fileConnection.getFilePath());			
			}
		} else {
			this.getFrameworkExecution().getFrameworkLog().log("restore.error.path.isFile", Level.DEBUG);
			RestoreTargetOperation restoreTargetOperation = new RestoreTargetOperation(this.getFrameworkExecution(), this.getExecutionControl());
			restoreTargetOperation.execute(path);		
		}
		
		// Log End
		this.getExecutionControl().logEnd(this);

		// Exit the execution
		// this.getEoControl().endExecution();
	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public ExecutionControl getExecutionControl() {
		return executionControl;
	}

	public void setExecutionControl(ExecutionControl executionControl) {
		this.executionControl = executionControl;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

}