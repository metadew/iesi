package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;
import nl.flotsam.xeger.Xeger;

public class Pattern extends GenerationComponentExecution {

    public Pattern(GenerationDataExecution execution) {
        super(execution);
    }

    public String nextValue(String pattern) {
        Xeger generator = new Xeger(pattern);
        return generator.generate();
    }

}
