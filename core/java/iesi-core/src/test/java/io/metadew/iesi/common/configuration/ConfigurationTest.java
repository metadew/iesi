package io.metadew.iesi.common.configuration;

import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.common.configuration.guard.GuardConfiguration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.common.configuration.plugin.PluginConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ConfigurationTest {

    @Test
    void initTest() throws Exception {

        Configuration configuration = Configuration.getInstance();

        FrameworkConfiguration conf = FrameworkConfiguration.getInstance();
        MetadataConfiguration metadataRepositoryConfiguration = MetadataConfiguration.getInstance();
        assertTrue(true);

        PluginConfiguration pluginConfiguration = PluginConfiguration.getInstance();
        GuardConfiguration guardConfiguration = GuardConfiguration.getInstance();

        assertTrue(true);
    }
}
