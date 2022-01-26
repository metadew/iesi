package io.metadew.iesi.datatypes.array;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.LookupResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArrayServiceTest {

    @Test
    void appliesToTest() {
        assertEquals(Array.class, ArrayService.getInstance().appliesTo());
    }


    @Test
    void keywordTest() {
        assertEquals("list", ArrayService.getInstance().keyword());
    }

    @Test
    void resolveInputTest() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        when(executionRuntime.resolveVariables("testing1"))
                .thenReturn("testing1");
        when(executionRuntime.resolveConceptLookup("testing1"))
                .thenReturn(new LookupResult("testing1", null, null));

        when(executionRuntime.resolveVariables("testing2"))
                .thenReturn("testing2");
        when(executionRuntime.resolveConceptLookup("testing2"))
                .thenReturn(new LookupResult("testing2", null, null));

        when(executionRuntime.resolveVariables("testing3"))
                .thenReturn("testing3");
        when(executionRuntime.resolveConceptLookup("testing3"))
                .thenReturn(new LookupResult("testing3", null, null));

        assertEquals(new Array(Stream.of(new Text("testing1"), new Text("testing2"), new Text("testing3"))
                        .collect(Collectors.toList())),
                ArrayService.getInstance().resolve("testing1, testing2, testing3", executionRuntime));
    }

    @Test
    void resolveArrayNodeEmpty() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = (ArrayNode) objectMapper
                .readTree(new FileReader(getClass().getClassLoader().getResource("io.metadew.iesi.datatypes.array/array.empty.json").getFile()))
                .get("array");

        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        DatabaseDatasetImplementation keyValueDataset = mock(DatabaseDatasetImplementation.class);
        assertEquals(new Array(Collections.emptyList()),
                ArrayService.getInstance().resolve(keyValueDataset, "array", arrayNode, executionRuntime));
    }

    @Test
    void resolveArrayNodeSimple() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = (ArrayNode) objectMapper
                .readTree(new FileReader(getClass().getClassLoader().getResource("io.metadew.iesi.datatypes.array/array.simple.json").getFile()))
                .get("array");

        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
//        when(executionRuntime.resolveVariables(anyString()))
//                .thenReturn("testing1, testing2, testing3")
//                .thenReturn("testing1")
//                .thenReturn("testing2")
//                .thenReturn("testing3");

        DatabaseDatasetImplementation keyValueDataset = mock(DatabaseDatasetImplementation.class);
//        when(executionRuntime.resolveVariables(anyString()))
//                .thenReturn("testing1, testing2, testing3")
//                .thenReturn("testing1")
//                .thenReturn("testing2")
//                .thenReturn("testing3");
        assertEquals(new Array(Stream.of(new Text("1"), new Text("2"), new Text("3"), new Text("4")).collect(Collectors.toList())),
                ArrayService.getInstance().resolve(keyValueDataset, "array", arrayNode, executionRuntime));
    }

    @Test
    void resolveArrayNodeNested() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = (ArrayNode) objectMapper
                .readTree(new FileReader(getClass().getClassLoader().getResource("io.metadew.iesi.datatypes.array/array.array.json").getFile()))
                .get("array");

        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        DatabaseDatasetImplementation keyValueDataset = mock(DatabaseDatasetImplementation.class);

        assertEquals(new Array(Stream.of(new Array(Stream.of(new Text("1"), new Text("2")).collect(Collectors.toList())),
                new Array(Stream.of(new Text("3"), new Text("4")).collect(Collectors.toList()))).collect(Collectors.toList())),
                ArrayService.getInstance().resolve(keyValueDataset, "array", arrayNode, executionRuntime));
    }


    @Test
    void resolveArrayNodeComplex() throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ArrayNode arrayNode = (ArrayNode) objectMapper
//                .readTree(new FileReader(getClass().getClassLoader().getResource("io.metadew.iesi.datatypes.array/array.complex.json").getFile()))
//                .get("array");
//
//        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
//
//        KeyValueDataset keyValueDataset = mock(KeyValueDataset.class);
//        KeyValueDataset createdKeyValueDataset = mock(KeyValueDataset.class);
//
//        KeyValueDatasetService.getInstance();
//
//        KeyValueDatasetService keyValueDatasetService = spy(KeyValueDatasetService.getInstance());
//        doReturn(createdKeyValueDataset).when(keyValueDatasetService).resolve(any(), any(), any(), any());
//
//        assertEquals(new Array(Stream.of(new Text("1"),
//                new Array(Stream.of(new Text("1"), new Text("2")).collect(Collectors.toList())),
//                createdKeyValueDataset).collect(Collectors.toList())),
//                ArrayService.getInstance().resolve(keyValueDataset, "array", arrayNode, executionRuntime));
    }


    @Test
    void equalsTest() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        assertThat(ArrayService.getInstance().equals(new Array(new ArrayList<>()), new Array(new ArrayList<>()), executionRuntime))
                .isTrue();
        assertThat(
                ArrayService.getInstance().equals(
                        new Array(Stream.of(new Text("test"), new Text("test2")).collect(Collectors.toList())),
                        new Array(Stream.of(new Text("test"), new Text("test2")).collect(Collectors.toList())),
                        executionRuntime))
                .isTrue();
        assertThat(
                ArrayService.getInstance().equals(
                        new Array(Stream.of(new Array(Stream.of(new Text("test")).collect(Collectors.toList()))).collect(Collectors.toList())),
                        new Array(Stream.of(new Array(Stream.of(new Text("test")).collect(Collectors.toList()))).collect(Collectors.toList())),
                        executionRuntime))
                .isTrue();
        // TODO datasets

        assertThat(
                ArrayService.getInstance().equals(
                        null,
                        null,
                        executionRuntime))
                .isTrue();
        assertThat(
                ArrayService.getInstance().equals(
                        null,
                        new Array(Stream.of(new Text("test"), new Text("test3")).collect(Collectors.toList())),
                        executionRuntime))
                .isFalse();
        assertThat(
                ArrayService.getInstance().equals(
                        new Array(Stream.of(new Array(Stream.of(new Text("test")).collect(Collectors.toList()))).collect(Collectors.toList())),
                        null,
                        executionRuntime))
                .isFalse();
    }

}
