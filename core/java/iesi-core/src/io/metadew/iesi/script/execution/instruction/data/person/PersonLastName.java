package io.metadew.iesi.script.execution.instruction.data.person;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

/**
 * @author robbe.berrevoets
 */
public class PersonLastName implements DataInstruction {

    private final GenerationObjectExecution generationObjectExecution;

    public PersonLastName(GenerationObjectExecution generationObjectExecution) {
        this.generationObjectExecution = generationObjectExecution;
    }

    @Override
    public String generateOutput(String parameters) {
        return generationObjectExecution.getName().lastName();
    }

    @Override
    public String getKeyword() {
        return "person.lastname";
    }
}