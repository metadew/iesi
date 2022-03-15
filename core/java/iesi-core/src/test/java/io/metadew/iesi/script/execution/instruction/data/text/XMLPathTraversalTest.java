package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class XMLPathTraversalTest {

    DataTypeHandler dataTypeHandler;
    DataTypeHandler dataTypeHandlerServiceSpy;
    ExecutionRuntime executionRuntime;
    XMLPathTraversal xmlPathTraversal;

    @BeforeEach
    public void before() {
        dataTypeHandler = DataTypeHandler.getInstance();
        dataTypeHandlerServiceSpy = Mockito.spy(dataTypeHandler);
        Whitebox.setInternalState(DataTypeHandler.class, "instance", dataTypeHandlerServiceSpy);
        executionRuntime = mock(ExecutionRuntime.class);
        xmlPathTraversal = new XMLPathTraversal(executionRuntime);
    }

    @AfterEach
    public void after() {
        Whitebox.setInternalState(DataTypeHandler.class, "instance", (DataTypeHandler) null);
    }

    @Test
    void xmlPathValidParameters1() {
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/title";

        doReturn(new Text((xmlString + "," + xmlPath))).when(dataTypeHandlerServiceSpy).resolve(xmlString + "," + xmlPath, executionRuntime);

        String result = xmlPathTraversal.generateOutput(xmlString + "," + xmlPath);

        assertEquals("Guava", result);
    }

    @Test
    void xmlPathValidParameters2() {
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/description";

        doReturn(new Text((xmlString + "," + xmlPath))).when(dataTypeHandlerServiceSpy).resolve(xmlString + "," + xmlPath, executionRuntime);
        String result = xmlPathTraversal.generateOutput(xmlString + "," + xmlPath);

        assertEquals("Introduction to Guava", result);
    }

    @Test
    void xmlPathShouldNotRequireXmlHeader() {
        String xmlString = "<Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/title";

        doReturn(new Text((xmlString + "," + xmlPath))).when(dataTypeHandlerServiceSpy).resolve(xmlString + "," + xmlPath, executionRuntime);
        String result = xmlPathTraversal.generateOutput(xmlString + "," + xmlPath);

        assertEquals("Guava", result);
    }

    @Test
    void xmlPathWithSpacesAndBreakLines() {
        String xmlString = "<Tutorials> \n <Tutorial>\n<title>Guava</title>\n<description>Introduction to Guava</description>\n\n</Tutorial>\n\n</Tutorials>";
        String xmlPathTitle = "/Tutorials/Tutorial[1]/title";
        String xmmPathDescription = "/Tutorials/Tutorial[1]/description";

        doReturn(new Text((xmlString + "," + xmlPathTitle))).when(dataTypeHandlerServiceSpy).resolve(xmlString + "," + xmlPathTitle, executionRuntime);
        doReturn(new Text((xmlString + "," + xmmPathDescription))).when(dataTypeHandlerServiceSpy).resolve(xmlString + "," + xmmPathDescription, executionRuntime);
        assertEquals("Guava", xmlPathTraversal.generateOutput(xmlString + "," + xmlPathTitle));
        assertEquals("Introduction to Guava", xmlPathTraversal.generateOutput(xmlString + "," + xmmPathDescription));
    }

    @Test
    void xmlPathWithCommaInsideFirstArgument() {
        String xmlString = "<Tutorials> \n <Tutorial>\n<title>Guava</title>\n<description>Introduction to Guava,</description>\n</Tutorial>\n</Tutorials>";
        String xmlPathTitle = "/Tutorials/Tutorial[1]/title";
        String xlmPathDescription = "/Tutorials/Tutorial[1]/description";

        doReturn(new Text((xmlString + "," + xmlPathTitle))).when(dataTypeHandlerServiceSpy).resolve(xmlString + "," + xmlPathTitle, executionRuntime);
        doReturn(new Text((xmlString + "," + xlmPathDescription))).when(dataTypeHandlerServiceSpy).resolve(xmlString + "," + xlmPathDescription, executionRuntime);
        assertEquals("Guava", xmlPathTraversal.generateOutput(xmlString + "," + xmlPathTitle));
        assertEquals("Introduction to Guava,", xmlPathTraversal.generateOutput(xmlString + "," + xlmPathDescription));
    }

    @Test
    void xmlPathWithMultipleCommaInsideFirstArgument() {
        String xmlString = "<Tutorials> \n <Tutorial>\n<title>Guava,</title>\n<description>,Introduct,ion to Guava,</description>\n</Tutorial>\n</Tutorials>";
        String xmlPathTitle = "/Tutorials/Tutorial[1]/title";
        String xlmPathDescription = "/Tutorials/Tutorial[1]/description";

        doReturn(new Text((xmlString + "," + xmlPathTitle))).when(dataTypeHandlerServiceSpy).resolve(xmlString + "," + xmlPathTitle, executionRuntime);
        doReturn(new Text((xmlString + "," + xlmPathDescription))).when(dataTypeHandlerServiceSpy).resolve(xmlString + "," + xlmPathDescription, executionRuntime);
        assertEquals("Guava,", xmlPathTraversal.generateOutput(xmlString + "," + xmlPathTitle));
        assertEquals(",Introduct,ion to Guava,", xmlPathTraversal.generateOutput(xmlString + "," + xlmPathDescription));
    }

    @Test
    void xmlPathWithCommaInsideSecondtArgument() {
        String xmlString = "<Tutorials> \n <Tutorial>\n<title>Guava</title>\n<description>Introduction to Guava</description>\n</Tutorial>\n</Tutorials>";
        String xmlPathTitle = "/Tutorials/Tutorial[1]/title,1";
        doReturn(new Text((xmlString + xmlPathTitle))).when(dataTypeHandlerServiceSpy).resolve(xmlString + xmlPathTitle, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlString + xmlPathTitle));
    }

    @Test
    void xmlPathMissingCommaBetweenParametersShouldThrowException() {
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/description";
        doReturn(new Text((xmlString + xmlPath))).when(dataTypeHandlerServiceSpy).resolve(xmlString + xmlPath, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlString + xmlPath));
    }

    @Test
    void xmlPathMissingXmlPathShouldThrowException() {
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        doReturn(new Text((xmlString))).when(dataTypeHandlerServiceSpy).resolve(xmlString, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlString));
    }

    @Test
    void xmlPathMissingXmlStringShouldThrowException() {
        String xmlPath = "/Tutorials/Tutorial[1]/description";

        doReturn(new Text((xmlPath))).when(dataTypeHandlerServiceSpy).resolve(xmlPath, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlPath));
    }

    @Test
    void xmlPathMalformedXmlStringShouldThrowException() {
        String xmlString = "<Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/title";

        doReturn(new Text((xmlString + "," + xmlPath))).when(dataTypeHandlerServiceSpy).resolve(xmlString + "," + xmlPath, executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlString + "," + xmlPath));
    }
}