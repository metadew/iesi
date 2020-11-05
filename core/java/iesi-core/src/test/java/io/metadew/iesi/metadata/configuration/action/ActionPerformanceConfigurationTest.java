package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.metadata.configuration.action.performance.ActionPerformanceConfiguration;
import io.metadew.iesi.metadata.definition.action.performance.ActionPerformance;
import io.metadew.iesi.metadata.definition.action.performance.key.ActionPerformanceKey;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import io.metadew.iesi.metadata.repository.ResultMetadataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionPerformanceConfigurationTest {

    private ResultMetadataRepository designMetadataRepository;
    private ActionPerformance actionPerformance;
    private ActionPerformance actionPerformance2;

    @BeforeEach
    void setup() {
        designMetadataRepository = RepositoryTestSetup.getResultMetadataRepository();
        designMetadataRepository.createAllTables();
        actionPerformance = ActionPerformance.builder()
                .context("context")
                .duration(2.8)
//                .stopTimestamp(timestamp.toLocalDateTime())
//                .startTimestamp(timestamp.toLocalDateTime())
                .actionId("id")
                .actionPerformanceKey(ActionPerformanceKey.builder().scope("scope")
                        .procedureId(1L).runId("id2").build())
                .build();

        actionPerformance2 = ActionPerformance.builder()
                .context("context")
                .duration(2.8)
//                .stopTimestamp(dateTime)
//                .startTimestamp(dateTime)
                .actionId("id")
                .actionPerformanceKey(ActionPerformanceKey.builder().scope("scope")
                        .procedureId(1L).runId("id").build())
                .build();
    }

    @AfterEach
    void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }


//    @Test
//    void actionExistsOnlyTest() {
//        ActionPerformanceConfiguration.getInstance().insert(actionPerformance);
//        assertTrue(ActionPerformanceConfiguration.getInstance().exists(actionPerformance.getMetadataKey()));
//    }
}
