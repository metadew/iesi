package io.metadew.iesi.script.execution.instruction.data.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XMLPathTraversalTest {

    @Test
    void xmlPathValidParameters1() {
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/title";

        String result = xmlPathTraversal.generateOutput(xmlString + "," + xmlPath);

        assertEquals("Guava", result);
    }

    @Test
    void xmlPathValidParameters2() {
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/description";

        String result = xmlPathTraversal.generateOutput(xmlString + "," + xmlPath);

        assertEquals("Introduction to Guava", result);
    }

    @Test
    void xmlPathShouldNotRequireXmlHeader() {
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlString = "<Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/title";

        String result = xmlPathTraversal.generateOutput(xmlString + "," + xmlPath);

        assertEquals("Guava", result);
    }

    @Test
    void xmlPathMissingCommaBetweenParametersShouldThrowException() {
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/description";

        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlString + xmlPath));
    }

    @Test
    void xmlPathMissingXmlPathShouldThrowException() {
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlString = "<?xml version=\"1.0\"?><Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorial></Tutorials>";

        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlString));
    }

    @Test
    void xmlPathMissingXmlStringShouldThrowException() {
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlPath = "/Tutorials/Tutorial[1]/description";

        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlPath));
    }

    @Test
    void xmlPathMalformedXmlStringShouldThrowException() {
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();

        String xmlString = "<Tutorials><Tutorial><title>Guava</title><description>Introduction to Guava</description></Tutorials>";
        String xmlPath = "/Tutorials/Tutorial[1]/title";

        assertThrows(IllegalArgumentException.class, () -> xmlPathTraversal.generateOutput(xmlString + "," + xmlPath));
    }

    @Test
    void xmlPathWithSpacesAndBreakLines() {
        XMLPathTraversal xmlPathTraversal = new XMLPathTraversal();
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:CustomerExamAnswersResponse xmlns=\"http://schemas.dvsys.dv.be/entity/agkp/person/v1.0\" xmlns:ns2=\"http://schemas.dvsys.dv.be/interface/agkp/person/management/v1.0\">     <ExamId>1</ExamId>     <CustomerPSI>42</CustomerPSI>     <NrOfAttempts>1</NrOfAttempts>     <ExamResult>passed</ExamResult>     \n" +
                "    <ExamResultId>750</ExamResultId> \n" +
                "   </ns2:CustomerExamAnswersResponse>\n" +
                "\n";
        String xmlPath = "/CustomerExamAnswersResponse/ExamResult";

        String result = xmlPathTraversal.generateOutput(xmlString + "," + xmlPath);

        assertEquals("passed", result);
    }
}
