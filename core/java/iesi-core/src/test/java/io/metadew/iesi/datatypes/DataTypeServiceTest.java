package io.metadew.iesi.datatypes;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataTypeServiceTest {


    @Test
    void splitInstructionArgumentsSimple() {
        DataTypeService dataTypeService = new DataTypeService();
        assertEquals(2, dataTypeService.splitInstructionArguments("1, 2").size());
        assertEquals("1", dataTypeService.splitInstructionArguments("1, 2").get(0));
        assertEquals("2", dataTypeService.splitInstructionArguments("1, 2").get(1));
    }
    @Test
    void splitInstructionArgumentsDatasetInstruction() {
        DataTypeService dataTypeService = new DataTypeService();
        assertEquals(2, dataTypeService.splitInstructionArguments("{{^dataset(name, label)}}, 2").size());
        assertEquals("{{^dataset(name, label)}}", dataTypeService.splitInstructionArguments("{{^dataset(name, label)}}, 2").get(0));
        assertEquals("2", dataTypeService.splitInstructionArguments("{{^dataset(name, label)}}, 2").get(1));
    }
    @Test
    void splitInstructionArgumentsDatasetInstructionLabelInstruction() {
        DataTypeService dataTypeService = new DataTypeService();
        List splittedArguemnts = dataTypeService.splitInstructionArguments("{{^dataset(name, {{^list(label)}})}}, 2");
        assertEquals(2, splittedArguemnts.size());
        assertEquals("{{^dataset(name, {{^list(label)}})}}", splittedArguemnts.get(0));
        assertEquals("2", splittedArguemnts.get(1));
    }

}
