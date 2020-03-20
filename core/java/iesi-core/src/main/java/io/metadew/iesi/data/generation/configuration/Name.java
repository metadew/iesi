package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.util.List;

public class Name extends GenerationComponentExecution {

    public Name(GenerationDataExecution execution) {
        super(execution);
    }

    public String firstName() {
        return fetch("name.first_name");
    }

    public String lastName() {
        return fetch("name.last_name");
    }

}