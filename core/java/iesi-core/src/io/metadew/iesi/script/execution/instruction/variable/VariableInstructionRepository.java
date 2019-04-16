package io.metadew.iesi.script.execution.instruction.variable;

import java.util.HashMap;

import io.metadew.iesi.script.execution.instruction.variable.framework.VersionInstruction;

public class VariableInstructionRepository
{

	public static HashMap<String, VariableInstruction> getReposistory()
	{
		HashMap<String, VariableInstruction> variableInstructions = new HashMap<>();

		VersionInstruction versionInstruction = new VersionInstruction();
		variableInstructions.put(versionInstruction.getKeyword(), versionInstruction);

		return variableInstructions;
	}

}
