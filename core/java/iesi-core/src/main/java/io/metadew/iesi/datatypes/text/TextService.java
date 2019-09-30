package io.metadew.iesi.datatypes.text;

import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

public class TextService {

    private final static Logger LOGGER = LogManager.getLogger();

    public Text resolve(String input, ExecutionRuntime executionRuntime) {
        LOGGER.trace(MessageFormat.format("resolving {0} for Text", input));
        return new Text(executionRuntime.resolveVariables(input));
    }

    public Text resolve(ValueNode jsonNode) throws IOException, SQLException {
        return new Text(jsonNode.asText());
    }

    public Text resolve(NullNode jsonNode) throws IOException, SQLException {
        return new Text("");
    }
}
