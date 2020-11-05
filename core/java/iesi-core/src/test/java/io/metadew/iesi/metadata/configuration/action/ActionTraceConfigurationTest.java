package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.metadata.configuration.action.trace.ActionTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.trace.ActionTrace;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionTraceKey;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import io.metadew.iesi.metadata.repository.TraceMetadataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ActionTraceConfigurationTest {

    private TraceMetadataRepository designMetadataRepository;
    private ActionTrace actionTrace;
    private ActionTrace actionTrace2;

    @BeforeEach
    void setup() {
        designMetadataRepository = RepositoryTestSetup.getTraceMetadataRepository();
        designMetadataRepository.createAllTables();

        actionTrace = ActionTrace.builder()
                .number(1L)
                .retries(1)
                .errorStop("error")
                .errorExpected("error")
                .iteration("ite")
                .component("comp")
                .actionTraceKey(ActionTraceKey.builder().actionId("id").processId(1L).runId("run").build())
                .description("desc")
                .name("name")
                .type("type")
                .condition("cond")
                .build();

        actionTrace2 = ActionTrace.builder()
                .number(2L)
                .retries(1)
                .errorStop("error2")
                .errorExpected("error2")
                .iteration("ite2")
                .component("comp2")
                .actionTraceKey(ActionTraceKey.builder().actionId("id2").processId(2L).runId("run2").build())
                .description("desc2")
                .name("name2")
                .type("type2")
                .condition("cond2")
                .build();
    }

    @AfterEach
    void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }


    @Test
    void actionExistsOnlyTest() {
        ActionTraceConfiguration.getInstance().insert(actionTrace);
        assertTrue(ActionTraceConfiguration.getInstance().exists(actionTrace.getMetadataKey()));
    }

    @Test
    void actionGetAll() {
        ActionTraceConfiguration.getInstance().insert(actionTrace);
        ActionTraceConfiguration.getInstance().insert(actionTrace2);
        assertEquals(2, ActionTraceConfiguration.getInstance().getAll().size());
        assertEquals(Stream.of(ActionTraceConfiguration.getInstance().get(actionTrace.getMetadataKey()).get()
                , (ActionTraceConfiguration.getInstance().get(actionTrace2.getMetadataKey()).get()))
                .collect(Collectors.toList()), ActionTraceConfiguration.getInstance().getAll());
    }
}
