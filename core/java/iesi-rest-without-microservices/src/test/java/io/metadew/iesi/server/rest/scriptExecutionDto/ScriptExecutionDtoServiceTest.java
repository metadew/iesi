//package io.metadew.iesi.server.rest.scriptExecutionDto;
//
//import io.metadew.iesi.common.configuration.ScriptRunStatus;
//import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
//import io.metadew.iesi.metadata.definition.environment.Environment;
//import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
//import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
//import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilder;
//import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
//import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
//import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
//import io.metadew.iesi.metadata.definition.execution.script.*;
//import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
//import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
//import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
//import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
//import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
//import io.metadew.iesi.metadata.definition.script.Script;
//import io.metadew.iesi.metadata.repository.MetadataRepository;
//import io.metadew.iesi.metadata.tools.IdentifierTools;
//import io.metadew.iesi.script.execution.ScriptExecutionBuilder;
//import io.metadew.iesi.server.rest.Application;
//import io.metadew.iesi.server.rest.builder.script.ScriptBuilder;
//import io.metadew.iesi.server.rest.configuration.TestConfiguration;
//import org.apache.commons.codec.digest.DigestUtils;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.UUID;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
//@ContextConfiguration(classes = TestConfiguration.class)
//@ActiveProfiles("test")
//class ScriptExecutionDtoServiceTest {
//
//    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
//    private final ScriptExecutionDtoService scriptExecutionDtoService;
//
//    @Autowired
//    ScriptExecutionDtoServiceTest(MetadataRepositoryConfiguration metadataRepositoryConfiguration,
//                                  ScriptExecutionDtoService scriptExecutionDtoService) {
//        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
//        this.scriptExecutionDtoService = scriptExecutionDtoService;
//    }
//
////    @BeforeEach
////    void setUp() {
////        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::cleanAllTables);
////        populateDB();
////    }
//
//
//    @Test
//    void emptyDBTest() {
//        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::cleanAllTables);
//        assertThat(scriptExecutionDtoService.getAll())
//                .describedAs("DB is empty and the service should return an empty list")
//                .isEmpty();
//    }
//
////    @Test
////    void getAll() throws ExecutionRequestBuilderException {
////        populateDB();
////        System.out.println(scriptExecutionDtoService.getAll());
////    }
////
////    @Test
////    void getByRunId() {
////
////    }
////
////    @Test
////    void getByRunIdAndProcessId() {
////
////    }
//
//    void populateDB() throws ExecutionRequestBuilderException {
//        // Simulate a ScriptExecution:
//        // 1) Script Insertion with action etc
//        // 2) ScriptExecution : insert data as they are after a ScriptExecution
//
//        Script script1 = ScriptBuilder.simpleScript("oneScriptTest", 0L, 3, 2, 2);
//        Script script2 = ScriptBuilder.simpleScript("oneScriptTest", 1L, 3, 2, 2);
//        Script script3 = ScriptBuilder.simpleScript("anotherScriptTest", 0L, 3, 2, 2);
//        Environment environment = new Environment(new EnvironmentKey("tst"), "description", new ArrayList<>());
//        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1);
//        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2);
//        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script3);
//        metadataRepositoryConfiguration.getConnectivityMetadataRepository().save(environment);
//
//        String executionRequestId = UUID.randomUUID().toString();
//        String scriptExecutionRequestId = UUID.randomUUID().toString();
//
//        io.metadew.iesi.script.execution.ScriptExecution scriptExecution = new ScriptExecutionBuilder(true, false)
//                .script(script1)
//                .exitOnCompletion(false)
//                .parameters(new HashMap<>())
//                .impersonations(new HashMap<>())
//                .environment("tst")
//                .build();
//
//        scriptExecution.execute();
//        // TODO: create accurate scriptExecutionRequest and scriptExecution simulation
//
////
////        ExecutionRequest executionRequest = new ExecutionRequestBuilder()
////                .id(executionRequestId)
////                .name("name")
////                .context("context1")
////                .description("description")
////                .scope("scope")
////                .scriptExecutionRequests(Stream.of(
////                        new ScriptNameExecutionRequest(
////                                new ScriptExecutionRequestKey(scriptExecutionRequestId),
////                                new ExecutionRequestKey(executionRequestId),
////                                "tst",
////                                true,
//////                                Stream.of(
//////                                        new ScriptExecutionRequestImpersonation(
//////                                                new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()),
//////                                                new ScriptExecutionRequestKey(scriptExecutionRequestId),
//////                                                new ImpersonationKey("name1")),
//////                                        new ScriptExecutionRequestImpersonation(
//////                                                new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()),
//////                                                new ScriptExecutionRequestKey(scriptExecutionRequestId),
//////                                                new ImpersonationKey("name2")))
//////                                        .collect(Collectors.toList()),
////                                new ArrayList<>(),
////                                Stream.of(new ScriptExecutionRequestParameter(
////                                                new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()),
////                                                new ScriptExecutionRequestKey(scriptExecutionRequestId),
////                                                "param1",
////                                                "value1"),
////                                        new ScriptExecutionRequestParameter(
////                                                new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()),
////                                                new ScriptExecutionRequestKey(scriptExecutionRequestId),
////                                                "param2",
////                                                "value2")).collect(Collectors.toList()),
////                                ScriptExecutionRequestStatus.COMPLETED,
////                                "script",
////                                1L)
////                ).collect(Collectors.toList()))
////                .build();
////        executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.COMPLETED);
////
////        String runId = UUID.randomUUID().toString();
////        String scriptExecutionId = UUID.randomUUID().toString();
////        ScriptExecution scriptExecution =
////                new ScriptExecution(new ScriptExecutionKey(scriptExecutionId),
////                        new ScriptExecutionRequestKey(scriptExecutionRequestId), runId,
////                        ScriptRunStatus.SUCCESS, LocalDateTime.now(), LocalDateTime.now().plus(1, ChronoUnit.SECONDS));
////
////
////
////
//    }
//
//}