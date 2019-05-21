package io.metadew.iesi.script.execution.instruction.variable;

import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.instruction.variable.framework.FrameworkHomeInstruction;
import io.metadew.iesi.script.execution.instruction.variable.framework.VersionInstruction;
import io.metadew.iesi.script.execution.instruction.variable.runtime.EnvironmentInstruction;
import io.metadew.iesi.script.execution.instruction.variable.runtime.RunidInstruction;

import java.util.HashMap;

public class VariableInstructionRepository {

    public static HashMap<String, VariableInstruction> getRepository(ExecutionControl executionControl) {
        HashMap<String, VariableInstruction> variableInstructions = new HashMap<>();

        // Framework
        FrameworkHomeInstruction frameworkHomeInstruction = new FrameworkHomeInstruction(executionControl);
        variableInstructions.put(frameworkHomeInstruction.getKeyword(), frameworkHomeInstruction);
        VersionInstruction versionInstruction = new VersionInstruction();
        variableInstructions.put(versionInstruction.getKeyword(), versionInstruction);

        // Runtime
        EnvironmentInstruction environmentInstruction = new EnvironmentInstruction(executionControl);
        variableInstructions.put(environmentInstruction.getKeyword(), environmentInstruction);
        RunidInstruction runidInstruction = new RunidInstruction(executionControl);
        variableInstructions.put(runidInstruction.getKeyword(), runidInstruction);


        return variableInstructions;
    }

}
