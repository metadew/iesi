package io.metadew.iesi.datatypes.text;

import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.io.IOException;
import java.sql.SQLException;

public class TextService {

    public Text resolve(String input) {
        return new Text(input);
    }

    public Text resolve(ValueNode jsonNode) throws IOException, SQLException {
        return new Text(jsonNode.asText());
    }

    public Text resolve(NullNode jsonNode) throws IOException, SQLException {
        return new Text("");
    }
}
