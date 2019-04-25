package io.metadew.iesi.script.execution.instruction.variable.framework;

import org.apache.commons.io.FilenameUtils;

import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstruction;


public class FrameworkHomeInstruction implements VariableInstruction
{
	
	private final ExecutionControl executionControl; 

	public FrameworkHomeInstruction(ExecutionControl executionControl)
	{
		this.executionControl = executionControl;
	}

	@Override
	public String generateOutput()
	{
		return FilenameUtils.normalize(this.getExecutionControl().getFrameworkExecution().getFrameworkConfiguration().getFrameworkHome());
	}

	@Override
	public String getKeyword()
	{
		return "fwk.home";
	}

	public ExecutionControl getExecutionControl() {
		return executionControl;
	}
}