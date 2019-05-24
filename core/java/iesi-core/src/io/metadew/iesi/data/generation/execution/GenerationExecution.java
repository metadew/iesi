package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Generation;
import io.metadew.iesi.metadata.definition.GenerationRule;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

public class GenerationExecution {

	private Generation generation;
	private GenerationOutputExecution generationOutputExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private Long processId;
	private GenerationRuntime generationRuntime;
	private long numberOfRecords = 0;
	private String paramList = "";
	private String paramFile = "";

	// Constructors
	public GenerationExecution(FrameworkExecution frameworkExecution, Generation generation) {
		this.setGeneration(generation);
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(new ExecutionControl(this.getFrameworkExecution()));
	}
	
	// Methods
	public void execute(String generationOutputName) {
		this.getFrameworkExecution().getFrameworkLog()
				.log("generation.name=" + this.getGeneration().getName(), Level.INFO);

		// Log Start
		//this.getExecutionControl().logStart(this);
		this.setProcessId(0L);
		this.setGenerationRuntime(new GenerationRuntime(this.getFrameworkExecution(), this.getExecutionControl()));
		this.getGenerationRuntime().addGeneration(this.getGeneration(), this.getNumberOfRecords());
		this.setGenerationOutputExecution(new GenerationOutputExecution(this.getFrameworkExecution(), this.getExecutionControl(), this, generationOutputName));

		// Parameters
		// ParamList has priority of ParamFile
		if (!this.getParamFile().trim().equalsIgnoreCase("")) {
			// TODO does not work anymore (process id added for scripts)
			//this.getExecutionControl().getExecutionRuntime().loadParamFiles(this.getParamFile());
		}
		if (!this.getParamList().trim().equalsIgnoreCase("")) {
			// TODO does not work anymore (process id added for scripts)
			//this.getExecutionControl().getExecutionRuntime().loadParamList(this.getParamList());
		}
		
		// Loop through the generation rules
		for (GenerationRule generationRule : this.getGeneration().getRules()) {
			GenerationRuleExecution generationRuleExecution = new GenerationRuleExecution(this.getFrameworkExecution(), this.getExecutionControl(),
					this, generationRule);
			generationRuleExecution.execute();
		}
		
		// Generate output
		this.getGenerationOutputExecution().execute(); 

		// Log End
		//this.getExecutionControl().logEnd(this);

		// Exit the execution
		// this.getEoControl().endExecution();
	}

	// Getters and Setters
	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public long getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(long numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	public String getParamList() {
		return paramList;
	}

	public void setParamList(String paramList) {
		this.paramList = paramList;
	}

	public String getParamFile() {
		return paramFile;
	}

	public void setParamFile(String paramFile) {
		this.paramFile = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(paramFile);
	}

	public GenerationRuntime getGenerationRuntime() {
		return generationRuntime;
	}

	public void setGenerationRuntime(GenerationRuntime generationRuntime) {
		this.generationRuntime = generationRuntime;
	}

	public ExecutionControl getExecutionControl() {
		return executionControl;
	}

	public void setExecutionControl(ExecutionControl executionControl) {
		this.executionControl = executionControl;
	}

	public GenerationOutputExecution getGenerationOutputExecution() {
		return generationOutputExecution;
	}

	public void setGenerationOutputExecution(GenerationOutputExecution generationOutputExecution) {
		this.generationOutputExecution = generationOutputExecution;
	}

	public Generation getGeneration() {
		return generation;
	}

	public void setGeneration(Generation generation) {
		this.generation = generation;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}