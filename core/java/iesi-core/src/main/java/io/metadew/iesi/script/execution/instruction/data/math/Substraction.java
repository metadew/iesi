package io.metadew.iesi.script.execution.instruction.data.math;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.util.Arrays;
import java.util.List;

public class Substraction extends BinaryArithmeticOperation{

    @Override
    Double executeOperation(Double operator1, Double operator2) {
        return operator1-operator2;
    }

    @Override
    public String getKeyword() {
        return "math.substract";
    }
}
