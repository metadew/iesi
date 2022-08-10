package io.metadew.iesi.common.configuration;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.FrameworkControl;
import io.metadew.iesi.common.configuration.guard.GuardConfiguration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.MetadataPolicyConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorHandler;
import io.metadew.iesi.common.configuration.plugin.PluginConfiguration;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = { SpringContext.class, Configuration.class, FrameworkControl.class,
        MetadataRepositoryCoordinatorHandler.class, DatabaseHandler.class
})
class ConfigurationTest {


    @Autowired
    private SpringContext springContext;

    @Autowired
    private Configuration configuration;

    @Autowired
    private MetadataRepositoryCoordinatorHandler metadataRepositoryCoordinatorHandler;

    @Autowired
    private FrameworkControl frameworkControl;

    @Autowired
    private DatabaseHandler databaseHandler;

    @Test
    void initTest() {
        assertNotNull(springContext);
        assertNotNull(configuration);
        assertNotNull(metadataRepositoryCoordinatorHandler);
        assertNotNull(frameworkControl);
        assertNotNull(databaseHandler);

        MetadataConfiguration metadataRepositoryConfiguration = MetadataConfiguration.getInstance();
        assertTrue(true);

        PluginConfiguration pluginConfiguration = PluginConfiguration.getInstance();
        GuardConfiguration guardConfiguration = GuardConfiguration.getInstance();

        assertTrue(true);
    }
}
