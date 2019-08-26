package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.data.generation.output.DelimitedFile;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.generation.GenerationOutput;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenerationOutputExecution {

	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private GenerationExecution generationExecution;
	private GenerationOutput generationOutput;
	private static final Logger LOGGER = LogManager.getLogger();
	
	// Constructors
	public GenerationOutputExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationExecution generationExecution, String generationOutputName) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setGenerationExecution(generationExecution);
		for (GenerationOutput generationOutput : this.getGenerationExecution().getGeneration().getOutputs()) {
			if (generationOutput.getName().trim().equalsIgnoreCase(generationOutputName.trim())) {
				this.setGenerationOutput(generationOutput);
			}
		}
	}

	// Methods
	public void execute() {
		
		try {
			if (this.getGenerationOutput().getType().trim().equalsIgnoreCase("file.delimited")) {
				DelimitedFile delimitedFile = new DelimitedFile(this.getFrameworkExecution(), this.getExecutionControl(), this);
				delimitedFile.execute();
			} else {

			}
		} catch (Exception e) {
			LOGGER.info("generation.output.error=" + this.getGenerationOutput().getType()+e);

		} finally {
	
		}
		
	}

	// Getters and Setters
	public ExecutionControl getExecutionControl() {
		return executionControl;
	}

	public void setExecutionControl(ExecutionControl executionControl) {
		this.executionControl = executionControl;
	}

	public GenerationExecution getGenerationExecution() {
		return generationExecution;
	}

	public void setGenerationExecution(GenerationExecution generationExecution) {
		this.generationExecution = generationExecution;
	}

	public GenerationOutput getGenerationOutput() {
		return generationOutput;
	}

	public void setGenerationOutput(GenerationOutput generationOutput) {
		this.generationOutput = generationOutput;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}