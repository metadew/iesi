package io.metadew.iesi.common.configuration;

import io.metadew.iesi.common.configuration.guard.GuardConfiguration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.MetadataPolicyConfiguration;
import io.metadew.iesi.common.configuration.plugin.PluginConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ConfigurationTest {
    @Test
    void initTest() {
        Configuration configuration = Configuration.getInstance();
        io.metadew.iesi.common.configuration.framework.FrameworkConfiguration conf = io.metadew.iesi.common.configuration.framework.FrameworkConfiguration.getInstance();
        MetadataConfiguration metadataRepositoryConfiguration = MetadataConfiguration.getInstance();
        assertTrue(true);

        PluginConfiguration pluginConfiguration = PluginConfiguration.getInstance();
        GuardConfiguration guardConfiguration = GuardConfiguration.getInstance();

        assertTrue(true);
    }
}
