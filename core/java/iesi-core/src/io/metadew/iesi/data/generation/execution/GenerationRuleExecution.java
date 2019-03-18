package io.metadew.iesi.data.generation.execution;

import org.apache.logging.log4j.Level;

import io.metadew.iesi.data.generation.rule.DtDate;
import io.metadew.iesi.data.generation.rule.DtNow;
import io.metadew.iesi.data.generation.rule.DtTimestamp;
import io.metadew.iesi.data.generation.rule.IdGuid;
import io.metadew.iesi.data.generation.rule.ListCustom;
import io.metadew.iesi.data.generation.rule.NumNumber;
import io.metadew.iesi.data.generation.rule.NumRowNumber;
import io.metadew.iesi.data.generation.rule.TxtCharacters;
import io.metadew.iesi.data.generation.rule.TxtParagraphs;
import io.metadew.iesi.data.generation.rule.TxtSentences;
import io.metadew.iesi.data.generation.rule.TxtWords;
import io.metadew.iesi.data.generation.rule.ValBlank;
import io.metadew.iesi.data.generation.rule.ValBoolean;
import io.metadew.iesi.data.generation.rule.ValPattern;
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
	public GenerationRuleExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationExecution generationExecution,
			GenerationRule generationRule) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setGenerationExecution(generationExecution);
		this.setGenerationRule(generationRule);
	}

	// Methods
	public void execute() {
		this.getFrameworkExecution().getFrameworkLog().log("generation.rule.name="
				+ this.getGenerationRule().getField() + " (ID=" + this.getGenerationRule().getId() + ")",
				Level.INFO);

		// Log Start
		//this.getExecutionControl().logStart(this);
		this.setProcessId(this.getExecutionControl().getProcessId());
		
		try {
			if (this.getGenerationRule().getType().trim().equalsIgnoreCase("blank")) {
				ValBlank eoValBlank = new ValBlank(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoValBlank.execute();
			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("boolean")) {
				ValBoolean eoValBoolean = new ValBoolean(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoValBoolean.execute();
			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("guid")) {
				IdGuid eoIdGuid = new IdGuid(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoIdGuid.execute();
			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("characters")) {
				TxtCharacters eoTxtCharacters = new TxtCharacters(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoTxtCharacters.execute();
			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("words")) {
				TxtWords eoTxtWords = new TxtWords(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoTxtWords.execute();
			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("sentences")) {
				TxtSentences eoTxtSentences = new TxtSentences(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoTxtSentences.execute();
			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("paragraphs")) {
				TxtParagraphs eoTxtParagraphs = new TxtParagraphs(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoTxtParagraphs.execute();
			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("pattern")) {
				ValPattern eoValPattern = new ValPattern(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoValPattern.execute();

			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("row_number")) {
				NumRowNumber eoNumRowNumber = new NumRowNumber(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoNumRowNumber.execute();

			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("date")) {
				DtDate eoDtDate = new DtDate(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoDtDate.execute();
			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("timestamp")) {
				DtTimestamp eoDtTimestamp = new DtTimestamp(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoDtTimestamp.execute();
			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("now")) {
				DtNow eoDtNow = new DtNow(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoDtNow.execute();

			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("number")) {
				NumNumber eoNumNumber = new NumNumber(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoNumNumber.execute();

			} else if (this.getGenerationRule().getType().trim().equalsIgnoreCase("custom_list")) {
				ListCustom eoListCustom = new ListCustom(this.getFrameworkExecution(), this.getExecutionControl(), this);
				eoListCustom.execute();
			
			} else {

			}

			// ValBlank injection
			if (this.getGenerationRule().getBlankInjectionFlag().trim().equalsIgnoreCase("y")) {
				GenerationRuleBlankInjectionExecution generationRuleBlankInjectionExecution = new GenerationRuleBlankInjectionExecution(
						this.getFrameworkExecution(), this.getExecutionControl(), this);
				generationRuleBlankInjectionExecution.execute();
			}

		} catch (Exception e) {
			this.getFrameworkExecution().getFrameworkLog().log("Exception during Executing Generation Rule (NAME) "
					+ this.getGenerationRule().getField() + " (ID=" + this.getGenerationRule().getId() + ")",
					Level.WARN);
		} finally {
			// Log End
			//this.getExecutionControl().logEnd(this);
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