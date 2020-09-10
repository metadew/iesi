package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.component.trace.componentTrace.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentTraceConfigurationTest {


    private HttpComponentTrace httpComponentTrace;
    private HttpComponentTrace httpComponentTrace2;
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
        ComponentTraceKey componetKey = ComponentTraceKey.builder().uuid(componentUuid)
                .build();

        List<HttpComponentHeaderTrace> httpComponentHeaderTraces = new ArrayList<>();
        ComponentTraceKey httpComponentTraceKey = ComponentTraceKey.builder().uuid(componentUuid)
                .build();

        HttpComponentHeaderTrace httpComponentHeaders1Trace = HttpComponentHeaderTrace.builder()
                .metadataKey(new HttpComponentHeaderTraceKey(uuid))
                .httpComponentHeaderID(httpComponentTraceKey)
                .name("test")
                .value("test").build();
        httpComponentHeaderTraces.add(httpComponentHeaders1Trace);

        ComponentTraceKey httpComponentQueryKey = ComponentTraceKey.builder().uuid(componentUuid)
                .build();
        List<HttpComponentQueryTrace> httpComponentQueryTraces = new ArrayList<>();
        UUID uuid0 = UUID.randomUUID();
        HttpComponentQueryTrace httpComponentQueryTrace1 = HttpComponentQueryTrace.builder()
                .metadataKey(new HttpComponentQueryTraceKey(uuid0))
                .httpComponentQueryID(httpComponentQueryKey).name("test")
                .value("test").build();
        httpComponentQueryTraces.add(httpComponentQueryTrace1);

        httpComponentTrace = HttpComponentTrace.builder()
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
                .httpComponentHeaderTrace(httpComponentHeaderTraces)
                .httpComponentQueries(httpComponentQueryTraces)
                .build();

        componentUuid2 = UUID.randomUUID();
        ComponentTraceKey componentKey2 = ComponentTraceKey.builder().uuid(componentUuid2)
                .build();
        UUID uuid01 = UUID.randomUUID();
        List<HttpComponentHeaderTrace> httpComponentHeaders2Trace = new ArrayList<>();
        ComponentTraceKey httpComponentTraceKey2 = ComponentTraceKey.builder().uuid(componentUuid2)
                .build();
        HttpComponentHeaderTrace httpComponentHeaders3Trace = HttpComponentHeaderTrace.builder()
                .metadataKey(new HttpComponentHeaderTraceKey(uuid01))
                .httpComponentHeaderID(httpComponentTraceKey2)
                .name("test")
                .value("test").build();
        httpComponentHeaders2Trace.add(httpComponentHeaders3Trace);

        ComponentTraceKey httpComponentQueryKey2 = ComponentTraceKey.builder().uuid(componentUuid2)
                .build();
        List<HttpComponentQueryTrace> httpComponentQuerys2Trace = new ArrayList<>();

        UUID uuid1 = UUID.randomUUID();

        HttpComponentQueryTrace httpComponentQueryTrace3 = HttpComponentQueryTrace.builder()
                .metadataKey(new HttpComponentQueryTraceKey(uuid1))
                .httpComponentQueryID(httpComponentQueryKey2)
                .name("test2")
                .value("test2").build();

        UUID uuid2 = UUID.randomUUID();

        HttpComponentQueryTrace httpComponentQueryTrace4 = HttpComponentQueryTrace.builder()
                .metadataKey(new HttpComponentQueryTraceKey(uuid2))
                .httpComponentQueryID(httpComponentQueryKey2).name("test4")
                .value("test4").build();
        httpComponentQuerys2Trace.add(httpComponentQueryTrace3);
        httpComponentQuerys2Trace.add(httpComponentQueryTrace4);

        httpComponentTrace2 = HttpComponentTrace.builder()
                .metadataKey(componentKey2)
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
                .httpComponentHeaderTrace(httpComponentHeaders2Trace)
                .httpComponentQueries(httpComponentQuerys2Trace)
                .build();
    }

    ;

    @Test
    void testGetAllEmpty() {
        assertThat(ComponentTraceConfiguration.getInstance().getAll())
                .isEmpty();
    }

    @Test
    void testInsert() {
        ComponentTraceConfiguration.getInstance().insert(httpComponentTrace);
        assertThat(ComponentTraceConfiguration.getInstance().get(httpComponentTrace.getMetadataKey()))
                .hasValue(httpComponentTrace);
        assertThat(httpComponentTrace.getRunId()).isEqualTo("testRunid");
    }

    @Test
    void testGetAll() {
        ComponentTraceConfiguration.getInstance().insert(httpComponentTrace);
        ComponentTraceConfiguration.getInstance().insert(httpComponentTrace2);
        assertThat(ComponentTraceConfiguration.getInstance().getAll())
                .containsOnly(httpComponentTrace, httpComponentTrace2);
    }
}
