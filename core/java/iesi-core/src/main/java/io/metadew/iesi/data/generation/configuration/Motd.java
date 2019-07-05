package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

public class Motd extends GenerationComponentExecution {

    public Motd(GenerationDataExecution execution) {
        super(execution);
    }

    public String message() {
        return fetch("motd.message");
    }

}