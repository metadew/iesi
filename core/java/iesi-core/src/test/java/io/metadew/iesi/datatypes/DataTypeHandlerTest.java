package io.metadew.iesi.datatypes;

import io.metadew.iesi.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = DataTypeHandler.class)
@ContextConfiguration(classes = { TestConfiguration.class })
@ActiveProfiles("test")
class DataTypeHandlerTest {

    @Autowired
    DataTypeHandler dataTypeHandler;

    @Test
    void splitInstructionArgumentsSimple() {
        assertEquals(2, dataTypeHandler.splitInstructionArguments("1, 2").size());
        assertEquals("1", dataTypeHandler.splitInstructionArguments("1, 2").get(0));
        assertEquals("2", dataTypeHandler.splitInstructionArguments("1, 2").get(1));
    }
    @Test
    void splitInstructionArgumentsDatasetInstruction() {
        assertEquals(2, dataTypeHandler.splitInstructionArguments("{{^dataset(name, label)}}, 2").size());
        assertEquals("{{^dataset(name, label)}}", dataTypeHandler.splitInstructionArguments("{{^dataset(name, label)}}, 2").get(0));
        assertEquals("2", dataTypeHandler.splitInstructionArguments("{{^dataset(name, label)}}, 2").get(1));
    }
    @Test
    void splitInstructionArgumentsDatasetInstructionLabelInstruction() {
        List splittedArguemnts = dataTypeHandler.splitInstructionArguments("{{^dataset(name, {{^list(label)}})}}, 2");
        assertEquals(2, splittedArguemnts.size());
        assertEquals("{{^dataset(name, {{^list(label)}})}}", splittedArguemnts.get(0));
        assertEquals("2", splittedArguemnts.get(1));
    }

}
