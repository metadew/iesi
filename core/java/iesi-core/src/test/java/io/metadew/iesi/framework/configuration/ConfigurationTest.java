package io.metadew.iesi.framework.configuration;

import io.metadew.iesi.framework.configuration.guard.GuardConfiguration;
import io.metadew.iesi.framework.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.framework.configuration.plugin.PluginConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ConfigurationTest {

    @Test
    void initTest() {
        Configuration configuration = Configuration.getInstance();
        io.metadew.iesi.framework.configuration.framework.FrameworkConfiguration conf = io.metadew.iesi.framework.configuration.framework.FrameworkConfiguration.getInstance();
        MetadataConfiguration metadataRepositoryConfiguration = MetadataConfiguration.getInstance();
        assertTrue(true);

        PluginConfiguration pluginConfiguration = PluginConfiguration.getInstance();
        GuardConfiguration guardConfiguration = GuardConfiguration.getInstance();

        assertTrue(true);
    }
}
