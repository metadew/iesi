package io.metadew.iesi.datatypes.text;

import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

@Log4j2
public class TextService implements DataTypeService<Text> {

    private static TextService INSTANCE;

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
        return new Text(executionRuntime.resolveVariables(input));
    }

    public Text resolve(ValueNode jsonNode) {
        return new Text(jsonNode.asText());
    }

    public Text resolve(NullNode jsonNode) {
        return new Text("");
    }
}
