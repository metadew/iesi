package io.metadew.iesi.script.execution.instruction.data.text;

import com.jayway.jsonpath.JsonPath;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class JsonPathTraversal implements DataInstruction {

    private static final String TEXT = "text";
    private static final String JSON_PATH = "jsonPath";

    private static final Pattern PATTERN = Pattern.compile("\\s*(?<" + TEXT + ">.+?)\\s*,\\s*(?<" + JSON_PATH + ">.+)");


    @Override
    public String getKeyword() {
        return "text.jsonPath";
    }

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameter = PATTERN.matcher(parameters);
        if (inputParameter.find()) {
            String text = inputParameter.group(TEXT);
            String jsonPath = inputParameter.group(JSON_PATH);
            List<String> result = JsonPath.read(text, jsonPath);
            return result.toString();
        }
        else {
            throw new IllegalArgumentException(String.format("Illegal arguments provided to %s:%s", this.getKeyword(), parameters));
        }
    }
}
