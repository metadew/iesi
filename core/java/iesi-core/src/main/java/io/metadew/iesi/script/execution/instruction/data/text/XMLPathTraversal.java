package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class XMLPathTraversal implements DataInstruction {

    private static final String TEXT = "text";
    private static final String XML_PATH = "xmlPath";

    private static final Pattern PATTERN = Pattern.compile("\\s*(?<" + TEXT + ">.+?)\\s*,\\s*(?<" + XML_PATH + ">.+)");


    @Override
    public String getKeyword() {
        return "text.xmlPath";
    }

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameter = PATTERN.matcher(parameters);
        if (inputParameter.find()) {
            String text = inputParameter.group(TEXT);
            String xmlPath = inputParameter.group(XML_PATH);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;

            try
            {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(text)));
                return doc.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Illegal arguments provided to %s:%s", this.getKeyword(), parameters));
        }
        return null;
    }
}
