package io.metadew.iesi.data.generation.output;

import io.metadew.iesi.common.list.ListTools;
import io.metadew.iesi.common.text.ParsingTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.connection.tools.OutputTools;
import io.metadew.iesi.data.generation.execution.GenerationControlExecution;
import io.metadew.iesi.data.generation.execution.GenerationOutputExecution;
import io.metadew.iesi.data.generation.execution.GenerationOutputParameterExecution;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControl;
import io.metadew.iesi.metadata.definition.GenerationOutputParameter;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class DelimitedFile {

	private GenerationOutputExecution generationOutputExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private String generationOutputTypeName = "file.delimited";

	// Parameters
	private GenerationOutputParameterExecution fileName;
	private GenerationOutputParameterExecution separator;
	private GenerationOutputParameterExecution enclosure;
	private GenerationOutputParameterExecution includeFieldNames;
	private GenerationOutputParameterExecution encoding;
	private GenerationOutputParameterExecution controls;

	// Constructors
	public DelimitedFile(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
			GenerationOutputExecution generationOutputExecution) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setGenerationOutputExecution(generationOutputExecution);
	}

	//
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean execute() {
		try {
			this.getFrameworkExecution().getFrameworkLog()
					.log("generation.output.type=" + this.getGenerationOutputTypeName(), Level.INFO);

			// Reset Parameters
			this.setFileName(new GenerationOutputParameterExecution(this.getFrameworkExecution(), this.getExecutionControl(),
					this.getGenerationOutputTypeName(), "file_nm"));
			this.setSeparator(new GenerationOutputParameterExecution(this.getFrameworkExecution(), this.getExecutionControl(),
					this.getGenerationOutputTypeName(), "separator"));
			this.setEnclosure(new GenerationOutputParameterExecution(this.getFrameworkExecution(), this.getExecutionControl(),
					this.getGenerationOutputTypeName(), "enclosure"));
			this.setIncludeFieldNames(new GenerationOutputParameterExecution(this.getFrameworkExecution(), this.getExecutionControl(),
					this.getGenerationOutputTypeName(), "incl_field_names"));
			this.setEncoding(new GenerationOutputParameterExecution(this.getFrameworkExecution(), this.getExecutionControl(),
					this.getGenerationOutputTypeName(), "encoding"));
			this.setControls(new GenerationOutputParameterExecution(this.getFrameworkExecution(), this.getExecutionControl(),
					this.getGenerationOutputTypeName(), "controls"));

			// Get Parameters
			for (GenerationOutputParameter generationOutputParameter : this.getGenerationOutputExecution()
					.getGenerationOutput().getParameters()) {
				if (generationOutputParameter.getName().equalsIgnoreCase("file_nm")) {
					this.getFileName().setInputValue(generationOutputParameter.getValue());
				} else if (generationOutputParameter.getName().equalsIgnoreCase("separator")) {
					this.getSeparator().setInputValue(generationOutputParameter.getValue());
				} else if (generationOutputParameter.getName().equalsIgnoreCase("enclosure")) {
					this.getEnclosure().setInputValue(generationOutputParameter.getValue());
				} else if (generationOutputParameter.getName().equalsIgnoreCase("incl_field_names")) {
					this.getIncludeFieldNames().setInputValue(generationOutputParameter.getValue());
				} else if (generationOutputParameter.getName().equalsIgnoreCase("encoding")) {
					this.getEncoding().setInputValue(generationOutputParameter.getValue());
				} else if (generationOutputParameter.getName().equalsIgnoreCase("controls")) {
					this.getControls().setInputValue(generationOutputParameter.getValue());
				}
			}

			// Run the generation Output
			String fullFileName = "";
			try {

				String folderName = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.tmp")
						+ File.separator + this.getGenerationOutputExecution().getGenerationExecution().getGeneration().getName();
				FolderTools.createFolder(folderName);
				String fileName = this.composeFileName();
				fullFileName = folderName + File.separator + fileName;
				CachedRowSet crs = null;
				String query = "select "
						+ this.getGenerationOutputExecution().getGenerationExecution().getGenerationRuntime().getFieldListSelect()
						+ " from "
						+ this.getGenerationOutputExecution().getGenerationExecution().getGenerationRuntime().getTableName();
				crs = this.getGenerationOutputExecution().getGenerationExecution().getGenerationRuntime().getTemporaryDatabaseConnection()
						.executeQuery(query);
				OutputTools.createOutputFile(fileName, folderName, crs,
						this.getSeparator().getValue(),
						(this.getIncludeFieldNames().getValue().trim().equalsIgnoreCase("y") ? true : false));

			} catch (Exception e) {
				throw new RuntimeException("Issue generating output: " + e, e);
			}
			
			ArrayList<String> controlsList = new ArrayList();
			String[] parts = this.getControls().getValue().split(",");
			for (int i = 0; i < parts.length; i++) {
				String innerpart = parts[i];
				controlsList.add(innerpart.trim().toLowerCase());
			}
			
			for (GenerationControl generationControl : this.getGenerationOutputExecution().getGenerationExecution().getGeneration().getControls()) {
				if (ListTools.inList(controlsList, generationControl.getName().trim().toLowerCase())) {
					GenerationControlExecution generationControlExecution = new GenerationControlExecution(this.getFrameworkExecution(), this.getExecutionControl(), this.getGenerationOutputExecution().getGenerationExecution(), "footer");
					generationControlExecution.execute(fullFileName);
				}
			}
			
			return true;
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			// TODO logging

			return false;
		}

	}

	private String composeFileName() {
		String tempFileName = "";
		if (this.getFileName().getValue().trim().equalsIgnoreCase("")) {
			// Set default file name
			tempFileName = this.getGenerationOutputExecution().getGenerationExecution().getGeneration().getName() + ".txt";
		} else {
			if (ParsingTools.isRegexFunction(this.getFileName().getValue())) {
				tempFileName = this.getGenerationOutputExecution().getGenerationExecution().getGenerationRuntime()
						.getGenerationObjectExecution().getPattern()
						.nextValue(ParsingTools.getRegexFunctionValue(this.getFileName().getValue()));
			} else {
				tempFileName = this.getFileName().getValue();
			}
		}
		return tempFileName;
	}

	// Getters and Setters
	public String getGenerationOutputTypeName() {
		return generationOutputTypeName;
	}

	public void setGenerationOutputTypeName(String generationOutputTypeName) {
		this.generationOutputTypeName = generationOutputTypeName;
	}

	public GenerationOutputParameterExecution getSeparator() {
		return separator;
	}

	public void setSeparator(GenerationOutputParameterExecution separator) {
		this.separator = separator;
	}

	public GenerationOutputParameterExecution getIncludeFieldNames() {
		return includeFieldNames;
	}

	public void setIncludeFieldNames(GenerationOutputParameterExecution includeFieldNames) {
		this.includeFieldNames = includeFieldNames;
	}

	public GenerationOutputParameterExecution getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(GenerationOutputParameterExecution enclosure) {
		this.enclosure = enclosure;
	}

	public GenerationOutputParameterExecution getFileName() {
		return fileName;
	}

	public void setFileName(GenerationOutputParameterExecution fileName) {
		this.fileName = fileName;
	}

	public GenerationOutputParameterExecution getEncoding() {
		return encoding;
	}

	public void setEncoding(GenerationOutputParameterExecution encoding) {
		this.encoding = encoding;
	}

	public GenerationOutputParameterExecution getControls() {
		return controls;
	}

	public void setControls(GenerationOutputParameterExecution controls) {
		this.controls = controls;
	}

	public GenerationOutputExecution getGenerationOutputExecution() {
		return generationOutputExecution;
	}

	public void setGenerationOutputExecution(GenerationOutputExecution generationOutputExecution) {
		this.generationOutputExecution = generationOutputExecution;
	}

	public ExecutionControl getExecutionControl() {
		return executionControl;
	}

	public void setExecutionControl(ExecutionControl executionControl) {
		this.executionControl = executionControl;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}