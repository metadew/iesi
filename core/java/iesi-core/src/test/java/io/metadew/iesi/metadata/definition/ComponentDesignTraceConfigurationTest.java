package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.component.trace.componentDesign.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentDesignTraceConfigurationTest {

    private HttpComponentDesignTrace httpComponentDesignTrace;
    private HttpComponentDesignTrace httpComponentDesignTrace2;
    private UUID componentUuid;
    private UUID componentUuid2;

    @BeforeAll
    static void prepare() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getTraceMetadataRepository()
                .createAllTables();
    }

    @BeforeEach
    void initializeTemplates() {
        componentUuid = UUID.randomUUID();
        UUID uuid = UUID.randomUUID();
        ComponentDesignTraceKey componetKey = ComponentDesignTraceKey.builder().uuid(componentUuid)
                .build();

        List<HttpComponentHeaderDesign> httpComponentHeaderDesigns = new ArrayList<>();
        HttpComponentDesignTraceKey httpComponentDesignTraceKey = HttpComponentDesignTraceKey.builder().uuid(componentUuid)
                .build();

        HttpComponentHeaderDesign httpComponentHeaderDesigns1 = HttpComponentHeaderDesign.builder()
                .id(uuid)
                .httpComponentDesignID(httpComponentDesignTraceKey)
                .name("test")
                .value("test").build();
        httpComponentHeaderDesigns.add(httpComponentHeaderDesigns1);

        HttpComponentQueryDesignKey httpComponentQueryDesignKey = HttpComponentQueryDesignKey.builder().uuid(componentUuid)
                .build();
        List<HttpComponentQueryDesign> httpComponentQueryDesigns = new ArrayList<>();
        UUID uuid0 = UUID.randomUUID();
        HttpComponentQueryDesign httpComponentQueryDesign1 = HttpComponentQueryDesign.builder()
                .id(uuid0)
                .httpComponentQueryDesignID(httpComponentQueryDesignKey).name("test")
                .value("test").build();
        httpComponentQueryDesigns.add(httpComponentQueryDesign1);

        httpComponentDesignTrace = HttpComponentDesignTrace.builder()
                .metadataKey(componetKey)
                .runId("testRunid")
                .processId(1L)
                .actionParameter("actionParameter")
                .componentTypeParameter("componentTypeParameter")
                .componentName("componentName")
                .componentDescription("componentDescription")
                .componentVersion(1L)
                .connectionName("connectionName")
                .type("type")
                .endpoint("endpoint")
                .httpComponentHeaderDesigns(httpComponentHeaderDesigns)
                .httpComponentQueryDesigns(httpComponentQueryDesigns)
                .build();

        componentUuid2 = UUID.randomUUID();
        ComponentDesignTraceKey componentKey2 = ComponentDesignTraceKey.builder().uuid(componentUuid2)
                .build();
        UUID uuid01 = UUID.randomUUID();
        List<HttpComponentHeaderDesign> httpComponentHeaderDesigns2 = new ArrayList<>();
        HttpComponentDesignTraceKey httpComponentDesignTraceKey2 = HttpComponentDesignTraceKey.builder().uuid(componentUuid2)
                .build();
        HttpComponentHeaderDesign httpComponentHeaderDesigns3 = HttpComponentHeaderDesign.builder()
                .id(uuid01)
                .httpComponentDesignID(httpComponentDesignTraceKey2)
                .name("test")
                .value("test").build();
        httpComponentHeaderDesigns2.add(httpComponentHeaderDesigns3);

        HttpComponentQueryDesignKey httpComponentQueryDesignKey2 = HttpComponentQueryDesignKey.builder().uuid(componentUuid2)
                .build();
        List<HttpComponentQueryDesign> httpComponentQueryDesigns2 = new ArrayList<>();

        UUID uuid1 = UUID.randomUUID();

        HttpComponentQueryDesign httpComponentQueryDesign3 = HttpComponentQueryDesign.builder()
                .id(uuid1)
                .httpComponentQueryDesignID(httpComponentQueryDesignKey2)
                .name("test2")
                .value("test2").build();

        UUID uuid2 = UUID.randomUUID();

        HttpComponentQueryDesign httpComponentQueryDesign4 = HttpComponentQueryDesign.builder()
                .id(uuid2)
                .httpComponentQueryDesignID(httpComponentQueryDesignKey2).name("test4")
                .value("test4").build();
        httpComponentQueryDesigns2.add(httpComponentQueryDesign3);
        httpComponentQueryDesigns2.add(httpComponentQueryDesign4);

        httpComponentDesignTrace2 = HttpComponentDesignTrace.builder()
                .metadataKey(componentKey2)
                .runId("testRunid2")
                .processId(1L)
                .actionParameter("test2")
                .componentTypeParameter("test2")
                .componentName("test2")
                .componentDescription("test2")
                .componentVersion(1L)
                .connectionName("test2")
                .type("test2")
                .endpoint("test2")
                .httpComponentHeaderDesigns(httpComponentHeaderDesigns2)
                .httpComponentQueryDesigns(httpComponentQueryDesigns2)
                .build();
    };

    @AfterEach
    void clearDatabase() {
        MetadataRepositoryConfiguration.getInstance()
                .getTraceMetadataRepository().cleanAllTables();
    }

    @AfterAll
    static void teardown() {
        MetadataRepositoryConfiguration.getInstance()
                .getTraceMetadataRepository().dropAllTables();
    }

    @Test
    void testGetAllEmpty() {
        assertThat(ComponentDesignTraceConfiguration.getInstance().getAll())
                .isEmpty();
    }

    @Test
    void testInsert() {
        ComponentDesignTraceConfiguration.getInstance().insert(httpComponentDesignTrace);
        assertThat(ComponentDesignTraceConfiguration.getInstance().get(httpComponentDesignTrace.getMetadataKey()))
                .hasValue(httpComponentDesignTrace);
        assertThat(httpComponentDesignTrace.getRunId()).isEqualTo("testRunid");
    }

    @Test
    void testGetAll() {
        ComponentDesignTraceConfiguration.getInstance().insert(httpComponentDesignTrace);
        ComponentDesignTraceConfiguration.getInstance().insert(httpComponentDesignTrace2);
        assertThat(ComponentDesignTraceConfiguration.getInstance().getAll())
                .containsOnly(httpComponentDesignTrace, httpComponentDesignTrace2);
    }
}
