package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class XMLPathTraversal implements DataInstruction {

    private final ExecutionRuntime executionRuntime;

    private static final String TEXT = "text";
    private static final String XML_PATH = "xmlPath";
    private static final Pattern PATTERN = Pattern.compile("(?<" + TEXT + ">[\\s\\S]+),(?<" + XML_PATH + ">.+)");

    private final DataTypeHandler dataTypeHandler = SpringContext.getBean(DataTypeHandler.class);

    public XMLPathTraversal(ExecutionRuntime executionRuntime) {
        this.executionRuntime = executionRuntime;
    }

    @Override
    public String getKeyword() {
        return "text.xmlpath";
    }

    @Override
    public String generateOutput(String parameters) {
        DataType resolvedParameters = dataTypeHandler.resolve(parameters, executionRuntime);
        if (!(resolvedParameters instanceof Text)) {
            throw new IllegalArgumentException(MessageFormat.format("text cannot be a type of", resolvedParameters.getClass()));
        }

        Matcher inputParameter = PATTERN.matcher(((Text) resolvedParameters).getString());

        if (inputParameter.find()) {
            String text = inputParameter.group(TEXT);
            String xmlPath = inputParameter.group(XML_PATH);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(text)));

                XPath xPath = XPathFactory.newInstance().newXPath();
                String result = (String) xPath.compile(xmlPath).evaluate(doc, XPathConstants.STRING);
                return result;
            } catch (SAXParseException e) {
                throw new IllegalArgumentException(String.format("%s %s:%s", e.getMessage(), this.getKeyword(), resolvedParameters));
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException(String.format("%s %s:%s", e.getMessage(), this.getKeyword(), resolvedParameters));
            }
        } else {
            throw new IllegalArgumentException(String.format("Illegal arguments provided to %s:%s", this.getKeyword(), resolvedParameters));
        }
    }
}
