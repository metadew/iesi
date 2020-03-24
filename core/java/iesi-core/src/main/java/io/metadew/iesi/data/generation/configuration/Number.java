package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.math.BigDecimal;

public class Number extends GenerationComponentExecution {

    private static final int DEFAULT_DECIMAL_PART_DIGITS = 2;
    private static final int DEFAULT_FROM = 1;
    private static final int DEFAULT_TO = 5000;

    public Number(GenerationDataExecution execution) {
        super(execution);
    }

    public String number(int digits) {
        String num = "";
        if (digits > 1) {
            num = nonZeroDigit();
            digits -= 1;
        }
        return num + leadingZeroNumber(digits);
    }

    public String leadingZeroNumber(int digits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            sb.append(digit());
        }
        return sb.toString();
    }

    public String decimalPart(int digits) {
        String num = "";
        if (digits > 1) {
            num = nonZeroDigit();
            digits -= 1;
        }
        return leadingZeroNumber(digits) + num;
    }

    public String decimal(int leftDigits) {
        return decimal(leftDigits, DEFAULT_DECIMAL_PART_DIGITS);
    }

    public String decimal(int leftDigits, int rightDigits) {
        return number(leftDigits) + "." + decimalPart(rightDigits);
    }

    public String digit() {
        return Integer.toString(this.getGenerationTools().getRandomTools().number(10));
    }

    public String nonZeroDigit() {
        return Integer.toString(this.getGenerationTools().getRandomTools().number(9) + 1);
    }

    public String hexadecimal(int digits) {
        if (digits < 1) {
            return "";
        }
        long num = this.getGenerationTools().getRandomTools().number((long) Math.pow(16, digits));
        return String.format("%0" + digits + "x", num);
    }

    public int between() {
        return between(DEFAULT_FROM, DEFAULT_TO);
    }

    public int between(int from, int to) {
        return this.getGenerationTools().getRandomTools().range(from, to);
    }

    public long between(long from, long to) {
        return this.getGenerationTools().getRandomTools().range(from, to);
    }

    public double between(double from, double to) {
        return this.getGenerationTools().getRandomTools().range(from, to);
    }

    public int positive() {
        return positive(DEFAULT_FROM, DEFAULT_TO);
    }

    public int positive(int from, int to) {
        return Math.abs(between(from, to));
    }

    public long positive(long from, long to) {
        return Math.abs(between(from, to));
    }

    public double positive(double from, double to) {
        return Math.abs(between(from, to));
    }

    public int negative() {
        return negative(DEFAULT_FROM, DEFAULT_TO);
    }

    public int negative(int from, int to) {
        return Math.abs(between(from, to)) * -1;
    }

    public long negative(long from, long to) {
        return Math.abs(between(from, to)) * -1;
    }

    public double negative(double from, double to) {
        return Math.abs(between(from, to)) * -1;
    }

    public int getNextInt(int lbound, int ubound) {
        return lbound + (int) (Math.random() * (ubound - lbound));
    }

    public long getNextLong(long lbound, long ubound) {
        return lbound + (long) (Math.random() * (ubound - lbound));
    }

    public float getNextFloat(double lbound, double ubound, int decimalPlace) {
        return this.round((float) (lbound + (Math.random() * (ubound - lbound))), decimalPlace);
    }

    public float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
}
