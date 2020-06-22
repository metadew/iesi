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
    void getAllNoScriptResultTest() {
        assertThat(scriptResultService.getAll().size())
                .as("No ScriptResult should be found")
                .isEqualTo(0);
    }

    @Test
    void getAllConformity1ScriptResultTest() {
        ScriptResult insertedScriptResult = createAndInsertADummyScriptResultN(1, "1");
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
        ScriptResult insertedScriptResult = createAndInsertADummyScriptResultN(1, "1");
        ScriptResult insertedScriptResult2 = createAndInsertADummyScriptResultN(2, "2");
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
        ScriptResult insertedScriptResult = createAndInsertADummyScriptResultN(1, "1");
        ScriptResult insertedScriptResult2 = createAndInsertADummyScriptResultN(2, "2");
        ScriptResult insertedScriptResult3 = createAndInsertADummyScriptResultN(3, "3");
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
            arrayOfScriptResult[i] = createAndInsertADummyScriptResultN(i, String.format("%s", i));

        List<ScriptResult> scriptResultList = scriptResultService.getAll();
        for (ScriptResult currentScriptResult : arrayOfScriptResult)
            assertThat(scriptResultList)
                    .as("All the inserted ScriptResults should be retrieved")
                    .contains(currentScriptResult);
    }

    @Test
    void getAll1NegativeTestCase() {
        for (int i = 0; i < 20; i++)
            createAndInsertADummyScriptResultN(i,String.format("%s", i));

        ScriptResult notInsertedScriptResult = createADummyScriptResult(50,"50");
        List<ScriptResult> scriptResultList = scriptResultService.getAll();
        assertThat(scriptResultList)
                .as("All the inserted ScriptResults should be retrieved")
                .doesNotContain(notInsertedScriptResult);
    }

    @Test
    void getByRunIdNoScriptResultEmptyDBTest() {
        assertThat(scriptResultService.getByRunId("1").size())
                .as("No ScriptResult should be found")
                .isEqualTo(0);
    }

    @Test
    void getByRunIdNoScriptResult20SRTest() {

    }

    @Test
    void getByRunId1ScriptResult20SRTest() {

    }

    @Test
    void getByRunId2ScriptResult20SRTest() {

    }

    @Test
    void getByRunId3ScriptResult20SRTest() {

    }

    @Test
    void getByRunId1NegativeTestCase() {

    }

    @Test
    void getByRunIdAndProcessIdEmptyDB() {

    }

    @Test
    void getByRunIdAndProcessIdNoResult20SRTest() {

    }

    @Test
    void getByRunIdAndProcessId1Result20SRTest() {

    }

    ScriptResult createAndInsertADummyScriptResultN(int n, String runId) {
        ScriptResult scriptResultN = createADummyScriptResult(n, runId);
        scriptResultConfiguration.insert(scriptResultN);
        return scriptResultN;
    }

    ScriptResult createADummyScriptResult(int n, String runId) {
        String nString = String.valueOf(n);
        LocalDateTime now = LocalDateTime.now();
        int nMonth = now.getMonthValue();
        int nDay = now.getDayOfMonth();
        int nHours = now.getHour();
        int nMin = now.getMinute();

        return ScriptResult.builder()
                .scriptResultKey(new ScriptResultKey(nString, (long) n))
                .parentProcessId((long) n)
                .scriptId(nString)
                .scriptName("Script" + runId)
                .scriptVersion((long) n)
                .environment(nString)
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.of(2000, nMonth, nDay, nHours, nMin))
                .endTimestamp(LocalDateTime.of(2000, nMonth, nDay, nHours, nMin))
                .build();
    }
}
