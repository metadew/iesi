package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.util.ArrayList;
import java.util.List;

public class Lorem extends GenerationComponentExecution {

    private static final boolean DEFAULT_SUPPLEMENTAL = false;

    public Lorem(GenerationDataExecution execution) {
        super(execution);
    }

    public String word() {
        return fetch("lorem.words");
    }

    public String supplemental() {
        return fetch("lorem.supplemental");
    }

    public List<String> wordsList(int num) {
        return wordsList(num, DEFAULT_SUPPLEMENTAL);
    }

    public List<String> wordsList(int num, boolean supplemental) {
        List<String> words = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            if (supplemental && this.getGenerationTools().getRandomTools().randBoolean()) {
                words.add(supplemental());
            } else {
                words.add(word());
            }
        }
        return words;
    }

}
