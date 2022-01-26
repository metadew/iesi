package io.metadew.iesi.script.execution.instruction.data.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonPathTraversalTest {

    @Test
    void jsonPathValidParametersOne() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{\"tutorials\": [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/title";

        String result = jsonPathTraversal.generateOutput(jsonString + "," + jsonPath);

        assertEquals("Guava", result);
    }

    @Test
    void jsonPathValidParametersTwo() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{\"tutorials\": [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/description";

        String result = jsonPathTraversal.generateOutput(jsonString + "," + jsonPath);

        assertEquals("Introduction to Guava", result);
    }

    @Test
    void jsonPathValidParametersThree() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{\"tutorials\": [{\"title\": \"guava\", \"test\": {\"id\": 1}}]}";
        String jsonPath = "/tutorials/0/title";

        String result = jsonPathTraversal.generateOutput(jsonString + "," + jsonPath);

        assertEquals("guava", result);
    }

    @Test
    void jsonPathValidParametersFour() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{\"menu\": {\"id\": \"file\",\"value\": \"File\", \"popup\": { \"menuitem\": [{\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},{\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},{\"value\":\"Close\",\"onclick\":\"CloseDoc()\"}]}}}";

        String jsonPath = "/menu/id";

        String result = jsonPathTraversal.generateOutput(jsonString + "," + jsonPath);

        assertEquals("file", result);
    }

    @Test
    void jsonPathMissingCommaBetweenParametersShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/description";

        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + jsonPath));
    }

    @Test
    void jsonPathMissingJsonPathShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";

        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString));
    }

    @Test
    void jsonPathMissingJsonStringShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonPath = "/tutorials/0/description";

        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonPath));
    }

    @Test
    void jsonPathMalformedJsonStringShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/description";

        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathMalformedJsonPathShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/description";

        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathInvalidJsonStringWrongParentNameShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutoria/0/description";

        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathInvalidJsonStringWrongChildNameShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/0/descrion";

        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathInvalidJsonStringNonExistingArrayIndexShouldThrowException() {
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "/tutorials/1/description";

        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }
}
