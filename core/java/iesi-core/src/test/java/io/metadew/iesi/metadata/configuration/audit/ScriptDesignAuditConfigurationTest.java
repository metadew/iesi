package io.metadew.iesi.metadata.configuration.audit;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAudit;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAuditAction;
import io.metadew.iesi.metadata.definition.audit.key.ScriptDesignAuditKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class ScriptDesignAuditConfigurationTest {

    @BeforeAll
    static void prepare() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void clearDatabase() {
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    @AfterAll
    static void teardown() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
    }

    @Test
    void scriptGetAllEmptyTest() throws Exception{
        assertThat(ScriptDesignAuditConfiguration.getInstance().getAll())
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
        ScriptDesignAuditConfiguration.getInstance().insert(scriptDesignAudit1);
        ScriptDesignAuditConfiguration.getInstance().insert(scriptDesignAudit2);
        assertThat(ScriptDesignAuditConfiguration.getInstance().getAll()).containsOnly(scriptDesignAudit1,scriptDesignAudit2);
    }

    @Test
    void scriptDesignAuditGetById(){
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
        ScriptDesignAuditConfiguration.getInstance().insert(scriptDesignAudit1);
        assertThat(ScriptDesignAuditConfiguration.getInstance().get(scriptDesignAuditKey))
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
        ScriptDesignAuditConfiguration.getInstance().insert(scriptDesignAudit1);
        assertThat(ScriptDesignAuditConfiguration.getInstance().get(scriptDesignAuditKey))
                .hasValue(scriptDesignAudit1);
    }

    @Test
    void scriptDeleteTest() throws Exception{
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
        ScriptDesignAuditConfiguration.getInstance().insert(scriptDesignAudit1);
        assertEquals(1, ScriptDesignAuditConfiguration.getInstance().getAll().size());
        ScriptDesignAuditConfiguration.getInstance().delete(scriptDesignAuditKey);
        assertEquals(0, ScriptDesignAuditConfiguration.getInstance().getAll().size());
    }
}
