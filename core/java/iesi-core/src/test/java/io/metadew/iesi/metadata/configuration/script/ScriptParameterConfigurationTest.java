package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, ScriptParameterConfiguration.class })
@ActiveProfiles("test")
class ScriptParameterConfigurationTest {
    ScriptParameter scriptParameter1;
    ScriptParameter scriptParameter2;

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    ScriptParameterConfiguration scriptParameterConfiguration;


    @BeforeEach
    void setup() {
        scriptParameter1 = new ScriptParameterBuilder("1", 1, "parameter1")
                .value("parameter value")
                .build();
        scriptParameter2 = new ScriptParameterBuilder("1", 1, "parameter2")
                .value("parameter value")
                .build();

        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void scriptParameterNotExistTest() {
        assertFalse(scriptParameterConfiguration.exists(scriptParameter1.getMetadataKey()));
    }

    @Test
    void scriptParameterExistsTest() {
        scriptParameterConfiguration.insert(scriptParameter1);
        assertTrue(scriptParameterConfiguration.exists(scriptParameter1.getMetadataKey()));
    }

    @Test
    void scriptParameterInsertTest() {
        assertEquals(0, scriptParameterConfiguration.getAll().size());

        scriptParameterConfiguration.insert(scriptParameter1);

        assertEquals(1, scriptParameterConfiguration.getAll().size());
        assertTrue(scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).isPresent());
        assertEquals(scriptParameter1, scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).get());
    }

    @Test
    void scriptParameterInsertAlreadyExistsTest() {
        scriptParameterConfiguration.insert(scriptParameter1);
        assertThrows(MetadataAlreadyExistsException.class, () -> scriptParameterConfiguration.insert(scriptParameter1));
    }

    @Test
    void scriptParameterDeleteTest() {
        scriptParameterConfiguration.insert(scriptParameter1);
        assertEquals(1, scriptParameterConfiguration.getAll().size());

        scriptParameterConfiguration.delete(scriptParameter1.getMetadataKey());
        assertEquals(0, scriptParameterConfiguration.getAll().size());
        assertFalse(scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).isPresent());
    }

    @Test
    void scriptParameterDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () -> scriptParameterConfiguration.delete(scriptParameter1.getMetadataKey()));
    }

    @Test
    void scriptParameterGetTest() {
        scriptParameterConfiguration.insert(scriptParameter1);

        assertTrue(scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).isPresent());
        assertEquals(scriptParameter1, scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).get());
    }

    @Test
    void scriptParameterGetNotExistsTest() {
        assertFalse(scriptParameterConfiguration.exists(scriptParameter1));
        assertFalse(scriptParameterConfiguration.get(scriptParameter1.getMetadataKey()).isPresent());
    }

    @Test
    void scriptParameterUpdateTest() {
        scriptParameterConfiguration.insert(scriptParameter1);

        Optional<ScriptParameter> fetchedScriptParameter = scriptParameterConfiguration.get(scriptParameter1.getMetadataKey());
        assertTrue(fetchedScriptParameter.isPresent());
        assertEquals("parameter value", fetchedScriptParameter.get().getValue());

        scriptParameter1.setValue("new value");
        scriptParameterConfiguration.update(scriptParameter1);

        fetchedScriptParameter = scriptParameterConfiguration.get(scriptParameter1.getMetadataKey());
        assertTrue(fetchedScriptParameter.isPresent());
        assertEquals(fetchedScriptParameter.get().getValue(), "new value");
    }

}
