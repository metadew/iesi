package io.metadew.iesi.script.execution.instruction.data.math;

public class Exponentiation extends BinaryArithmeticOperation {
    @Override
    Double executeOperation(Double operator1, Double operator2) {
        return Math.pow(operator1, operator2);
    }

    @Override
    public String getKeyword() {
        return "math.power";
    }
}
