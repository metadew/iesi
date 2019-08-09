package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.data.generation.output.control.rule.Print;
import io.metadew.iesi.data.generation.output.control.rule.RecordCount;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControlRule;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenerationControlRuleExecution {

	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private GenerationExecution generationExecution;
	private GenerationControlRule generationControlRule;
	private Long processId;
	private String output = "";
	private static final Logger LOGGER = LogManager.getLogger();

	// Constructors
	public GenerationControlRuleExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationExecution generationExecution,
			GenerationControlRule generationControlRule) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setGenerationExecution(generationExecution);
		this.setGenerationControlRule(generationControlRule);
	}

	// Methods
	public void execute() {
		LOGGER.warn("generation.rule.name="
				+ this.getGenerationControlRule().getName() + " (ID=" + this.getGenerationControlRule().getId() + ")",
				Level.INFO);

		// Log Start
		//this.getExecutionControl().logStart(this);
		this.setProcessId(0L);

		try {
			if (this.getGenerationControlRule().getType().trim().equalsIgnoreCase("print")) {
				Print eoPrint = new Print(this.getFrameworkExecution(), this.getExecutionControl(), this);
				if (eoPrint.execute()) {
					this.setOutput(eoPrint.getOutput());
				}
			} else if (this.getGenerationControlRule().getType().trim().equalsIgnoreCase("record_count")) {
				RecordCount eoRecordCount = new RecordCount(this.getFrameworkExecution(), this.getExecutionControl(), this);
				if (eoRecordCount.execute()) {
					this.setOutput(eoRecordCount.getOutput());
				}				
			} else {

			}

		} catch (Exception e) {
			LOGGER.warn("generation.rule.error="
					+ this.getGenerationControlRule().getName() + " (ID=" + this.getGenerationControlRule().getId() + ")"+e,
					Level.INFO);
		} finally {
			// Log End
			//this.getExecutionControl().endExecution(this);
		}

	}

	// Getters and Setters
	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

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

	public GenerationControlRule getGenerationControlRule() {
		return generationControlRule;
	}

	public void setGenerationControlRule(GenerationControlRule generationControlRule) {
		this.generationControlRule = generationControlRule;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}