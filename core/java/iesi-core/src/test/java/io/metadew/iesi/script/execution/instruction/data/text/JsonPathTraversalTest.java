package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = DataTypeHandler.class )
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext
@ActiveProfiles("test")
class JsonPathTraversalTest {

    ExecutionRuntime executionRuntime;
    JsonPathTraversal jsonPathTraversal;

    @SpyBean
    DataTypeHandler dataTypeHandlerSpy;

    @BeforeEach
    public void before() {
        executionRuntime = mock(ExecutionRuntime.class);
        jsonPathTraversal = new JsonPathTraversal(executionRuntime);
    }

    @Test
    void jsonPathValidParametersOne() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonString = "{\"tutorials\": [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/title";

        doReturn(new Text((jsonString + "," + jsonPath))).when(dataTypeHandlerSpy).resolve(jsonString + "," + jsonPath, executionRuntime);
        String result = jsonPathTraversal.generateOutput(jsonString + "," + jsonPath);

        assertEquals("Guava", result);
    }

    @Test
    void jsonPathValidParametersTwo() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonString = "{\"tutorials\": [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/description";

        doReturn(new Text((jsonString + "," + jsonPath))).when(dataTypeHandlerSpy).resolve(jsonString + "," + jsonPath, executionRuntime);
        String result = jsonPathTraversal.generateOutput(jsonString + "," + jsonPath);

        assertEquals("Introduction to Guava", result);
    }

    @Test
    void jsonPathValidParametersThree() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonString = "{\"tutorials\": [{\"title\": \"guava\", \"test\": {\"id\": 1}}]}";
        String jsonPath = "/tutorials/0/title";

        doReturn(new Text((jsonString + "," + jsonPath))).when(dataTypeHandlerSpy).resolve(jsonString + "," + jsonPath, executionRuntime);
        String result = jsonPathTraversal.generateOutput(jsonString + "," + jsonPath);

        assertEquals("guava", result);
    }

    @Test
    void jsonPathValidParametersFour() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonString = "{\"menu\": {\"id\": \"file\",\"value\": \"File\", \"popup\": { \"menuitem\": [{\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},{\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},{\"value\":\"Close\",\"onclick\":\"CloseDoc()\"}]}}}";
        String jsonPath = "/menu/id";

        doReturn(new Text((jsonString + "," + jsonPath))).when(dataTypeHandlerSpy).resolve(jsonString + "," + jsonPath, executionRuntime);
        String result = jsonPathTraversal.generateOutput(jsonString + "," + jsonPath);

        assertEquals("file", result);
    }

    @Test
    void jsonPathMissingCommaBetweenParametersShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonString = "{tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/description";

        doReturn(new Text((jsonString + jsonPath))).when(dataTypeHandlerSpy).resolve(jsonString + jsonPath, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + jsonPath));
    }

    @Test
    void jsonPathMissingJsonPathShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonString = "{tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";

        doReturn(new Text((jsonString))).when(dataTypeHandlerSpy).resolve(jsonString, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString));
    }

    @Test
    void jsonPathMissingJsonStringShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonPath = "/tutorials/0/description";

        doReturn(new Text((jsonPath))).when(dataTypeHandlerSpy).resolve(jsonPath, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonPath));
    }

    @Test
    void jsonPathMalformedJsonStringShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/description";

        doReturn(new Text((jsonString + "," + jsonPath))).when(dataTypeHandlerSpy).resolve(jsonString + "," + jsonPath, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathMalformedJsonPathShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonString = "{tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/description";

        doReturn(new Text((jsonString + "," + jsonPath))).when(dataTypeHandlerSpy).resolve(jsonString + "," + jsonPath, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathInvalidJsonStringWrongParentNameShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutoria/0/description";

        doReturn(new Text((jsonString + "," + jsonPath))).when(dataTypeHandlerSpy).resolve(jsonString + "," + jsonPath, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathInvalidJsonStringWrongChildNameShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/descrion";

        doReturn(new Text((jsonString + "," + jsonPath))).when(dataTypeHandlerSpy).resolve(jsonString + "," + jsonPath, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathInvalidJsonStringNonExistingArrayIndexShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal(executionRuntime);
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/1/description";

        doReturn(new Text((jsonString + "," + jsonPath))).when(dataTypeHandlerSpy).resolve(jsonString + "," + jsonPath, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }
}
