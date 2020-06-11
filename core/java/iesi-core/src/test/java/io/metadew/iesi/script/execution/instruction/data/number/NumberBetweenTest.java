package io.metadew.iesi.script.execution.instruction.data.number;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NumberBetweenTest {

    NumberBetween numberBetween = new NumberBetween();

    @Test
    void illegalInput() {
        assertAll(()->{
            assertThrows(IllegalArgumentException.class, () -> numberBetween.generateOutput(""), "parameters : \"\"");
            assertThrows(IllegalArgumentException.class, () -> numberBetween.generateOutput(" Illegal, 5"), "parameters: \" Illegal, 5\"");
            assertThrows(IllegalArgumentException.class, () -> numberBetween.generateOutput(" 5, Illegal"), "parameters: \" 5, Illegal\"");
            assertThrows(IllegalArgumentException.class, () -> numberBetween.generateOutput(" Illegal, Illegal"), "parameters: \" Illegal, Illegal\"");
            assertThrows(IllegalArgumentException.class, () -> numberBetween.generateOutput(" Illegal, 5, 32"), "parameters: \" Illegal, 5, 32\"");
            assertThrows(IllegalArgumentException.class, () -> numberBetween.generateOutput(" 2  , 5 , -1"), "parameters: \" 2  , 5 , -1\"");
            assertThrows(IllegalArgumentException.class, () -> numberBetween.generateOutput("   , 5 , 1"), "parameters: \"   , 5 , 1\"");
            assertThrows(IllegalArgumentException.class, () -> numberBetween.generateOutput(" 5  ,  , 1"), "parameters: \" 5  ,  , 1\"");
            assertThrows(IllegalArgumentException.class, () -> numberBetween.generateOutput(" 5  , 2 ,  "),"parameters: \" 5  , 2 ,  \"");
        });
    }

    @Test
    void shortNumberFormatTests() {
        assertAll(()->{
            assertEquals("2", numberBetween.generateOutput("2,2,0"),"parameters: 2,2,0");
            assertEquals("2.0", numberBetween.generateOutput("2,2,1"),"parameters: 2,2,1");
            assertEquals("2.0", numberBetween.generateOutput("2,2"),"parameters: 2,2");
            assertEquals("2.00", numberBetween.generateOutput("2,2,2"),"parameters: 2,2,2");
            assertEquals("2.000", numberBetween.generateOutput("2,2,3"),"parameters: 2,2,3");
            assertEquals("2.0000000000", numberBetween.generateOutput("2,2,10"),"parameters: 2,2,10");
        });
    }

    @Test
    void biggerNumberFormatTest(){
        assertEquals("2000.0" , numberBetween.generateOutput("2000,2000"), "parameter: 2000,2000");
        assertEquals("2000" , numberBetween.generateOutput("2000,2000,0"), "parameter: 2000,2000,0");
        assertEquals("2000.0" , numberBetween.generateOutput("2000,2000,1"), "parameter: 2000,2000,1");
        assertEquals("2000.00" , numberBetween.generateOutput("2000,2000,2"), "parameter: 2000,2000,2");
    }

    @Test
    void floatNumberFormatTest(){
        assertEquals("1999.99",numberBetween.generateOutput("1999.99,1999.99,2"));
        assertEquals("1999.99879",numberBetween.generateOutput("1999.99879,1999.99879,5"));
        assertEquals("2000.00",numberBetween.generateOutput("1999.99879,1999.99879,2"));
    }

}
