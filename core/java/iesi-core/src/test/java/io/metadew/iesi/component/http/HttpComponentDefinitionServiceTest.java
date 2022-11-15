package io.metadew.iesi.component.http;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.metadata.configuration.component.trace.ComponentDesignTraceConfiguration;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.script.execution.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = { HttpComponentDefinitionService.class, HttpComponentDesignTraceService.class, ComponentDesignTraceConfiguration.class})
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class HttpComponentDefinitionServiceTest {

    @Autowired
    private HttpComponentDefinitionService httpComponentDefinitionService;

    @Test
    void convertTest() {
        ComponentKey componentKey = new ComponentKey("96e2d9e8-fb5b-4554-ac0b-3acd938f0127", 1L);

        ActionRuntime actionRuntime = mock(ActionRuntime.class);
        ActionControl actionControl = mock(ActionControl.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        ActionExecution actionExecution = mock(ActionExecution.class);

        when(actionExecution.getExecutionControl())
                .thenReturn(executionControl);
        when(executionControl.getExecutionRuntime())
                .thenReturn(executionRuntime);
        when(actionExecution.getActionControl())
                .thenReturn(actionControl);
        when(actionControl.getActionRuntime())
                .thenReturn(actionRuntime);
        when(actionExecution.getExecutionControl().getRunId())
                .thenReturn("runId");

        Component component = Component.builder()
                .componentKey(componentKey)
                .version(new ComponentVersion(new ComponentVersionKey(componentKey), "version description"))
                .type("http.request")
                .name("component1")
                .description("description")
                .parameters(Stream.of(
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "connection"))
                                .value("connection1")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "endpoint"))
                                .value("endpoint")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "type"))
                                .value("get")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "header.1"))
                                .value("content-type,application/json")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "header.2"))
                                .value("content-length,1000")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "queryparam.1"))
                                .value("name,test")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "queryparam.2"))
                                .value("version,2")
                                .build()
                ).collect(Collectors.toList()))
                .attributes(Stream.of(
                        ComponentAttribute.builder()
                                .componentAttributeKey(new ComponentAttributeKey(componentKey, new EnvironmentKey("test"), "attribute1"))
                                .value("value1")
                                .build()
                ).collect(Collectors.toList()))
                .build();

        assertThat(httpComponentDefinitionService.convert(component, actionExecution))
                .isEqualTo(new HttpComponentDefinition(
                        "component1",
                        1L,
                        "description",
                        "connection1",
                        "endpoint",
                        "get",
                        Stream.of(new HttpHeaderDefinition("content-type", "application/json"), new HttpHeaderDefinition("content-length", "1000")).collect(Collectors.toList()),
                        Stream.of(new HttpQueryParameterDefinition("name", "test"), new HttpQueryParameterDefinition("version", "2")).collect(Collectors.toList())
                ));
    }

    @Test
    void convertWrongTypeTest() {
        ComponentKey componentKey = new ComponentKey("96e2d9e8-fb5b-4554-ac0b-3acd938f0127", 1L);

        ActionExecution actionExecution = mock(ActionExecution.class);
        ActionRuntime actionRuntime = mock(ActionRuntime.class);
        ActionControl actionControl = mock(ActionControl.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        when(actionExecution.getExecutionControl())
                .thenReturn(executionControl);
        when(executionControl.getExecutionRuntime())
                .thenReturn(executionRuntime);
        when(actionExecution.getActionControl())
                .thenReturn(actionControl);
        when(actionControl.getActionRuntime())
                .thenReturn(actionRuntime);

        Component component = Component.builder()
                .componentKey(componentKey)
                .version(new ComponentVersion(new ComponentVersionKey(componentKey), "version description"))
                .type("not.http.request")
                .name("component1")
                .description("description")
                .parameters(Stream.of(
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "connection"))
                                .value("connection1")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "endpoint"))
                                .value("endpoint")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "type"))
                                .value("get")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "header.1"))
                                .value("content-type,application/json")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "header.2"))
                                .value("content-length,1000")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "queryparam.1"))
                                .value("name,test")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "queryparam.2"))
                                .value("version,2")
                                .build()
                ).collect(Collectors.toList()))
                .attributes(Stream.of(
                        ComponentAttribute.builder()
                                .componentAttributeKey(new ComponentAttributeKey(componentKey, new EnvironmentKey("test"), "attribute1"))
                                .value("value1")
                                .build()
                ).collect(Collectors.toList()))
                .build();
        assertThatThrownBy(() -> httpComponentDefinitionService.convert(component, actionExecution))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot convert " + component.toString() + " to http component");
    }

    @Test
    void convertNoTypeTest() {
        ComponentKey componentKey = new ComponentKey("96e2d9e8-fb5b-4554-ac0b-3acd938f0127", 1L);

        ActionExecution actionExecution = mock(ActionExecution.class);
        ActionRuntime actionRuntime = mock(ActionRuntime.class);
        ActionControl actionControl = mock(ActionControl.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        when(actionExecution.getExecutionControl())
                .thenReturn(executionControl);
        when(executionControl.getExecutionRuntime())
                .thenReturn(executionRuntime);
        when(actionExecution.getActionControl())
                .thenReturn(actionControl);
        when(actionControl.getActionRuntime())
                .thenReturn(actionRuntime);

        Component component = Component.builder()
                .componentKey(componentKey)
                .version(new ComponentVersion(new ComponentVersionKey(componentKey), "version description"))
                .type("http.request")
                .name("component1")
                .description("description")
                .parameters(Stream.of(
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "connection"))
                                .value("connection1")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "endpoint"))
                                .value("endpoint")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "header.1"))
                                .value("content-type,application/json")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "header.2"))
                                .value("content-length,1000")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "queryparam.1"))
                                .value("name,test")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "queryparam.2"))
                                .value("version,2")
                                .build()
                ).collect(Collectors.toList()))
                .attributes(Stream.of(
                        ComponentAttribute.builder()
                                .componentAttributeKey(new ComponentAttributeKey(componentKey, new EnvironmentKey("test"), "attribute1"))
                                .value("value1")
                                .build()
                ).collect(Collectors.toList()))
                .build();
        assertThatThrownBy(() -> httpComponentDefinitionService.convert(component, actionExecution))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Http component " + component.toString() + " does not contain a type");
    }

    @Test
    void convertNoEndpointTest() {
        ActionExecution actionExecution = mock(ActionExecution.class);
        ActionRuntime actionRuntime = mock(ActionRuntime.class);
        ActionControl actionControl = mock(ActionControl.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        when(actionExecution.getExecutionControl())
                .thenReturn(executionControl);
        when(executionControl.getExecutionRuntime())
                .thenReturn(executionRuntime);
        when(actionExecution.getActionControl())
                .thenReturn(actionControl);
        when(actionControl.getActionRuntime())
                .thenReturn(actionRuntime);

        ComponentKey componentKey = new ComponentKey("96e2d9e8-fb5b-4554-ac0b-3acd938f0127", 1L);
        Component component = Component.builder()
                .componentKey(componentKey)
                .version(new ComponentVersion(new ComponentVersionKey(componentKey), "version description"))
                .type("http.request")
                .name("component1")
                .description("description")
                .parameters(Stream.of(
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "connection"))
                                .value("connection1")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "type"))
                                .value("get")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "header.1"))
                                .value("content-type,application/json")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "header.2"))
                                .value("content-length,1000")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "queryparam.1"))
                                .value("name,test")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "queryparam.2"))
                                .value("version,2")
                                .build()
                ).collect(Collectors.toList()))
                .attributes(Stream.of(
                        ComponentAttribute.builder()
                                .componentAttributeKey(new ComponentAttributeKey(componentKey, new EnvironmentKey("test"), "attribute1"))
                                .value("value1")
                                .build()
                ).collect(Collectors.toList()))
                .build();
        assertThatThrownBy(() -> httpComponentDefinitionService.convert(component, actionExecution))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Http component " + component.toString() + " does not contain an endpoint");
    }

    @Test
    void convertNoConnectionTest() {
        ActionExecution actionExecution = mock(ActionExecution.class);
        ActionRuntime actionRuntime = mock(ActionRuntime.class);
        ActionControl actionControl = mock(ActionControl.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        when(actionExecution.getExecutionControl())
                .thenReturn(executionControl);
        when(executionControl.getExecutionRuntime())
                .thenReturn(executionRuntime);
        when(actionExecution.getActionControl())
                .thenReturn(actionControl);
        when(actionControl.getActionRuntime())
                .thenReturn(actionRuntime);
        ComponentKey componentKey = new ComponentKey("96e2d9e8-fb5b-4554-ac0b-3acd938f0127", 1L);
        Component component = Component.builder()
                .componentKey(componentKey)
                .version(new ComponentVersion(new ComponentVersionKey(componentKey), "version description"))
                .type("http.request")
                .name("component1")
                .description("description")
                .parameters(Stream.of(
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "endpoint"))
                                .value("endpoint")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "type"))
                                .value("get")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "header.1"))
                                .value("content-type,application/json")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "header.2"))
                                .value("content-length,1000")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "queryparam.1"))
                                .value("name,test")
                                .build(),
                        ComponentParameter.builder()
                                .componentParameterKey(new ComponentParameterKey(componentKey, "queryparam.2"))
                                .value("version,2")
                                .build()
                ).collect(Collectors.toList()))
                .attributes(Stream.of(
                        ComponentAttribute.builder()
                                .componentAttributeKey(new ComponentAttributeKey(componentKey, new EnvironmentKey("test"), "attribute1"))
                                .value("value1")
                                .build()
                ).collect(Collectors.toList()))
                .build();
        assertThatThrownBy(() -> httpComponentDefinitionService.convert(component, actionExecution))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Http component " + component.toString() + " does not contain a connection");
    }
}
