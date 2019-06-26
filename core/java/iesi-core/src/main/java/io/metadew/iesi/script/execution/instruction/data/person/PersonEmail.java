package io.metadew.iesi.script.execution.instruction.data.person;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

/**
 * @author robbe.berrevoets
 */
public class PersonEmail implements DataInstruction {

    private final GenerationObjectExecution generationObjectExecution;

    public PersonEmail(GenerationObjectExecution generationObjectExecution) {
        this.generationObjectExecution = generationObjectExecution;
    }

    @Override
    public String generateOutput(String parameters) {
        return generationObjectExecution.getInternet().email();
    }

    @Override
    public String getKeyword() {
        return "person.email";
    }
}