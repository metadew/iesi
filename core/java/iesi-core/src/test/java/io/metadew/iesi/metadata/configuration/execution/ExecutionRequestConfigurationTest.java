package io.metadew.iesi.metadata.configuration.execution;

import io.metadew.iesi.metadata.definition.execution.*;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import io.metadew.iesi.metadata.definition.execution.script.*;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.repository.ExecutionServerMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

class ExecutionRequestConfigurationTest {

    private ExecutionServerMetadataRepository executionServerMetadataRepository;
    private AuthenticatedExecutionRequest executionRequest1;
    private AuthenticatedExecutionRequest executionRequest2;
    private NonAuthenticatedExecutionRequest nonAuthenticatedExecutionRequest;
    private NonAuthenticatedExecutionRequest nonAuthenticatedExecutionRequest2;

    @BeforeEach
    void setup() {
        executionServerMetadataRepository = RepositoryTestSetup.getExecutionServerMetadataRepository();
        executionServerMetadataRepository.createAllTables();
        Set<ExecutionRequestLabel> executionRequestLabelList = new HashSet<>();
        ExecutionRequestLabel executionRequestLabel = ExecutionRequestLabel.builder()
                .executionRequestKey(ExecutionRequestKey
                        .builder().id("id").build()).metadataKey(ExecutionRequestLabelKey.builder().id("id").build()).build();
        executionRequestLabelList.add(executionRequestLabel);
        List<ScriptExecutionRequest> scriptExecutionRequests = new ArrayList<>();
        List<ScriptExecutionRequestImpersonation> scriptExecutionRequestImpersonations = new ArrayList<>();
        ScriptExecutionRequestImpersonation scriptExecutionRequestImpersonation = ScriptExecutionRequestImpersonation.builder()
                .impersonationKey(ImpersonationKey.builder().name("name").build()).scriptExecutionRequestImpersonationKey(ScriptExecutionRequestImpersonationKey.builder().id("id").build())
                .scriptExecutionRequestKey(ScriptExecutionRequestKey.builder().id("id").build()).build();
        scriptExecutionRequestImpersonations.add(scriptExecutionRequestImpersonation);
        ScriptExecutionRequestParameter scriptExecutionRequestParameter = ScriptExecutionRequestParameter.builder()
                .scriptExecutionRequestParameterKey(ScriptExecutionRequestParameterKey.builder().id("id").build()).scriptExecutionRequestKey(ScriptExecutionRequestKey.builder().id("id").build()).name("name").value("value").build();
        List<ScriptExecutionRequestParameter> scriptExecutionRequestParameterList = new ArrayList<>();
        scriptExecutionRequestParameterList.add(scriptExecutionRequestParameter);
        ScriptFileExecutionRequest scriptExecutionRequest = ScriptFileExecutionRequest.builder()
                .scriptExecutionRequestKey(ScriptExecutionRequestKey.builder().id("id").build())
                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.ABORTED)
                .executionRequestKey(ExecutionRequestKey.builder().id("id").build())
                .exit(true)
                .fileName("filename")
                .impersonations(scriptExecutionRequestImpersonations)
                .environment("env")
                .parameters(scriptExecutionRequestParameterList)
                .build();
        scriptExecutionRequests.add(scriptExecutionRequest);
        LocalDateTime a = LocalDateTime.of(2017, 2, 13, 15, 56);
        executionRequest1 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(ExecutionRequestKey
                        .builder().id("id").build())
                .user("user")
                .description("desc")
                .requestTimestamp(a)
                .context("context")
                .name("name")
                .password("password")
                .scope("scope")
                .space("space")
                .email("email")
                .executionRequestStatus(ExecutionRequestStatus.SUBMITTED)
                .executionRequestLabels(executionRequestLabelList)
                .scriptExecutionRequests(scriptExecutionRequests)
                .build();
        nonAuthenticatedExecutionRequest = NonAuthenticatedExecutionRequest.builder()
                .executionRequestKey(ExecutionRequestKey
                        .builder().id("id").build())
                .description("descNonAuth")
                .requestTimestamp(a)
                .context("contextNonAuth")
                .name("nameNonAuth")
                .scope("scopeNonAuth")
                .email("emailNonAuth")
                .executionRequestStatus(ExecutionRequestStatus.SUBMITTED)
                .executionRequestLabels(executionRequestLabelList)
                .scriptExecutionRequests(scriptExecutionRequests)
                .build();

        Set<ExecutionRequestLabel> executionRequestLabelList2 = new HashSet<>();
        ExecutionRequestLabel executionRequestLabel2 = ExecutionRequestLabel.builder()
                .executionRequestKey(ExecutionRequestKey
                        .builder().id("ide2").build()).metadataKey(ExecutionRequestLabelKey.builder().id("ide2").build()).build();
        executionRequestLabelList2.add(executionRequestLabel2);
        List<ScriptExecutionRequest> scriptExecutionRequests2 = new ArrayList<>();
        List<ScriptExecutionRequestImpersonation> scriptExecutionRequestImpersonations2 = new ArrayList<>();
        ScriptExecutionRequestImpersonation scriptExecutionRequestImpersonation2 = ScriptExecutionRequestImpersonation.builder()
                .impersonationKey(ImpersonationKey.builder().name("name").build()).scriptExecutionRequestImpersonationKey(ScriptExecutionRequestImpersonationKey.builder().id("ide2").build())
                .scriptExecutionRequestKey(ScriptExecutionRequestKey.builder().id("ide2").build()).build();
        scriptExecutionRequestImpersonations2.add(scriptExecutionRequestImpersonation2);
        ScriptExecutionRequestParameter scriptExecutionRequestParameter2 = ScriptExecutionRequestParameter.builder()
                .scriptExecutionRequestParameterKey(ScriptExecutionRequestParameterKey.builder().id("ide2").build()).scriptExecutionRequestKey(ScriptExecutionRequestKey.builder().id("ide2").build()).name("name").value("value").build();
        List<ScriptExecutionRequestParameter> scriptExecutionRequestParameterList2 = new ArrayList<>();
        scriptExecutionRequestParameterList2.add(scriptExecutionRequestParameter2);
        ScriptFileExecutionRequest scriptExecutionRequest2 = ScriptFileExecutionRequest.builder()
                .scriptExecutionRequestKey(ScriptExecutionRequestKey.builder().id("ide2").build())
                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.ABORTED)
                .executionRequestKey(ExecutionRequestKey.builder().id("ide2").build())
                .exit(true)
                .fileName("filename")
                .impersonations(scriptExecutionRequestImpersonations2)
                .environment("env")
                .parameters(scriptExecutionRequestParameterList2)
                .build();
        scriptExecutionRequests2.add(scriptExecutionRequest2);
        LocalDateTime b = LocalDateTime.of(2017, 2, 13, 15, 56);

        List<ScriptExecutionRequest> scriptExecutionRequestsName = new ArrayList<>();
        ScriptNameExecutionRequest scriptNameExecutionRequest = ScriptNameExecutionRequest.builder()
                .scriptExecutionRequestKey(ScriptExecutionRequestKey.builder().id("ide2").build())
                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.ABORTED)
                .executionRequestKey(ExecutionRequestKey.builder().id("ide2").build())
                .exit(true)
                .scriptName("scriptName")
                .impersonations(scriptExecutionRequestImpersonations2)
                .environment("env")
                .parameters(scriptExecutionRequestParameterList2)
                .build();
        scriptExecutionRequestsName.add(scriptNameExecutionRequest);

        executionRequest2 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(ExecutionRequestKey
                        .builder().id("ide2").build())
                .user("user")
                .description("desc")
                .requestTimestamp(b)
                .context("context")
                .name("name")
                .password("password")
                .scope("scope")
                .space("space")
                .email("email")
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .executionRequestLabels(executionRequestLabelList2)
                .scriptExecutionRequests(scriptExecutionRequests2)
                .build();

        nonAuthenticatedExecutionRequest2 = NonAuthenticatedExecutionRequest.builder()
                .executionRequestKey(ExecutionRequestKey
                        .builder().id("ide2").build())
                .description("descNonAuth2")
                .requestTimestamp(b)
                .context("contextNonAuth2")
                .name("nameNonAuth2")
                .scope("scopeNonAuth2")
                .email("emailNonAuth2")
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .executionRequestLabels(executionRequestLabelList2)
                .scriptExecutionRequests(scriptExecutionRequestsName)
                .build();

    }

    @AfterEach
    void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        executionServerMetadataRepository.dropAllTables();
    }

    @Test
    void executionRequestExistsTest() {
        assertThat(ExecutionRequestConfiguration.getInstance().getAll())
                .isEmpty();
    }

    @Test
    void executionRequestInsertTest() {
        ExecutionRequestConfiguration.getInstance().insert(executionRequest1);
        assertEquals(1, ExecutionRequestConfiguration.getInstance().getAll().size());
        assertEquals(Stream.of(ExecutionRequestConfiguration.getInstance().get(executionRequest1.getMetadataKey()).get()).collect(Collectors.toList()), ExecutionRequestConfiguration.getInstance().getAll());
    }

    @Test
    void executionRequestInsertTestNonAuth() {
        ExecutionRequestConfiguration.getInstance().insert(nonAuthenticatedExecutionRequest);
        assertEquals(1, ExecutionRequestConfiguration.getInstance().getAll().size());
        assertEquals(Stream.of(ExecutionRequestConfiguration.getInstance().get(nonAuthenticatedExecutionRequest.getMetadataKey()).get()).collect(Collectors.toList()), ExecutionRequestConfiguration.getInstance().getAll());
    }

    @Test
    void executionRequestGetTest() {
        ExecutionRequestConfiguration.getInstance().insert(executionRequest1);
        Optional<ExecutionRequest> fetchedComponentVersion1 = ExecutionRequestConfiguration.getInstance().get(executionRequest1.getMetadataKey());
        assertEquals(fetchedComponentVersion1, ExecutionRequestConfiguration.getInstance().get(executionRequest1.getMetadataKey()));
        assertEquals(Stream.of(ExecutionRequestConfiguration.getInstance().get(executionRequest1.getMetadataKey()).get()).collect(Collectors.toList()), ExecutionRequestConfiguration.getInstance().getAll());
    }

    @Test
    void executionRequestGetTestNonAuth() {
        ExecutionRequestConfiguration.getInstance().insert(nonAuthenticatedExecutionRequest);
        Optional<ExecutionRequest> fetchedComponentVersion1 = ExecutionRequestConfiguration.getInstance().get(nonAuthenticatedExecutionRequest.getMetadataKey());
        assertEquals(fetchedComponentVersion1, ExecutionRequestConfiguration.getInstance().get(nonAuthenticatedExecutionRequest.getMetadataKey()));
        assertEquals(Stream.of(ExecutionRequestConfiguration.getInstance().get(nonAuthenticatedExecutionRequest.getMetadataKey()).get()).collect(Collectors.toList()), ExecutionRequestConfiguration.getInstance().getAll());
    }

    @Test
    void executionRequestGetAllTest() {
        ExecutionRequestConfiguration.getInstance().insert(executionRequest1);
        ExecutionRequestConfiguration.getInstance().insert(executionRequest2);
        assertEquals(2, ExecutionRequestConfiguration.getInstance().getAll().size());
        assertEquals(Stream.of(ExecutionRequestConfiguration.getInstance().get(executionRequest2.getMetadataKey()).get(), ExecutionRequestConfiguration.getInstance().get(executionRequest1.getMetadataKey()).get())
                .collect(Collectors.toList()), ExecutionRequestConfiguration.getInstance().getAll());
    }

    @Test
    void executionRequestGetAllTestNonAuth() {
        ExecutionRequestConfiguration.getInstance().insert(nonAuthenticatedExecutionRequest);
        ExecutionRequestConfiguration.getInstance().insert(nonAuthenticatedExecutionRequest2);
        assertEquals(2, ExecutionRequestConfiguration.getInstance().getAll().size());
        assertEquals(Stream.of(ExecutionRequestConfiguration.getInstance().get(nonAuthenticatedExecutionRequest2.getMetadataKey()).get(), ExecutionRequestConfiguration.getInstance().get(nonAuthenticatedExecutionRequest.getMetadataKey()).get())
                .collect(Collectors.toList()), ExecutionRequestConfiguration.getInstance().getAll());
    }

    @Test
    void executionRequestGetAllTestNew() {
        ExecutionRequestConfiguration.getInstance().insert(executionRequest1);
        ExecutionRequestConfiguration.getInstance().insert(executionRequest2);
        assertEquals(1, ExecutionRequestConfiguration.getInstance().getAllNew().size());
        assertEquals(Stream.of(ExecutionRequestConfiguration.getInstance().get(executionRequest2.getMetadataKey()).get(), ExecutionRequestConfiguration.getInstance().get(executionRequest1.getMetadataKey()).get())
                .collect(Collectors.toList()), ExecutionRequestConfiguration.getInstance().getAll());
    }

    @Test
    void executionRequestDeleteTest() {
        ExecutionRequestConfiguration.getInstance().insert(executionRequest1);
        ExecutionRequestConfiguration.getInstance().insert(executionRequest2);
        ExecutionRequestConfiguration.getInstance().delete(executionRequest1.getMetadataKey());
        assertEquals(1, ExecutionRequestConfiguration.getInstance().getAll().size());
        Optional<ExecutionRequest> fetchedComponentVersion1 = ExecutionRequestConfiguration.getInstance().get(executionRequest2.getMetadataKey());
        assertEquals(fetchedComponentVersion1, ExecutionRequestConfiguration.getInstance().get(executionRequest2.getMetadataKey()));
    }

    @Test
    void executionRequestUpdateTest() {
        ExecutionRequestConfiguration.getInstance().insert(executionRequest1);
        ExecutionRequestConfiguration.getInstance().insert(executionRequest2);
        assertEquals(2, ExecutionRequestConfiguration.getInstance().getAll().size());
        Optional<ExecutionRequest> fetchedComponentVersion1 = ExecutionRequestConfiguration.getInstance().get(executionRequest1.getMetadataKey());
        assertEquals(fetchedComponentVersion1, ExecutionRequestConfiguration.getInstance().get(executionRequest1.getMetadataKey()));
        fetchedComponentVersion1.get().setContext("new Context");

        List<ScriptExecutionRequestImpersonation> scriptExecutionRequestImpersonations3 = new ArrayList<>();
        ScriptExecutionRequestImpersonation scriptExecutionRequestImpersonation3 = ScriptExecutionRequestImpersonation.builder()
                .impersonationKey(ImpersonationKey.builder().name("name").build()).scriptExecutionRequestImpersonationKey(ScriptExecutionRequestImpersonationKey.builder().id("ide2").build())
                .scriptExecutionRequestKey(ScriptExecutionRequestKey.builder().id("ide2").build()).build();
        scriptExecutionRequestImpersonations3.add(scriptExecutionRequestImpersonation3);
        ScriptExecutionRequestParameter scriptExecutionRequestParameter3 = ScriptExecutionRequestParameter.builder()
                .scriptExecutionRequestParameterKey(ScriptExecutionRequestParameterKey.builder().id("ide2").build()).scriptExecutionRequestKey(ScriptExecutionRequestKey.builder().id("ide2").build()).name("name3").value("value3").build();
        List<ScriptExecutionRequestParameter> scriptExecutionRequestParameterList3 = new ArrayList<>();
        scriptExecutionRequestParameterList3.add(scriptExecutionRequestParameter3);
        ScriptFileExecutionRequest scriptExecutionRequest3 = ScriptFileExecutionRequest.builder()
                .scriptExecutionRequestKey(ScriptExecutionRequestKey.builder().id("ide2").build())
                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.ABORTED)
                .executionRequestKey(ExecutionRequestKey.builder().id("ide2").build())
                .exit(true)
                .fileName("filename3")
                .impersonations(scriptExecutionRequestImpersonations3)
                .environment("env3")
                .parameters(scriptExecutionRequestParameterList3)
                .build();
        List<ScriptExecutionRequest> scriptExecutionRequests3 = new ArrayList<>();
        scriptExecutionRequests3.add(scriptExecutionRequest3);
        fetchedComponentVersion1.get().setScriptExecutionRequests(scriptExecutionRequests3);

        assertEquals("new Context", fetchedComponentVersion1.get().getContext());

        assertEquals(scriptExecutionRequests3, fetchedComponentVersion1.get().getScriptExecutionRequests());

    }
}
