package io.metadew.iesi.data.generation.tools;

public class NumberTools {

    public NumberTools() {
    }

    public double round(double number, int precision) {
        double precisionPow = Math.pow(10, precision);
        return Math.round(number * precisionPow) / precisionPow;
    }
}
