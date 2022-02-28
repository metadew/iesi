package io.metadew.iesi.script.execution.instruction.data.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class JsonPathTraversal implements DataInstruction {

    private static final String TEXT = "text";
    private static final String JSON_PATH = "jsonPath";

    private static final Pattern PATTERN = Pattern.compile(
            "\\s*(?<" + TEXT + ">[\\s\\S]+)\\s*" +
            ",\\s*(?<" + JSON_PATH + ">.+)");

    @Override
    public String getKeyword() {
        return "text.jsonpath";
    }

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameter = PATTERN.matcher(parameters);

        if (inputParameter.find()) {
            try {
                String text = inputParameter.group(TEXT);
                String jsonPath = inputParameter.group(JSON_PATH);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(text);
                return jsonNode.at(jsonPath).asText();
            }
            catch (JsonProcessingException e) {
                log.error(e.getMessage());
                throw new IllegalArgumentException(String.format("%s %s:%s", e.getMessage(), this.getKeyword(), parameters));
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Illegal arguments provided to %s:%s", this.getKeyword(), parameters));
        }
    }
}
