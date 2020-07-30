package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.builder.script.ScriptBuilder;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
class ScriptExecutionDtoServiceTest {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final ScriptExecutionDtoService scriptExecutionDtoService;

    @Autowired
    ScriptExecutionDtoServiceTest(MetadataRepositoryConfiguration metadataRepositoryConfiguration,
                                  ScriptExecutionDtoService scriptExecutionDtoService) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.scriptExecutionDtoService = scriptExecutionDtoService;
    }

    @BeforeAll
    void setUp() {
        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::cleanAllTables);
        populateDB();
    }


    @AfterAll
    @Test
    void emptyDBTest() {
        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::cleanAllTables);
        assertThat(scriptExecutionDtoService.getAll())
                .describedAs("DB is empty and the service should return an empty list")
                .isEmpty();
    }

    @Test
    void getByRunIdAndProcessId() {

    }

    void populateDB() {
        // Simulate a ScriptExecution:
        // 1) Script Insertion with action etc
        // 2) ScriptExecution : insert data as they are after a ScriptExecution

        Script script = ScriptBuilder.simpleScript("scriptTest",0L,3,2,2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script);

    }

    /*
        A ScriptExecutionDto requires:

    // runId and processId = PRIMARY KEYS
    private String runId; // RES_SCRIPT - TRC_DES_SCRIPT - ...
    private Long processId; // RES_SCRIPT - TRC_DES_SCRIPT - ...

    private Long parentProcessId; // RES_SCRIPT
    private String scriptId; // RES_SCRIPT
    private String scriptName; // RES_SCRIPT
    private Long scriptVersion; // RES_SCRIPT
    private String environment; // RES_SCRIPT
    private ScriptRunStatus status; // RES_SCRIPT
    private LocalDateTime startTimestamp; // RES_SCRIPT
    private LocalDateTime endTimestamp; // RES_SCRIPT

    // TRC_DES_SCRIPT_PAR - Primary Key: RunID PrcId ScriptParName
    // InputParameterDto : name, rawValue, ?resolvedValue?
    private List<InputParametersDto> inputParameters = new ArrayList<>();

    // TRC_DES_SCRIPT_LBL - Primary Key: RunId PrcId ScriptLabelId
    // Label: name, value
    private List<ScriptLabelDto> designLabels = new ArrayList<>();

    // EXE_REQ_LBL OR TRC_SCRIPT_LBL
    private List<ExecutionRequestLabelDto> executionLabels = new ArrayList<>();

    // action: runId, processId, type, name, description, condition,
    // stopOnError, expectedError, status, startTimestamp, endTimestamp
    private List<ActionExecutionDto> actions = new ArrayList<>();

    // output: name, value
    private List<OutputDto> output = new ArrayList<>();

    */

}