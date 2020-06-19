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
public class ScriptResultDtoServiceTest {

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
    void getAllScriptResultTests(){
        ScriptResult insertedScriptResult = getAndInsertADummyScriptResultN(1);
        List<ScriptResult> scriptResultList = scriptResultService.getAll();

        assertThat(scriptResultList.size())
                .as("There should be one ScriptResult")
                .isEqualTo(1);

        assertThat(scriptResultList.get(0))
                .as("")
                .isEqualToComparingFieldByField(insertedScriptResult);

        ScriptResult insertedScriptResult2 = getAndInsertADummyScriptResultN(2);
        scriptResultList = scriptResultService.getAll();

        assertThat(scriptResultList.size())
                .as("There should be 2 ScriptResult")
                .isEqualTo(2);

        assertThat(scriptResultList)
                .as("")
                .contains(insertedScriptResult)
                .contains(insertedScriptResult2);

        ScriptResult insertedScriptResult3 = getAndInsertADummyScriptResultN(3);
        scriptResultList = scriptResultService.getAll();

        assertThat(scriptResultList.size())
                .as("There should be 2 ScriptResult")
                .isEqualTo(3);

        assertThat(scriptResultList)
                .as("")
                .contains(insertedScriptResult)
                .contains(insertedScriptResult2)
                .contains(insertedScriptResult3);

    }


    ScriptResult getAndInsertADummyScriptResultN(int n){
        String nString = String.valueOf(n);
        ScriptResult scriptResultN = ScriptResult.builder()
                .scriptResultKey(new ScriptResultKey(nString, (long) n))
                .parentProcessId((long) n)
                .scriptId(nString)
                .scriptName("Script"+nString)
                .scriptVersion((long) n)
                .environment(nString)
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.of(2000,n,n,n,n))
                .endTimestamp(LocalDateTime.of(2001,n,n,n,n))
                .build();

        scriptResultConfiguration.insert(scriptResultN);
        return scriptResultN;
    }
}
