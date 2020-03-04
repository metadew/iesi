package io.metadew.iesi.datatypes;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataTypeHandlerTest {


    @Test
    void splitInstructionArgumentsSimple() {
        assertEquals(2, DataTypeHandler.getInstance().splitInstructionArguments("1, 2").size());
        assertEquals("1", DataTypeHandler.getInstance().splitInstructionArguments("1, 2").get(0));
        assertEquals("2", DataTypeHandler.getInstance().splitInstructionArguments("1, 2").get(1));
    }
    @Test
    void splitInstructionArgumentsDatasetInstruction() {
        assertEquals(2, DataTypeHandler.getInstance().splitInstructionArguments("{{^dataset(name, label)}}, 2").size());
        assertEquals("{{^dataset(name, label)}}", DataTypeHandler.getInstance().splitInstructionArguments("{{^dataset(name, label)}}, 2").get(0));
        assertEquals("2", DataTypeHandler.getInstance().splitInstructionArguments("{{^dataset(name, label)}}, 2").get(1));
    }
    @Test
    void splitInstructionArgumentsDatasetInstructionLabelInstruction() {
        List splittedArguemnts = DataTypeHandler.getInstance().splitInstructionArguments("{{^dataset(name, {{^list(label)}})}}, 2");
        assertEquals(2, splittedArguemnts.size());
        assertEquals("{{^dataset(name, {{^list(label)}})}}", splittedArguemnts.get(0));
        assertEquals("2", splittedArguemnts.get(1));
    }

}
