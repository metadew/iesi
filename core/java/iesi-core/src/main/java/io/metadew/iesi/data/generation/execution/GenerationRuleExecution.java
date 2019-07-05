package io.metadew.iesi.data.generation.execution;

import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationRule;
import io.metadew.iesi.script.execution.ExecutionControl;

public class GenerationRuleExecution {

	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private GenerationExecution generationExecution;
	private GenerationRule generationRule;
	private Long processId;

	// Constructors
	public GenerationRuleExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
			GenerationExecution generationExecution, GenerationRule generationRule) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setGenerationExecution(generationExecution);
		this.setGenerationRule(generationRule);
	}

	// Methods
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute() {
		this.getFrameworkExecution().getFrameworkLog().log("generation.rule.name=" + this.getGenerationRule().getField()
				+ " (ID=" + this.getGenerationRule().getId() + ")", Level.INFO);

		// Log Start
		// this.getExecutionControl().logStart(this);
		this.setProcessId(0L);

		try {
			String className = this.getFrameworkExecution().getFrameworkConfiguration().getGenerationRuleTypeConfiguration()
					.getGenerationRuleTypeClass(this.getGenerationRule().getType());
			// TODO add log
			// this.getExecutionControl().logMessage(this, "generation.rule.type=" + this.getGenerationRule().getType(), Level.DEBUG);
			
			Class classRef = Class.forName(className);
			Object instance = classRef.newInstance();

			Class initMethodParams[] = { FrameworkExecution.class, ExecutionControl.class, GenerationRuleExecution.class };
			Method initMethod = classRef.getDeclaredMethod("init", initMethodParams);
			Object[] initMethodArgs = { this.getFrameworkExecution(), this.getExecutionControl(), this };
			initMethod.invoke(instance, initMethodArgs);
			
			Method executeMethod = classRef.getDeclaredMethod("execute");
			executeMethod.invoke(instance);

			// ValBlank injection
			if (this.getGenerationRule().getBlankInjectionFlag().trim().equalsIgnoreCase("y")) {
				GenerationRuleBlankInjectionExecution generationRuleBlankInjectionExecution = new GenerationRuleBlankInjectionExecution(
						this.getFrameworkExecution(), this.getExecutionControl(), this);
				generationRuleBlankInjectionExecution.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();
			this.getFrameworkExecution().getFrameworkLog().log("Exception during Executing Generation Rule (NAME) "
					+ this.getGenerationRule().getField() + " (ID=" + this.getGenerationRule().getId() + ")",
					Level.WARN);
		} finally {
			// Log End
			// this.getExecutionControl().logEnd(this);
		}

	}

	// Getters and Setters
	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
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

	public GenerationRule getGenerationRule() {
		return generationRule;
	}

	public void setGenerationRule(GenerationRule generationRule) {
		this.generationRule = generationRule;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}