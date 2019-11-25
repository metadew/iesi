package io.metadew.iesi.script.execution.instruction.data.math;

public class Addition extends AbstractOperationTwoArg {
    @Override
    Double executeOperation(Double operator1, Double operator2) {
        return operator1+operator2;
    }

    @Override
    public String getKeyword() {
        return "math.add";
    }
}
