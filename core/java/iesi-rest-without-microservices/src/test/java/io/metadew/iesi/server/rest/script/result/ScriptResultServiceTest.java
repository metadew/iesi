package io.metadew.iesi.server.rest.script.result;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
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
    void getAllTypeReturnedTest() {
        assertThat(scriptResultService.getAll())
                .as("The returned type should a List implementation")
                .isInstanceOf(List.class);
    }

    @Test
    void getAllNoScriptResultTest() {
        assertThat(scriptResultService.getAll().size())
                .as("DB should be empty : No ScriptResult should be found")
                .isEqualTo(0);
    }

    @Test
    void getAllConformity1ScriptResultTest() {
        ScriptResult insertedScriptResult = createAndInsertADummyScriptResultN(1);
        List<ScriptResult> scriptResultList = scriptResultService.getAll();

        assertThat(scriptResultList.size())
                .as("There should be one ScriptResult")
                .isEqualTo(1);

        assertThat(scriptResultList.get(0))
                .as("getAll method should retrieve the exact same data that were inserted")
                .isEqualToComparingFieldByField(insertedScriptResult);
    }

    @Test
    void getAllConformityBetweenInsertedAndRetrieved2ScriptResultTest() {
        ScriptResult insertedScriptResult = createAndInsertADummyScriptResultN(1);
        ScriptResult insertedScriptResult2 = createAndInsertADummyScriptResultN(2);
        List<ScriptResult> scriptResultList = scriptResultService.getAll();

        assertThat(scriptResultList.size())
                .as("There should be 2 ScriptResult")
                .isEqualTo(2);

        assertThat(scriptResultList)
                .as("getAll method should retrieve the exact same data that were inserted")
                .contains(insertedScriptResult)
                .contains(insertedScriptResult2);
    }

    @Test
    void getAllConformityBetweenInsertedAndRetrieved3ScriptResultTest() {
        ScriptResult insertedScriptResult = createAndInsertADummyScriptResultN(1);
        ScriptResult insertedScriptResult2 = createAndInsertADummyScriptResultN(2);
        ScriptResult insertedScriptResult3 = createAndInsertADummyScriptResultN(3);
        List<ScriptResult> scriptResultList = scriptResultService.getAll();

        assertThat(scriptResultList.size())
                .as("There should be 3 ScriptResult")
                .isEqualTo(3);

        assertThat(scriptResultList)
                .as("getAll method should retrieve the exact same data that were inserted")
                .contains(insertedScriptResult)
                .contains(insertedScriptResult2)
                .contains(insertedScriptResult3);
    }

    @Test
    void getAllConformityBetweenInsertedAndRetrieved20ScriptResultTest() {
        ScriptResult[] arrayOfScriptResult = new ScriptResult[20];
        for (int i = 0; i < arrayOfScriptResult.length; i++)
            arrayOfScriptResult[i] = createAndInsertADummyScriptResultN(i);

        List<ScriptResult> scriptResultList = scriptResultService.getAll();

        assertThat(scriptResultList.size())
                .as("There should be 20 ScriptResult")
                .isEqualTo(20);

        for (ScriptResult currentScriptResult : arrayOfScriptResult)
            assertThat(scriptResultList)
                    .as("All inserted ScriptResults should be contained in this list")
                    .contains(currentScriptResult);
    }

    @Test
    void getAll1NegativeTestCase() {
        for (int i = 0; i < 20; i++)
            createAndInsertADummyScriptResultN(i);

        ScriptResult notInsertedScriptResult = createADummyScriptResult(50);
        List<ScriptResult> scriptResultList = scriptResultService.getAll();
        assertThat(scriptResultList)
                .as("This ScriptResult isn't inserted and shouldn't be retrieved")
                .doesNotContain(notInsertedScriptResult);
    }

    @Test
    void getByRunIdTypeReturnedTest() {
        assertThat(scriptResultService.getByRunId("1"))
                .as("The returned type should an implementation of List")
                .isInstanceOf(List.class);
    }

    @Test
    void getByRunIdNoScriptResultEmptyDBTest() {
        assertThat(scriptResultService.getByRunId("1").size())
                .as("DB should be empty : No ScriptResult should be found")
                .isEqualTo(0);
    }

    @Test
    void getByRunIdNoScriptResult20SRTest() {
        for (int i = 0; i < 20; i++)
            createAndInsertADummyScriptResultN(i);

        List<ScriptResult> scriptResultList = scriptResultService.getByRunId("notContainedId");
        assertThat(scriptResultList.size())
                .as("There shouldn't be any result in this list")
                .isEqualTo(0);
    }

    @Test
    void getByRunId1ScriptResult21SRTest() {
        for (int i = 0; i < 20; i++)
            createAndInsertADummyScriptResultN(i);

        ScriptResult containedScriptResult = createAndInsertADummyScriptResultN(20, "containedId");

        List<ScriptResult> scriptResultList = scriptResultService.getByRunId("containedId");

        assertThat(scriptResultList.size())
                .as("There should be one result in this list")
                .isEqualTo(1);

        assertThat(scriptResultList)
                .as("This list should contain this exact result")
                .contains(containedScriptResult);
    }

    @Test
    void getByRunId2ScriptResult22SRTest() {
        for (int i = 0; i < 20; i++)
            createAndInsertADummyScriptResultN(i);

        ScriptResult containedScriptResult1 = createAndInsertADummyScriptResultN(20, "containedId");
        ScriptResult containedScriptResult2 = createAndInsertADummyScriptResultN(21, "containedId");
        List<ScriptResult> scriptResultList = scriptResultService.getByRunId("containedId");

        assertThat(scriptResultList.size())
                .as("There should be one result in this list")
                .isEqualTo(2);

        assertThat(scriptResultList)
                .as("This list should contain those exact results")
                .contains(containedScriptResult1)
                .contains(containedScriptResult2);
    }

    @Test
    void getByRunId3ScriptResult23SRTest() {
        for (int i = 0; i < 20; i++)
            createAndInsertADummyScriptResultN(i);

        ScriptResult containedScriptResult1 = createAndInsertADummyScriptResultN(20, "containedId");
        ScriptResult containedScriptResult2 = createAndInsertADummyScriptResultN(21, "containedId");
        ScriptResult containedScriptResult3 = createAndInsertADummyScriptResultN(22, "containedId");
        List<ScriptResult> scriptResultList = scriptResultService.getByRunId("containedId");

        assertThat(scriptResultList.size())
                .as("There should be one result in this list")
                .isEqualTo(3);

        assertThat(scriptResultList)
                .as("This list should contain those exact results")
                .contains(containedScriptResult1)
                .contains(containedScriptResult2)
                .contains(containedScriptResult3);
    }

    @Test
    void getByRunId20ScriptResult20SRTest() {
        ScriptResult[] arrayOfScriptResult = new ScriptResult[20];
        for (int i = 0; i < arrayOfScriptResult.length; i++)
            arrayOfScriptResult[i] = createAndInsertADummyScriptResultN(i, "containedId");

        List<ScriptResult> scriptResultList = scriptResultService.getByRunId("containedId");

        assertThat(scriptResultList.size())
                .as("There should be 20 retrieved ScriptResult on 20")
                .isEqualTo(20);

        for (ScriptResult currentScriptResult : arrayOfScriptResult)
            assertThat(scriptResultList)
                    .as("All the inserted ScriptResults should be retrieved")
                    .contains(currentScriptResult);
    }

    @Test
    void getByRunId2x20ScriptResult60SRTest() {
        ScriptResult[][] scriptResults2D = new ScriptResult[3][20];

        for (int i = 0; i < scriptResults2D.length; i++) {
            for (int j = 0; j < scriptResults2D[i].length; j++) {
                int unique = Integer.parseInt(String.format("%s%s", i, j));
                scriptResults2D[i][j] = createAndInsertADummyScriptResultN(unique,
                        i == 0 ? "containedId1" :
                                i == 1 ? "containedId2" :
                                        String.format("%s", j)
                );
            }
        }

        List<ScriptResult> scriptResultId1List = scriptResultService.getByRunId("containedId1");
        List<ScriptResult> scriptResultId2List = scriptResultService.getByRunId("containedId2");

        assertThat(scriptResultId1List.size())
                .as("")
                .isEqualTo(20);

        assertThat(scriptResultId2List.size())
                .as("")
                .isEqualTo(20);

        for (int i = 0; i < scriptResults2D.length; i++) {
            for (int j = 0; j < scriptResults2D[i].length; j++) {
                if (i == 0) {
                    assertThat(scriptResultId1List)
                            .as(String.format("The scriptResult[%s][%s] should be contained in this list (scriptResultId1List)", i, j))
                            .contains(scriptResults2D[i][j]);
                } else if (i == 1) {
                    assertThat(scriptResultId2List)
                            .as(String.format("The scriptResult[%s][%s] should be contained in this list (scriptResultId2List)", i, j))
                            .contains(scriptResults2D[i][j]);
                } else {
                    assertThat(scriptResultId1List)
                            .as(String.format("The scriptResult[%s][%s] shouldn't be contained in this list (scriptResultId1List)", i, j))
                            .doesNotContain(scriptResults2D[i][j]);
                    assertThat(scriptResultId2List)
                            .as(String.format("The scriptResult[%s][%s] shouldn't be contained in this list (scriptResultId2List)", i, j))
                            .doesNotContain(scriptResults2D[i][j]);
                }
            }
        }
    }


    @Test
    void getByRunId1NegativeTestCase() {
        for (int i = 0; i < 20; i++)
            createAndInsertADummyScriptResultN(i, "containedId");

        List<ScriptResult> scriptResultList = scriptResultService.getByRunId("notContainedId");

        assertThat(scriptResultList.size())
                .as("There should be 0 retrieved ScriptResult on 20")
                .isEqualTo(0);
    }

    @Test
    void getByRunIdAndProcessIdTypeReturnedTest() {
        assertThat(scriptResultService.getByRunIdAndProcessId("", 1L))
                .as("The returned type should be an optional")
                .isInstanceOf(Optional.class);
    }

    @Test
    void getByRunIdAndProcessIdEmptyDB() {
        assertThat(scriptResultService.getByRunIdAndProcessId("", 1L).isPresent())
                .as("DB should be empty : No ScriptResult should be found")
                .isFalse();
    }

    @Test
    void getByRunIdAndProcessIdNoResult20SRTest() {
        for (int i = 0; i < 20; i++)
            createAndInsertADummyScriptResultN(i);

        assertThat(scriptResultService.getByRunIdAndProcessId("notPresentRunId", 1L).isPresent())
                .as("No ScriptResult should be found")
                .isFalse();
    }

    @Test
    void getByRunIdAndProcessId20SR20Tests() {
        ScriptResult[] arrayOfScriptResult = new ScriptResult[20];
        for (int i = 0; i < arrayOfScriptResult.length; i++)
            arrayOfScriptResult[i] = createAndInsertADummyScriptResultN(i);

        for (int i = 0; i < arrayOfScriptResult.length; i++) {
            Optional<ScriptResult> requestedScriptResult = scriptResultService.getByRunIdAndProcessId(String.format("%s",i), (long) i);
            assertThat(requestedScriptResult.isPresent())
                    .as("One ScriptResult should be returned")
                    .isTrue();

            assertThat(requestedScriptResult.get())
                    .as("The retrieved ScriptResult should be equal to the inserted one")
                    .isEqualTo(arrayOfScriptResult[i]);
        }
    }

    ScriptResult createAndInsertADummyScriptResultN(int n, String runId) {
        ScriptResult scriptResultN = createADummyScriptResult(n, runId);
        scriptResultConfiguration.insert(scriptResultN);
        return scriptResultN;
    }

    ScriptResult createAndInsertADummyScriptResultN(int n) {
        return createAndInsertADummyScriptResultN(n, String.format("%s", n));
    }

    ScriptResult createADummyScriptResult(int n, String runId) {
        String nString = String.valueOf(n);
        LocalDateTime now = LocalDateTime.now();
        int nMonth = now.getMonthValue();
        int nDay = now.getDayOfMonth();
        int nHours = now.getHour();
        int nMin = now.getMinute();

        return ScriptResult.builder()
                .scriptResultKey(new ScriptResultKey(runId, (long) n))
                .parentProcessId((long) n)
                .scriptId(nString)
                .scriptName("Script" + nString)
                .scriptVersion((long) n)
                .environment(nString)
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.of(2000, nMonth, nDay, nHours, nMin))
                .endTimestamp(LocalDateTime.of(2000, nMonth, nDay, nHours, nMin))
                .build();
    }

    ScriptResult createADummyScriptResult(int n) {
        return createADummyScriptResult(n, String.format("%s", n));
    }
}
