package io.metadew.iesi.script.execution.instruction.data.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class JsonPathTraversal implements DataInstruction {

    private final ExecutionRuntime executionRuntime;
    private static final String TEXT = "text";
    private static final String JSON_PATH = "jsonPath";

    private final DataTypeHandler dataTypeHandler = SpringContext.getBean(DataTypeHandler.class);

    public JsonPathTraversal(ExecutionRuntime executionRuntime) {
        this.executionRuntime = executionRuntime;
    }

    private static final Pattern PATTERN = Pattern.compile(
            "\\s*(?<" + TEXT + ">[\\s\\S]+)\\s*" +
            ",\\s*(?<" + JSON_PATH + ">.+)");

    @Override
    public String getKeyword() {
        return "text.jsonpath";
    }

    @Override
    public String generateOutput(String parameters) {
        DataType resolvedParameters = dataTypeHandler.resolve(parameters, executionRuntime);
        if (!(resolvedParameters instanceof Text)) {
            throw new IllegalArgumentException(MessageFormat.format("text cannot be a type of", resolvedParameters.getClass()));
        }

        Matcher inputParameter = PATTERN.matcher(((Text) resolvedParameters).getString());

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
                throw new IllegalArgumentException(String.format("%s %s:%s", e.getMessage(), this.getKeyword(), resolvedParameters));
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Illegal arguments provided to %s:%s", this.getKeyword(), resolvedParameters));
        }
    }
}
