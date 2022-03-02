package io.metadew.iesi.datatypes.text;

import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import io.metadew.iesi.datatypes.IDataTypeService;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class TextService implements IDataTypeService<Text> {

    private static TextService INSTANCE;
    private final Pattern ESCAPE_CONCEPT_PATTERNS = Pattern.compile("<!(.*?[\\\\{\\\\}]*[\\S\\s]*?.*?[\\S\\s]*?)!>");

    public synchronized static TextService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TextService();
        }
        return INSTANCE;
    }

    private TextService() {
    }

    @Override
    public Class<Text> appliesTo() {
        return Text.class;
    }

    @Override
    public String keyword() {
        return "text";
    }

    public Text resolve(String input, ExecutionRuntime executionRuntime) {
        log.trace(MessageFormat.format("resolving {0} for Text", input));
        String resolvedVariable = executionRuntime.resolveVariables(input);
        Matcher matcher = ESCAPE_CONCEPT_PATTERNS.matcher(resolvedVariable);

        while (matcher.find()) {
            resolvedVariable = resolvedVariable.replaceAll(ESCAPE_CONCEPT_PATTERNS.pattern(), "$1");
            matcher = ESCAPE_CONCEPT_PATTERNS.matcher(resolvedVariable);
        }
        return new Text(resolvedVariable);
    }


    public boolean equals(Text _this, Text other, ExecutionRuntime executionRuntime) {
        if (_this.getString() == null && other.getString() == null) {
            return true;
        } else if (_this.getString() == null || other.getString() == null) {
            return false;
        } else {
            return _this.getString().replaceAll("\\s+", "").equals(other.getString().replaceAll("\\s+", ""));
        }
    }

    public Text resolve(ValueNode jsonNode) {
        return new Text(jsonNode.asText());
    }

    public Text resolve(NullNode jsonNode) {
        return new Text("");
    }
}
