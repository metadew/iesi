package io.metadew.iesi.script.execution.instruction.data.math;

public class Multiplication extends BinaryArithmeticOperation{
    @Override
    Double executeOperation(Double operator1, Double operator2) {
        return operator1*operator2;
    }

    @Override
    public String getKeyword() {
        return "math.multiply";
    }
}
