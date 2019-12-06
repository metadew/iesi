package io.metadew.iesi.script.execution.instruction.data.math;

public class Division extends BinaryArithmeticOperation {

    @Override
    Double executeOperation(Double operator1, Double operator2) {
        return operator1 / operator2;
    }

    @Override
    String handleOperationAsStrings(String operator1, String operator2){
        // this method needs to be overridden because the division of two ints can still be a double
        // therefore this method will always return a double transformed to string
        Double operator1Double = Double.valueOf(operator1);
        Double operator2Double = Double.valueOf(operator2);
        Double result = executeOperation(operator1Double, operator2Double);
        return result.toString();
    }

    @Override
    public String getKeyword() {
        return "math.divide";
    }
}
