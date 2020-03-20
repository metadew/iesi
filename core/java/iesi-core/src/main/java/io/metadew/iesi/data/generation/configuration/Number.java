package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

public class Number extends GenerationComponentExecution {

    public Number(GenerationDataExecution execution) {
        super(execution);
    }

    public double between(double from, double to) {
        return this.getGenerationTools().getRandomTools().range(from, to);
    }

    public long getNextLong(long lbound, long ubound) {
        return lbound + (long) (Math.random() * (ubound - lbound));
    }

}
