package io.metadew.iesi.script.execution.instruction.data.number;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class NumberBetweenTest {

    NumberBetween numberBetween = new NumberBetween();

    @Test
    void illegalInputGeneralTest() {

        assertThatIllegalArgumentException()
                .as("empty parameters should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(""));

        assertThatIllegalArgumentException()
                .as("empty parameters should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" , "));

        assertThatIllegalArgumentException()
                .as("empty parameters should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" , , "));

        assertThatIllegalArgumentException()
                .as("Illegal argument should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" Illegal "));

        assertThatIllegalArgumentException()
                .as("2 illegals arguments should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" Illegal, Illegal"));

        assertThatIllegalArgumentException()
                .as("3 illegals arguments should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" Illegal, Illegal, Illegal"));

    }

    @Test
    void illegalInput1stArgTest() {
        assertThatIllegalArgumentException()
                .as("Illegal first argument should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" Illegal, 5"));

        assertThatIllegalArgumentException()
                .as("Illegal first argument should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" Illegal, 5, 32"));

        assertThatIllegalArgumentException()
                .as("Empty first argument is Illegal and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput("   , 5 , 1"));

        assertThatIllegalArgumentException()
                .as("\"--\" is illegal an should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" --1  , 2"));

        assertThatIllegalArgumentException()
                .as("lol")
                .isThrownBy(() -> numberBetween.generateOutput(" \"2Illegal\"  , \"2\" , \"2\""));

    }

    @Test
    void illegalInput2ndArgTest() {
        assertThatIllegalArgumentException()
                .as("Illegal second argument should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 5, Illegal"));

        assertThatIllegalArgumentException()
                .as("Empty second argument is Illegal and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 5, "));

        assertThatIllegalArgumentException()
                .as("Empty second argument is Illegal and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 5  ,  , 1"));

        assertThatIllegalArgumentException()
                .as("\"--\" is illegal an should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 1  , --2"));

        assertThatIllegalArgumentException()
                .as("lol")
                .isThrownBy(() -> numberBetween.generateOutput(" \"2\"  , \"2Illegal\" , \"2\""));

    }

    @Test
    void illegalInput3rdArgTest() {
        assertThatIllegalArgumentException()
                .as("Optional 3rd argument initiated and left empty is an illegal argument and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 5  , 2 ,  "));

        assertThatIllegalArgumentException()
                .as("Negative 3rd argument is Illegal and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 2  , 5 , -1"));

        assertThatIllegalArgumentException()
                .as("3rd argument as a float is an illegal argument and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 1  , 2 , 2.0 "));

        assertThatIllegalArgumentException()
                .as("3rd argument as a float is an illegal argument and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 1  , 2 , 3.58 "));

        assertThatIllegalArgumentException()
                .as("3rd argument as a string is an illegal argument and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 1  , 2 , Illegal "));

        assertThatIllegalArgumentException()
                .as("3rd argument as a string is an illegal argument and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 1  , 2 , Illegal.Illegal "));

        assertThatIllegalArgumentException()
                .as("3rd argument contain string. It is an illegal argument and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 1  , 2 , 2.Illegal "));

        assertThatIllegalArgumentException()
                .as("3rd argument contain string. It is an illegal argument and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 1  , 2 , Illegal.2 "));

        assertThatIllegalArgumentException()
                .as("3rd argument is not properly formatted and should throw an IllegalArgumentException")
                .isThrownBy(() -> numberBetween.generateOutput(" 1  , 2 , 2,0 "));

        assertThatIllegalArgumentException()
                .as("lol")
                .isThrownBy(() -> numberBetween.generateOutput(" \"2\"  , \"2\" , \"2Illegal\""));

    }

    @Test
    void originalAllowedInput() {
        assertThat(numberBetween.generateOutput(" \"2\"  , \"2\" , \"2\""))
                .as("parameters <\"2\"  , \"2\" , \"2\"> should be allowed")
                .isEqualTo("2.00");

        assertThat(numberBetween.generateOutput(" \"2\"  , \"2\" , \"2\""))
                .as("parameters <\"20\"  , \"20\" , \"20\"> should be allowed")
                .isEqualTo("2.00");

    }

    @Test
    void shortNumberFormatTest() {
        assertThat(numberBetween.generateOutput("2,2"))
                .as("parameters: 2,2")
                .isEqualTo("2.0");

        assertThat(numberBetween.generateOutput("2,2,0"))
                .as("parameters: 2,2,0")
                .isEqualTo("2");

        assertThat(numberBetween.generateOutput("2,2,1"))
                .as("parameters: 2,2,1")
                .isEqualTo("2.0");

        assertThat(numberBetween.generateOutput("2,2,2"))
                .as("parameters: 2,2,2")
                .isEqualTo("2.00");

        assertThat(numberBetween.generateOutput("2,2,3"))
                .as("parameters: 2,2,3")
                .isEqualTo("2.000");

        assertThat(numberBetween.generateOutput("2,2,10"))
                .as("parameters: 2,2,1")
                .isEqualTo("2.0000000000");

    }

    @Test
    void shortNegativeNumberFormatTest() {
        assertThat(numberBetween.generateOutput("-2,-2"))
                .as("parameters: -2,-2")
                .isEqualTo("-2.0");

        assertThat(numberBetween.generateOutput("-2,-2,0"))
                .as("parameters: -2,-2,0")
                .isEqualTo("-2");

        assertThat(numberBetween.generateOutput("-2,-2,1"))
                .as("parameters: -2,-2,1")
                .isEqualTo("-2.0");

        assertThat(numberBetween.generateOutput("-2,-2,2"))
                .as("parameters: -2,-2,2")
                .isEqualTo("-2.00");

        assertThat(numberBetween.generateOutput("-2,-2,3"))
                .as("parameters: -2,-2,3")
                .isEqualTo("-2.000");

        assertThat(numberBetween.generateOutput("-2,-2,10"))
                .as("parameters: -2,-2,1")
                .isEqualTo("-2.0000000000");

    }

    @Test
    void biggerNumberFormatTest() {
        assertThat(numberBetween.generateOutput("200000,200000"))
                .as("parameter: 200000,200000")
                .isEqualTo("200000.0");

        assertThat(numberBetween.generateOutput("200000,200000,0"))
                .as("parameter: 200000,200000,0")
                .isEqualTo("200000");

        assertThat(numberBetween.generateOutput("200000,200000,1"))
                .as("parameter: 200000,200000,1")
                .isEqualTo("200000.0");

        assertThat(numberBetween.generateOutput("200000,200000,2"))
                .as("parameter: 200000,200000,2")
                .isEqualTo("200000.00");

        assertThat(numberBetween.generateOutput("200000,200000,3"))
                .as("parameter: 200000,200000,2")
                .isEqualTo("200000.000");

        assertThat(numberBetween.generateOutput("200000,200000,4"))
                .as("parameter: 200000,200000,2")
                .isEqualTo("200000.0000");

        assertThat(numberBetween.generateOutput("200000,200000,5"))
                .as("parameter: 200000,200000,2")
                .isEqualTo("200000.00000");

    }

    @Test
    void floatNumberFormatTest() {
        assertThat(numberBetween.generateOutput("1999.99,1999.99,2"))
                .isEqualTo("1999.99");

        assertThat(numberBetween.generateOutput("1999.99879,1999.99879,5"))
                .isEqualTo("1999.99879");

        assertThat(numberBetween.generateOutput("1999.99879,1999.99879,2"))
                .isEqualTo("2000.00");

    }

    @Test
    void outputRangeTest2args() {
        assertThat(Double.parseDouble(numberBetween.generateOutput("0, 10")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 10.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 10.0")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 10.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 100.0")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 100.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 1000.0")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 1000.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 10000.0")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 10000.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("\"0.0\", \"10000.0\"")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 10000.0);

    }

    @Test
    void outputRangeTest3rdArgs() {

        assertThat(Double.parseDouble(numberBetween.generateOutput("0, 1000, 0")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 1000.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0, 10, 0")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 10.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 1000.0, 1")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 1000.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 10.0, 1")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 10.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 1000.0, 2")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 1000.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 10.0, 2")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 10.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 1000.0, 3")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 1000.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 10.0, 3")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 10.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 1000.0, 4")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 1000.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 10.0, 4")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 10.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 1000.0, 5")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 1000.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("0.0, 10.0, 5")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 10.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("\"0.0\", \"10.0\", \"5\"")))
                .isNotNull()
                .isNotNegative()
                .isBetween(0.0, 10.0);

    }

    @Test
    void negativeNumberTest() {
        assertThat(Double.parseDouble(numberBetween.generateOutput("-10, -1")))
                .isNotNull()
                .isNegative()
                .isBetween(-10.00, -1.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("-10, -1, 0")))
                .isNotNull()
                .isNegative()
                .isBetween(-10.00, -1.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("-10, -1, 1")))
                .isNotNull()
                .isNegative()
                .isBetween(-10.00, -1.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("-10, -1, 2")))
                .isNotNull()
                .isNegative()
                .isBetween(-10.00, -1.0);

        assertThat(Double.parseDouble(numberBetween.generateOutput("-10, -1, 3")))
                .isNotNull()
                .isNegative()
                .isBetween(-10.00, -1.0);

    }

}
