package io.metadew.iesi.script.execution.instruction.data.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonPathTraversalTest {

    @Test
    void jsonPathValidParametersOne() {
        // Arrange
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{\"tutorials\": [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = ".tutorials[0].title";

        // Act
        String result = jsonPathTraversal.generateOutput(jsonString + "," + jsonPath);

        // Assert
        assertEquals("Guava", result);
    }

    @Test
    void jsonPathValidParametersTwo() {
        // Arrange
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{\"tutorials\": [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = ".tutorials[0].description";

        // Act
        String result = jsonPathTraversal.generateOutput(jsonString + "," + jsonPath);

        // Assert
        assertEquals("Introduction to Guava", result);
    }

    @Test
    void jsonPathMissingCommaBetweenParametersShouldThrowException() {
        // Arrange
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = ".tutorials[0].description";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + jsonPath));
    }

    @Test
    void jsonPathMissingJsonPathShouldThrowException() {
        // Arrange
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString));
    }

    @Test
    void jsonPathMissingJsonStringShouldThrowException() {
        // Arrange
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonPath = ".tutorials[0].description";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonPath));
    }

    @Test
    void jsonPathMalformedJsonStringShouldThrowException() {
        // Arrange
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = ".tutorials[0].description";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathMalformedJsonPathShouldThrowException() {
        // Arrange
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "{tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = "tutorials[0].description";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathInvalidJsonStringWrongParentNameShouldThrowException() {
        // Arrange
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = ".tutori[0].description";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathInvalidJsonStringWrongChildNameShouldThrowException() {
        // Arrange
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = ".tutorials[0].descrion";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

    @Test
    void jsonPathInvalidJsonStringNonExistingArrayIndexShouldThrowException() {
        // Arrange
        JsonPathTraversal jsonPathTraversal = new JsonPathTraversal();
        String jsonString = "tutorials: [{\"title\": \"Guava\",\"description\": \"Introduction to Guava\"}]}";
        String jsonPath = ".tutorials[1].description";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> jsonPathTraversal.generateOutput(jsonString + "," + jsonPath));
    }

}
