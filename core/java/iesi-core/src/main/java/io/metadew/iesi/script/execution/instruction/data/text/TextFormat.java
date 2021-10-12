package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class TextFormat implements DataInstruction {

    private static final String TEXT = "text";
    private static final String FORMAT = "format";

    private static final Pattern PATTERN = Pattern.compile("\\s*(?<" + TEXT + ">.+?)\\s*,\\s*(?<" + FORMAT + ">.+)");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameter = PATTERN.matcher(parameters);
        if (inputParameter.find()) {
            return String.format(inputParameter.group(FORMAT), inputParameter.group(TEXT));
        } else {
            throw new IllegalArgumentException(String.format("Illegal arguments provided to %s:%s", this.getKeyword(), parameters));
        }
    }

    @Override
    public String getKeyword() {
        return "text.format";
    }

}
