package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.generation.GenerationRule;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GenerationRuleExecution {

	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private GenerationExecution generationExecution;
	private GenerationRule generationRule;
	private Long processId;

	private static final Logger LOGGER = LogManager.getLogger();
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
		LOGGER.info("generation.rule.name=" + this.getGenerationRule().getField()
				+ " (ID=" + this.getGenerationRule().getId() + ")");

		// Log Start
		// this.getExecutionControl().logStart(this);
		this.setProcessId(0L);

		try {
			String className = null;
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

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			LOGGER.warn("Exception during Executing Generation Rule (NAME) "
					+ this.getGenerationRule().getField() + " (ID=" + this.getGenerationRule().getId() + ")");
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