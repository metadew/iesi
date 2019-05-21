package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.data.generation.configuration.*;
import io.metadew.iesi.data.generation.tools.GenerationTools;

import java.util.HashMap;
import java.util.Map;

public class GenerationDataExecution {

    private final Map<String, Object> data;
    private final Map<String, GenerationComponentExecution> executions;
    private GenerationTools generationTools;

    public GenerationDataExecution(Map<String, Object> data) {
        this.data = data;
        this.setGenerationTools(new GenerationTools());

        // Load executions
        GenerationComponentExecution[] componentsList = new GenerationComponentExecution[]{
                new Address(this),
                new App(this),
                new Avatar(this),
                new Book(this),
                new Bool(this),
                new Color(this),
                new Company(this),
                new CreditCard(this),
                new Date(this),
                new Internet(this),
                new Lorem(this),
                new Motd(this),
                new Name(this),
                new io.metadew.iesi.data.generation.configuration.Number(this),
                new Pattern(this),
                new PhoneNumber(this),
                new Placeholdit(this),
                new Retail(this),
                new SlackEmoji(this),
                new Time(this),
                new Timestamp(this),
                new Team(this),
                new University(this),
        };

        this.executions = new HashMap<>(componentsList.length);

        for (GenerationComponentExecution execution : componentsList) {
            this.executions.put(execution.getKey(), execution);
        }

    }

    @SuppressWarnings("unchecked")
    public <K extends GenerationComponentExecution> K getComponent(Class<K> componentClass) {
        String componentKey = componentClass.getSimpleName();
        return (K) getExecutionByKey(componentKey);
    }

    public GenerationComponentExecution getExecutionByKey(String componentKey) {
        String componentKeyInSnake = this.getGenerationTools().getStringTools().camelToSnake(componentKey);
        GenerationComponentExecution execution = executions.get(componentKeyInSnake);

        if (execution == null) {
            throw new IllegalArgumentException("Unsupported component '" + componentKey + "'");
        }

        return execution;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getComponentData(String componentKey) {
        Map<String, Object> component = (Map<String, Object>) get(componentKey);
        if (component == null) {
            throw new IllegalArgumentException("Unsupported component '" + componentKey + "'");
        }
        return component;
    }

    // Getters and setters
    public Object get(String componentKey) {
        return data.get(componentKey);
    }

    public GenerationTools getGenerationTools() {
        return generationTools;
    }

    public void setGenerationTools(GenerationTools generationTools) {
        this.generationTools = generationTools;
    }
}
