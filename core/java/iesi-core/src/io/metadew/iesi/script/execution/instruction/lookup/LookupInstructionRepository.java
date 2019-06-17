package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.instruction.lookup.script.ScriptOutputLookup;

import java.util.HashMap;

public class LookupInstructionRepository {

    public static HashMap<String, LookupInstruction> getRepository(ExecutionControl executionControl) {
        HashMap<String, LookupInstruction> lookupInstructions = new HashMap<>();

        // Script
        ScriptOutputLookup scriptOutputLookup = new ScriptOutputLookup(executionControl);
        lookupInstructions.put(scriptOutputLookup.getKeyword(), scriptOutputLookup);
        // Connection
        ConnectionLookup connectionLookup = new ConnectionLookup(executionControl);
        lookupInstructions.put(connectionLookup.getKeyword(), connectionLookup);
        // File
        FileLookup fileLookup = new FileLookup();
        lookupInstructions.put(fileLookup.getKeyword(), fileLookup);
        // Coalesce
        CoalesceLookup coalesceLookup = new CoalesceLookup();
        lookupInstructions.put(coalesceLookup.getKeyword(), coalesceLookup);
        // Environment
        EnvironmentLookup environmentLookup = new EnvironmentLookup(executionControl);
        lookupInstructions.put(environmentLookup.getKeyword(), environmentLookup);
        // dataset
        DatasetLookup datasetLookup = new DatasetLookup(executionControl);
        lookupInstructions.put(datasetLookup.getKeyword(), datasetLookup);

        return lookupInstructions;
    }

}
