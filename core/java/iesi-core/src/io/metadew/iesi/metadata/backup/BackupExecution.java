package io.metadew.iesi.metadata.backup;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Level;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.script.execution.ExecutionControl;

public class BackupExecution {

	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private Long processId;

	// Constructors
	public BackupExecution() {
		Context context = new Context();
		context.setName("backup");
		context.setScope("");
		this.setFrameworkExecution(new FrameworkExecution(new FrameworkExecutionContext(context), null));
		this.setExecutionControl(new ExecutionControl(this.getFrameworkExecution()));
	}

	// Methods
	public void execute(String path) {
		this.getFrameworkExecution().getFrameworkLog().log("metadata.backup.start", Level.INFO);

		// Log Start
		this.getExecutionControl().logStart(this);
		this.setProcessId(this.getExecutionControl().getProcessId());

		// Get source configuration
		DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(),
				this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("metadata.def") + File.separator
						+ "MetadataTables.json");

		// Create backup location
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		String folderName = "";
		if (path.trim().equals("")) {
			folderName = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("metadata.def") + File.separator + sdf.format(timestamp);;		
		
			// Ensure the base folder structure exists
			FolderTools.createFolder(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("data")); // Data
			FolderTools.createFolder(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("data")); //Backups
			FolderTools.createFolder(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("data")); // Backups Metadata
		} else {
			folderName = path;
		}

		// Create the folder name for the backup
		FolderTools.deleteFolder(folderName, true);
		FolderTools.createFolder(folderName);

		ObjectMapper objectMapper = new ObjectMapper();
		for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
			// Metadata Tables
			if (dataObject.getType().equalsIgnoreCase("metadatatable")) {
				MetadataTable metadataTable = objectMapper.convertValue(dataObject.getData(),
						MetadataTable.class);

				// Get source data for migration
				MetadataExtractOperation metadataExtractOperation = new MetadataExtractOperation(this.getFrameworkExecution(),
						this.getExecutionControl());
				metadataExtractOperation.execute(metadataTable, folderName);

			} else {

			}
		}

		// Log End
		this.getExecutionControl().logEnd(this);
		this.getFrameworkExecution().getFrameworkLog().log("metadata.backup.end", Level.INFO);

		// Exit the execution
		// this.getEoControl().endExecution();
	}

	// Getters and Setters
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

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}