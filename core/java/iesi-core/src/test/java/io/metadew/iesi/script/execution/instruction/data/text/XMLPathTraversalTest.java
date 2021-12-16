package io.metadew.iesi.script.execution.instruction.data.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XMLPathTraversalTest {

    @Test
    void xmlPathValidParameters1() {
        // Arrange
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/title";

        // Act
        String result = xmlPathTraversal.generateOutput(xmlString + "," + xmlPath);

        // Assert
        assertEquals("Guava", result);
    }

    @Test
    void xmlPathValidParameters2() {
        // Arrange
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/description";

        // Act
        String result = xmlPathTraversal.generateOutput(xmlString + "," + xmlPath);

        // Assert
        assertEquals("Introduction to Guava", result);
    }

    @Test
    void xmlPathShouldNotRequireXmlHeader() {
        // Arrange
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlString = "<Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/title";

        // Act
        String result = xmlPathTraversal.generateOutput(xmlString + "," + xmlPath);

        // Assert
        assertEquals("Guava", result);
    }

    @Test
    void xmlPathMissingCommaBetweenParametersShouldThrowException() {
        // Arrange
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/description";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlString + xmlPath));
    }

    @Test
    void xmlPathMissingXmlPathShouldThrowException() {
        // Arrange
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlString));
    }

    @Test
    void xmlPathMissingXmlStringShouldThrowException() {
        // Arrange
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlPath = "/Tutorials/Tutorial[1]/description";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlPath));
    }

    @Test
    void xmlPathMalformedXmlStringShouldThrowException() {
        // Arrange
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();

        // Following XML string is missing a closing 'Tutorial' tag
        String xmlString = "<Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/title";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlString + "," + xmlPath));
    }
}
