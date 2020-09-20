package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.component.trace.design.*;
import io.metadew.iesi.metadata.definition.component.trace.design.http.*;
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

    @BeforeEach
    void initializeTemplates() {
        componentUuid = UUID.randomUUID();
        UUID uuid = UUID.randomUUID();
        ComponentDesignTraceKey componetKey = ComponentDesignTraceKey.builder().uuid(componentUuid)
                .build();

        List<HttpComponentHeaderDesignTrace> httpComponentHeaderDesigns = new ArrayList<>();
        ComponentDesignTraceKey httpComponentHeaderDesignTraceKey = ComponentDesignTraceKey.builder().uuid(componentUuid)
                .build();

        HttpComponentHeaderDesignTrace httpComponentHeaderDesigns1 = HttpComponentHeaderDesignTrace.builder()
                .metadataKey(new HttpComponentHeaderDesignTraceKey(uuid))
                .httpComponentDesignID(httpComponentHeaderDesignTraceKey)
                .name("test")
                .value("test").build();
        httpComponentHeaderDesigns.add(httpComponentHeaderDesigns1);

        ComponentDesignTraceKey httpComponentQueryParameterDesignTraceKey = ComponentDesignTraceKey.builder().uuid(componentUuid)
                .build();
        List<HttpComponentQueryParameterDesignTrace> httpComponentQueryDesigns = new ArrayList<>();
        UUID uuid0 = UUID.randomUUID();
        HttpComponentQueryParameterDesignTrace httpComponentQueryDesign1 = HttpComponentQueryParameterDesignTrace.builder()
                .metadaKey(new HttpComponentQueryParameterDesignTraceKey(uuid0))
                .httpComponentQueryDesignID(httpComponentQueryParameterDesignTraceKey).name("test")
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
        List<HttpComponentHeaderDesignTrace> httpComponentHeaderDesigns2 = new ArrayList<>();
        ComponentDesignTraceKey httpComponentHeaderDesignTraceKey2 = ComponentDesignTraceKey.builder().uuid(componentUuid2)
                .build();
        HttpComponentHeaderDesignTrace httpComponentHeaderDesigns3 = HttpComponentHeaderDesignTrace.builder()
                .metadataKey(new HttpComponentHeaderDesignTraceKey(uuid01))
                .httpComponentDesignID(httpComponentHeaderDesignTraceKey2)
                .name("test")
                .value("test").build();
        httpComponentHeaderDesigns2.add(httpComponentHeaderDesigns3);

        ComponentDesignTraceKey httpComponentQueryParameterDesignTraceKey2 = ComponentDesignTraceKey.builder().uuid(componentUuid2)
                .build();
        List<HttpComponentQueryParameterDesignTrace> httpComponentQueryDesigns2 = new ArrayList<>();

        UUID uuid1 = UUID.randomUUID();

        HttpComponentQueryParameterDesignTrace httpComponentQueryDesign3 = HttpComponentQueryParameterDesignTrace.builder()
                .metadaKey(new HttpComponentQueryParameterDesignTraceKey(uuid1))
                .httpComponentQueryDesignID(httpComponentQueryParameterDesignTraceKey2)
                .name("test2")
                .value("test2").build();

        UUID uuid2 = UUID.randomUUID();

        HttpComponentQueryParameterDesignTrace httpComponentQueryDesign4 = HttpComponentQueryParameterDesignTrace.builder()
                .metadaKey(new HttpComponentQueryParameterDesignTraceKey(uuid2))
                .httpComponentQueryDesignID(httpComponentQueryParameterDesignTraceKey2).name("test4")
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
