package io.metadew.iesi.script.operation;

import io.metadew.iesi.metadata.definition.Iteration;

/**
 * Operation to manage the iterations that have been defined in the script
 *
 * @author peter.billen
 */
public class IterationOperation {
    private Iteration iteration;

    // Constructors
    public IterationOperation(Iteration iteration) {
        this.setIteration(iteration);
    }

    // Getters and setters
    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }


}