package io.metadew.iesi.script.execution.instruction.data.date;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateTravelTest {

    @Test
    void generateOutputYear() {
        DateTravel dateTravel = new DateTravel();
        assertEquals("01052010", dateTravel.generateOutput("01052000, \"year\", 10"));
    }

    @Test
    void generateOutputMonth() {
        DateTravel dateTravel = new DateTravel();
        assertEquals("01112000", dateTravel.generateOutput("01012000, \"month\", 10"));
    }

    @Test
    void generateOutputMonthOverflow() {
        DateTravel dateTravel = new DateTravel();
        assertEquals("01012001", dateTravel.generateOutput("01012000, \"month\", 12"));
    }

    @Test
    void generateOutputDay() {
        DateTravel dateTravel = new DateTravel();
        assertEquals("11052000", dateTravel.generateOutput("01052000, \"day\", 10"));
    }

    @Test
    void generateOutputDayBack() {
        DateTravel dateTravel = new DateTravel();
        assertEquals("01052000", dateTravel.generateOutput("11052000, \"day\", -10"));
    }

    @Test
    void generateOutputDayOverflow() {
        DateTravel dateTravel = new DateTravel();
        assertEquals("01062000", dateTravel.generateOutput("01052000, \"day\", 31"));
    }

    @Test
    void generateOutputParseError() {
        DateTravel dateTravel = new DateTravel();
        assertThrows(IllegalArgumentException.class, () -> dateTravel.generateOutput("xxyyzzzz, \"day\", 31"));
    }

    @Test
    void generateOutputInputError() {
        DateTravel dateTravel = new DateTravel();
        assertThrows(IllegalArgumentException.class, () -> dateTravel.generateOutput("test, test, test"));
    }

    @Test
    void generateOutputWorkingDayForward() {
        DateTravel dateTravel = new DateTravel();
        assertEquals("18112019", dateTravel.generateOutput("16112019, \"day\", 1, w"));
    }

    @Test
    void generateOutputWorkingDayBackward() {
        DateTravel dateTravel = new DateTravel();
        assertEquals("15112019", dateTravel.generateOutput("18112019, \"day\", -2, W"));
    }

    @Test
    void generateOutputWeekendDayForward() {
        DateTravel dateTravel = new DateTravel();
        assertEquals("23112019", dateTravel.generateOutput("18112019, \"day\", 3, NW"));
    }

    @Test
    void generateOutputWeekendDayBackward() {
        DateTravel dateTravel = new DateTravel();
        assertEquals("10112019", dateTravel.generateOutput("18112019, \"day\", -3, nW"));
    }

    @Test
    void generateOutPutWeekendDayForwardYear(){
        DateTravel dateTravel = new DateTravel();
        assertEquals("23112019", dateTravel.generateOutput("19112018, \"year\", 1, NW"));
    }

    @Test
    void generateOutputIllegalArgument() {
        DateTravel dateTravel = new DateTravel();
        assertThrows(IllegalArgumentException.class, () -> dateTravel.generateOutput("19112018, \"day\", 31, illegal"));
    }

}
