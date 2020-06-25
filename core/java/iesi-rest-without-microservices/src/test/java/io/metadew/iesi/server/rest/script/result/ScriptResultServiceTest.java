package io.metadew.iesi.server.rest.script.result;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
public class ScriptResultServiceTest {

    @Autowired
    private IScriptResultService scriptResultService;

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ScriptResultConfiguration scriptResultConfiguration;

    @BeforeEach
    void setup() {
        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::cleanAllTables);
    }

    @Test
    void getAllNoResultAndTypeReturnedTest() {
        assertThat(scriptResultService.getAll())
                .as("The returned type should a List implementation")
                .isInstanceOf(List.class);

        assertThat(scriptResultService.getAll().size())
                .as("The returned List should be empty")
                .isEqualTo(0);
    }

    @Test
    void getAll3ResultsTest() {
        ScriptResult insertedScriptResult0 = createAndInsertADummyScriptResultN(0);
        ScriptResult insertedScriptResult1 = createAndInsertADummyScriptResultN(1);
        ScriptResult insertedScriptResult2 = createAndInsertADummyScriptResultN(2);
        List<ScriptResult> scriptResultList = scriptResultService.getAll();

        assertThat(scriptResultList.size())
                .as("There should be 3 ScriptResult")
                .isEqualTo(3);

        assertThat(scriptResultList)
                .as("getAll method should retrieve the exact same data that were inserted")
                .containsOnly(insertedScriptResult0,insertedScriptResult1,insertedScriptResult2);

        assertThat(scriptResultList.get(0))
                .as("getAll method should retrieve the exact same data that were inserted")
                .isEqualToComparingFieldByField(insertedScriptResult0);

        assertThat(scriptResultList.get(1))
                .as("getAll method should retrieve the exact same data that were inserted")
                .isEqualToComparingFieldByField(insertedScriptResult1);

        assertThat(scriptResultList.get(2))
                .as("getAll method should retrieve the exact same data that were inserted")
                .isEqualToComparingFieldByField(insertedScriptResult2);

    }

    @Test
    void getByRunIdNoScriptResultEmptyDBTest() {
        assertThat(scriptResultService.getByRunId("1"))
                .as("The returned type should an implementation of List")
                .isInstanceOf(List.class);

        assertThat(scriptResultService.getByRunId("1").size())
                .as("The returned list and should be empty")
                .isEqualTo(0);
    }

    @Test
    void getByRunId2IDTest() {
        ScriptResult scriptResultId1n1 = createAndInsertADummyScriptResultN(1,"Id1");
        ScriptResult scriptResultId1n2 = createAndInsertADummyScriptResultN(2,"Id1");
        ScriptResult scriptResultId2n1 = createAndInsertADummyScriptResultN(3,"Id2");

        List<ScriptResult> scriptResultListId1 = scriptResultService.getByRunId("Id1");
        List<ScriptResult> scriptResultListId2 = scriptResultService.getByRunId("Id2");
        List<ScriptResult> scriptResultListNotContainedId = scriptResultService.getByRunId("NotContainedId");

        assertThat(scriptResultListId1)
                .as("There should be 2 and only 2 ScriptResult with Id1 retrieved")
                .containsOnly(scriptResultId1n1, scriptResultId1n2);

        assertThat(scriptResultListId2)
                .as("There should 1 and only 1 ScriptResult with Id2 retrieved")
                .containsOnly(scriptResultId2n1);

        assertThat(scriptResultListNotContainedId.size())
                .as("There shouldn't be any result in this list")
                .isEqualTo(0);

    }

    @Test
    void getByRunIdAndProcessIdTypeReturnedAndEmptyDBTest() {
        assertThat(scriptResultService.getByRunIdAndProcessId("", 1L))
                .as("The returned type should be an optional")
                .isInstanceOf(Optional.class);

        assertThat(scriptResultService.getByRunIdAndProcessId("", 1L).isPresent())
                .as("DB should be empty : No ScriptResult should be found")
                .isFalse();
    }

    @Test
    void getByRunIdAndProcessIdTest() {
        ScriptResult scriptResult1 = createAndInsertADummyScriptResultN(1);
        ScriptResult scriptResult2 = createAndInsertADummyScriptResultN(2);
        ScriptResult scriptResult3 = createAndInsertADummyScriptResultN(3);

        assertThat(scriptResultService.getByRunIdAndProcessId("notPresentRunId", 1L).isPresent())
                .as("No ScriptResult should be found")
                .isFalse();

        Optional<ScriptResult> requestedScriptResult = scriptResultService.getByRunIdAndProcessId("1", 1L);

        assertThat(requestedScriptResult.isPresent())
                .as("One ScriptResult should be returned")
                .isTrue();

        assertThat(requestedScriptResult.get())
                .as("The retrieved ScriptResult should be equal to the inserted one")
                .isEqualTo(scriptResult1);

        requestedScriptResult = scriptResultService.getByRunIdAndProcessId("2", 2L);

        assertThat(requestedScriptResult.isPresent())
                .as("One ScriptResult should be returned")
                .isTrue();

        assertThat(requestedScriptResult.get())
                .as("The retrieved ScriptResult should be equal to the inserted one")
                .isEqualTo(scriptResult2);
    }

    ScriptResult createAndInsertADummyScriptResultN(long n, String runId) {
        ScriptResult scriptResultN = createADummyScriptResult(n, runId);
        scriptResultConfiguration.insert(scriptResultN);
        return scriptResultN;
    }

    ScriptResult createAndInsertADummyScriptResultN(long n) {
        return createAndInsertADummyScriptResultN(n, String.format("%s", n));
    }

    ScriptResult createADummyScriptResult(long n, String runId) {
        return ScriptResult.builder()
                .scriptResultKey(new ScriptResultKey(runId, n))
                .parentProcessId(n)
                .scriptId(Long.toString(n))
                .scriptName(Long.toString(n))
                .scriptVersion(n)
                .environment(Long.toString(n))
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now())
                .build();
    }

    ScriptResult createADummyScriptResult(long n) {
        return createADummyScriptResult(n, String.format("%s", n));
    }
}
