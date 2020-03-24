package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

public class Bool extends GenerationComponentExecution {

    public Bool(GenerationDataExecution execution) {
        super(execution);
    }

    public boolean bool() {
        return this.getGenerationTools().getRandomTools().randBoolean();
        //sreturn bool(0.5f);
    }

    public boolean bool(float trueRatio) {
        return Math.random() < trueRatio;
    }
}