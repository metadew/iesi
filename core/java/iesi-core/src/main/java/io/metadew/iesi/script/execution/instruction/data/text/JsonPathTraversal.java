package io.metadew.iesi.script.execution.instruction.data.text;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class JsonPathTraversal implements DataInstruction {

    private static final String TEXT = "text";
    private static final String JSON_PATH = "jsonPath";

    private static final Pattern PATTERN = Pattern.compile("\\s*(?<" + TEXT + ">.+?)\\s*,\\s*\\.(?<" + JSON_PATH + ">.+)");

    @Override
    public String getKeyword() {
        return "text.jsonpath";
    }

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameter = PATTERN.matcher(parameters);
        String result = "";

        if (inputParameter.find()) {
            try {
                String text = inputParameter.group(TEXT);
                String jsonPath = inputParameter.group(JSON_PATH);
                result = JsonPath.read(text, "$." + jsonPath).toString();
            }
            catch (PathNotFoundException e) {
                throw new IllegalArgumentException(e.getMessage() + " " + this.getKeyword() + ":" + parameters);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Illegal arguments provided to %s:%s", this.getKeyword(), parameters));
        }

        return result;
    }
}