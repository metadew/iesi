package io.metadew.iesi.datatypes.text;

import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import io.metadew.iesi.datatypes.IDataTypeService;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;

@Log4j2
public class TextService implements IDataTypeService<Text> {

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

    @Override
    public boolean equals(Text _this, Text other, ExecutionRuntime executionRuntime) {
        if (_this.getString() == null && other.getString() == null) {
            return true;
        } else if (_this.getString() != null) {
            return _this.getString().equals(other.getString());
        } else {
            return false;
        }
    }

    public Text resolve(ValueNode jsonNode) {
        return new Text(jsonNode.asText());
    }

    public Text resolve(NullNode jsonNode) {
        return new Text("");
    }
}
