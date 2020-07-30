package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.launch.MetadataLauncher;
import io.metadew.iesi.launch.ScriptLauncher;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.builder.script.ScriptBuilder;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

//    @BeforeEach
//    void setUp() {
//        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::cleanAllTables);
//        populateDB();
//    }


    @Test
    void emptyDBTest() {
        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::cleanAllTables);
        assertThat(scriptExecutionDtoService.getAll())
                .describedAs("DB is empty and the service should return an empty list")
                .isEmpty();
    }

    @Test
    void getAll(){

    }

    @Test
    void getByRunId(){

    }

    @Test
    void getByRunIdAndProcessId() {

    }

    @Test
    void populateDB() {
        // Simulate a ScriptExecution:
        // 1) Script Insertion with action etc
        // 2) ScriptExecution : insert data as they are after a ScriptExecution

        Script script1 = ScriptBuilder.simpleScript("oneScriptTest", 0L, 3, 2, 2);
        Script script2 = ScriptBuilder.simpleScript("oneScriptTest", 1L, 3, 2, 2);
        Script script3 = ScriptBuilder.simpleScript("anotherScriptTest", 0L, 3, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script3);

        // TODO: create accurate scriptExecutionRequest and scriptExecution simulation

        // doesn't work: first, the column name sounds to be a problem, second, The framework instance shutdown the DB
        // after its work is done -> impossible to perform test on it + Can't be sure it opens the same DB
//        try {
//            String[] executeScripts = {"-script", "oneScriptTest", "-env", "testEnv"};
//            ScriptLauncher.main(executeScripts);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }


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