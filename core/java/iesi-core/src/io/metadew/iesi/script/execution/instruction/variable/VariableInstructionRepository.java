package io.metadew.iesi.script.execution.instruction.variable;

import java.util.HashMap;

import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.instruction.variable.framework.VersionInstruction;
import io.metadew.iesi.script.execution.instruction.variable.runtime.EnvironmentInstruction;

public class VariableInstructionRepository
{

	public static HashMap<String, VariableInstruction> getReposistory(ExecutionControl executionControl)
	{
		HashMap<String, VariableInstruction> variableInstructions = new HashMap<>();

		VersionInstruction versionInstruction = new VersionInstruction();
		variableInstructions.put(versionInstruction.getKeyword(), versionInstruction);
		
		EnvironmentInstruction environmentInstruction = new EnvironmentInstruction(executionControl);
		variableInstructions.put(environmentInstruction.getKeyword(), environmentInstruction);

		return variableInstructions;
	}

}
