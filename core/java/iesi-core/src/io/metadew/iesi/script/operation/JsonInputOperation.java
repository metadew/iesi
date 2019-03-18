package io.metadew.iesi.script.operation;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.metadata.operation.DataObjectOperation;

/**
 * Operation to manage script execution when a JSON file has been provided as input.
 * 
 * @author peter.billen
 *
 */
public class JsonInputOperation {
	
	private FrameworkExecution frameworkExecution;
	private String fileName;
	private DataObjectOperation dataObjectOperation;
	
	
	public JsonInputOperation(FrameworkExecution frameworkExecution, String fileName) {
		this.setFrameworkExecution(frameworkExecution);
		this.setFileName(fileName);
		this.setDataObjectOperation(new DataObjectOperation(this.getFrameworkExecution(), this.getFileName()));
	}
	
	public Script getScript() {
		ObjectMapper objectMapper = new ObjectMapper();
		Script script = null;
		for (DataObject dataObject : this.getDataObjectOperation().getDataObjectConfiguration().getDataObjects()) {

			// Scripts
			if (dataObject.getType().equalsIgnoreCase("script")) {
				script = objectMapper.convertValue(dataObject.getData(), Script.class);
				break;
			}

		}
		
		return script;
	}

	// Getters and Setters
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public DataObjectOperation getDataObjectOperation() {
		return dataObjectOperation;
	}

	public void setDataObjectOperation(DataObjectOperation dataObjectOperation) {
		this.dataObjectOperation = dataObjectOperation;
	}
	
}