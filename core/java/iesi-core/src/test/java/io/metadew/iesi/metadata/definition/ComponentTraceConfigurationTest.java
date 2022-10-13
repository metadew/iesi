package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.component.trace.ComponentTraceConfiguration;
import io.metadew.iesi.metadata.definition.component.trace.ComponentTraceKey;
import io.metadew.iesi.metadata.definition.component.trace.http.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class,  ComponentTraceConfiguration.class })
@ActiveProfiles("test")
class ComponentTraceConfigurationTest {

    HttpComponentTrace httpComponentTrace;
    HttpComponentTrace httpComponentTrace2;
    UUID componentUuid;
    UUID componentUuid2;

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    ComponentTraceConfiguration componentTraceConfiguration;

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
        List<HttpComponentQueryParameterTrace> httpComponentQueryParameterTraces = new ArrayList<>();
        UUID uuid0 = UUID.randomUUID();
        HttpComponentQueryParameterTrace httpComponentQueryParameterTrace1 = HttpComponentQueryParameterTrace.builder()
                .metadataKey(new HttpComponentQueryParameterTraceKey(uuid0))
                .httpComponentQueryID(httpComponentQueryKey).name("test")
                .value("test").build();
        httpComponentQueryParameterTraces.add(httpComponentQueryParameterTrace1);

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
                .httpComponentQueries(httpComponentQueryParameterTraces)
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
        List<HttpComponentQueryParameterTrace> httpComponentQuerys2Trace = new ArrayList<>();

        UUID uuid1 = UUID.randomUUID();

        HttpComponentQueryParameterTrace httpComponentQueryParameterTrace3 = HttpComponentQueryParameterTrace.builder()
                .metadataKey(new HttpComponentQueryParameterTraceKey(uuid1))
                .httpComponentQueryID(httpComponentQueryKey2)
                .name("test2")
                .value("test2").build();

        UUID uuid2 = UUID.randomUUID();

        HttpComponentQueryParameterTrace httpComponentQueryParameterTrace4 = HttpComponentQueryParameterTrace.builder()
                .metadataKey(new HttpComponentQueryParameterTraceKey(uuid2))
                .httpComponentQueryID(httpComponentQueryKey2).name("test4")
                .value("test4").build();
        httpComponentQuerys2Trace.add(httpComponentQueryParameterTrace3);
        httpComponentQuerys2Trace.add(httpComponentQueryParameterTrace4);

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

        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void testGetAllEmpty() {
        assertThat(componentTraceConfiguration.getAll())
                .isEmpty();
    }

    @Test
    void testInsert() {
        componentTraceConfiguration.insert(httpComponentTrace);
        assertThat(componentTraceConfiguration.get(httpComponentTrace.getMetadataKey()))
                .hasValue(httpComponentTrace);
        assertThat(httpComponentTrace.getRunId()).isEqualTo("testRunid");
    }

    @Test
    void testGetAll() {
        componentTraceConfiguration.insert(httpComponentTrace);
        componentTraceConfiguration.insert(httpComponentTrace2);
        assertThat(componentTraceConfiguration.getAll())
                .containsOnly(httpComponentTrace, httpComponentTrace2);
    }
}
