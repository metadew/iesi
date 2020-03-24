package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

public class Team extends GenerationComponentExecution {

    public Team(GenerationDataExecution execution) {
        super(execution);
    }

    public String name() {
        return parse(fetch("team.name"));
    }

    public String creature() {
        return fetch("team.creature");
    }

    public String state() {
        return call("address.state");
    }

    public String sport() {
        return fetch("team.sport");
    }
}
