package io.metadew.iesi.metadata.configuration.audit;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAudit;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAuditAction;
import io.metadew.iesi.metadata.definition.audit.key.ScriptDesignAuditKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ScriptDesignAuditConfiguration.class )
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ScriptDesignAuditConfigurationTest {

    @Autowired
    private ScriptDesignAuditConfiguration scriptDesignAuditConfiguration;

    @Test
    void scriptGetAllEmptyTest() throws Exception {
        assertThat(scriptDesignAuditConfiguration.getAll())
                .isEmpty();
    }

    @Test
    void scriptAuditGetAll() throws SQLException {
        ScriptDesignAudit scriptDesignAudit1 = ScriptDesignAudit.builder()
                .scriptDesignAuditKey(new ScriptDesignAuditKey(UUID.randomUUID()))
                .username("Admin")
                .userId(UUID.randomUUID().toString())
                .scriptDesignAuditAction(ScriptDesignAuditAction.CREATE)
                .scriptId(UUID.randomUUID().toString())
                .scriptName("script1")
                .scriptVersion(1l)
                .securityGroup("PUBLIC")
                .timeStamp(LocalDateTime.now().toString())
                .build();
        ScriptDesignAudit scriptDesignAudit2 = ScriptDesignAudit.builder()
                .scriptDesignAuditKey(new ScriptDesignAuditKey(UUID.randomUUID()))
                .username("Admin")
                .userId(UUID.randomUUID().toString())
                .scriptDesignAuditAction(ScriptDesignAuditAction.CREATE)
                .scriptId(UUID.randomUUID().toString())
                .scriptName("script2")
                .scriptVersion(1l)
                .securityGroup("PUBLIC")
                .timeStamp(LocalDateTime.now().toString())
                .build();
        scriptDesignAuditConfiguration.insert(scriptDesignAudit1);
        scriptDesignAuditConfiguration.insert(scriptDesignAudit2);
        assertThat(scriptDesignAuditConfiguration.getAll()).containsOnly(scriptDesignAudit1, scriptDesignAudit2);
    }

    @Test
    void scriptDesignAuditGetById() {
        ScriptDesignAuditKey scriptDesignAuditKey = new ScriptDesignAuditKey(UUID.randomUUID());
        ScriptDesignAudit scriptDesignAudit1 = ScriptDesignAudit.builder()
                .scriptDesignAuditKey(scriptDesignAuditKey)
                .username("Admin")
                .userId(UUID.randomUUID().toString())
                .scriptDesignAuditAction(ScriptDesignAuditAction.CREATE)
                .scriptId(UUID.randomUUID().toString())
                .scriptName("script1")
                .scriptVersion(1l)
                .securityGroup("PUBLIC")
                .timeStamp(LocalDateTime.now().toString())
                .build();
        scriptDesignAuditConfiguration.insert(scriptDesignAudit1);
        assertThat(scriptDesignAuditConfiguration.get(scriptDesignAuditKey))
                .hasValue(scriptDesignAudit1);
    }

    @Test
    void scriptDesignAuditInsertTest() {
        ScriptDesignAuditKey scriptDesignAuditKey = new ScriptDesignAuditKey(UUID.randomUUID());
        ScriptDesignAudit scriptDesignAudit1 = ScriptDesignAudit.builder()
                .scriptDesignAuditKey(scriptDesignAuditKey)
                .username("Admin")
                .userId(UUID.randomUUID().toString())
                .scriptDesignAuditAction(ScriptDesignAuditAction.CREATE)
                .scriptId(UUID.randomUUID().toString())
                .scriptName("script1")
                .scriptVersion(1l)
                .securityGroup("PUBLIC")
                .timeStamp(LocalDateTime.now().toString())
                .build();
        scriptDesignAuditConfiguration.insert(scriptDesignAudit1);
        assertThat(scriptDesignAuditConfiguration.get(scriptDesignAuditKey))
                .hasValue(scriptDesignAudit1);
    }

    @Test
    void scriptDeleteTest() throws Exception {
        ScriptDesignAuditKey scriptDesignAuditKey = new ScriptDesignAuditKey(UUID.randomUUID());
        ScriptDesignAudit scriptDesignAudit1 = ScriptDesignAudit.builder()
                .scriptDesignAuditKey(scriptDesignAuditKey)
                .username("Admin")
                .userId(UUID.randomUUID().toString())
                .scriptDesignAuditAction(ScriptDesignAuditAction.CREATE)
                .scriptId(UUID.randomUUID().toString())
                .scriptName("script1")
                .scriptVersion(1l)
                .securityGroup("PUBLIC")
                .timeStamp(LocalDateTime.now().toString())
                .build();
        scriptDesignAuditConfiguration.insert(scriptDesignAudit1);
        assertEquals(1, scriptDesignAuditConfiguration.getAll().size());
        scriptDesignAuditConfiguration.delete(scriptDesignAuditKey);
        assertEquals(0, scriptDesignAuditConfiguration.getAll().size());
    }
}
