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

    @BeforeEach
    void initializeTemplates() {
        componentUuid = UUID.randomUUID();
        UUID uuid = UUID.randomUUID();
        ComponentTraceKey componetKey = ComponentTraceKey.builder().uuid(componentUuid)
                .build();

        List<HttpComponentHeader> httpComponentHeaders = new ArrayList<>();
        HttpComponentHeaderKey httpComponentTraceKey = HttpComponentHeaderKey.builder().uuid(componentUuid)
                .build();

        HttpComponentHeader httpComponentHeaders1 = HttpComponentHeader.builder()
                .id(uuid.toString())
                .httpComponentHeaderID(httpComponentTraceKey)
                .name("test")
                .value("test").build();
        httpComponentHeaders.add(httpComponentHeaders1);

        HttpComponentQueryKey httpComponentQueryKey = HttpComponentQueryKey.builder().uuid(componentUuid)
                .build();
        List<HttpComponentQuery> httpComponentQuerys = new ArrayList<>();
        UUID uuid0 = UUID.randomUUID();
        HttpComponentQuery httpComponentQuery1 = HttpComponentQuery.builder()
                .id(uuid0.toString())
                .httpComponentQueryID(httpComponentQueryKey).name("test")
                .value("test").build();
        httpComponentQuerys.add(httpComponentQuery1);

        httpComponentTrace = HttpComponentTrace.builder()
                .metadataKey(componetKey)
                .runId("testRunid")
                .processId(1L)
                .actionParameter("actionParameter")
                .componentID("componentID")
                .componentTypeParameter("componentTypeParameter")
                .componentName("componentName")
                .componentDescription(1L)
                .componentVersion(1L)
                .componentVersionDescription("componentVersionDescription")
                .connectionName("connectionName")
                .type("type")
                .endpoint("endpoint")
                .httpComponentHeader(httpComponentHeaders)
                .httpComponentQueries(httpComponentQuerys)
                .build();

        componentUuid2 = UUID.randomUUID();
        ComponentTraceKey componentKey2 = ComponentTraceKey.builder().uuid(componentUuid2)
                .build();
        UUID uuid01 = UUID.randomUUID();
        List<HttpComponentHeader> httpComponentHeaders2 = new ArrayList<>();
        HttpComponentHeaderKey httpComponentTraceKey2 = HttpComponentHeaderKey.builder().uuid(componentUuid2)
                .build();
        HttpComponentHeader httpComponentHeaders3 = HttpComponentHeader.builder()
                .id(uuid01.toString())
                .httpComponentHeaderID(httpComponentTraceKey2)
                .name("test")
                .value("test").build();
        httpComponentHeaders2.add(httpComponentHeaders3);

        HttpComponentQueryKey httpComponentQueryKey2 = HttpComponentQueryKey.builder().uuid(componentUuid2)
                .build();
        List<HttpComponentQuery> httpComponentQuerys2 = new ArrayList<>();

        UUID uuid1 = UUID.randomUUID();
        String randomUUIDString = uuid1.toString();

        HttpComponentQuery httpComponentQuery3 = HttpComponentQuery.builder()
                .id(randomUUIDString)
                .httpComponentQueryID(httpComponentQueryKey2)
                .name("test2")
                .value("test2").build();

        httpComponentQuerys2.add(httpComponentQuery3);
        UUID uuid2 = UUID.randomUUID();
        String randomUUIDString2 = uuid2.toString();

        HttpComponentQuery httpComponentQuery4 = HttpComponentQuery.builder()
                .id(randomUUIDString2)
                .httpComponentQueryID(httpComponentQueryKey2).name("test4")
                .value("test4").build();
        httpComponentQuerys2.add(httpComponentQuery4);

        httpComponentTrace2 = HttpComponentTrace.builder()
                .metadataKey(componentKey2)
                .runId("testRunid")
                .processId(1L)
                .actionParameter("actionParameter")
                .componentID("componentID")
                .componentTypeParameter("componentTypeParameter")
                .componentName("componentName")
                .componentDescription(1L)
                .componentVersion(1L)
                .componentVersionDescription("componentVersionDescription")
                .connectionName("connectionName")
                .type("type")
                .endpoint("endpoint")
                .httpComponentHeader(httpComponentHeaders2)
                .httpComponentQueries(httpComponentQuerys2)
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
                .contains(httpComponentTrace, httpComponentTrace2);
    }
}
