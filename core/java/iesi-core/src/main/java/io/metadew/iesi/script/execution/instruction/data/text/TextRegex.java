package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import org.apache.xerces.impl.xpath.regex.Match;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextRegex implements DataInstruction {

    private final ExecutionRuntime executionRuntime;

    private final String TEXT = "text";
    private final String PATTERN = "pattern";

    private final Pattern pattern = Pattern.compile("(?<" + TEXT +">.+),(?<" + PATTERN + ">.+)");

    public TextRegex(ExecutionRuntime executionRuntime) {
        this.executionRuntime = executionRuntime;
    }


    @Override
    public String getKeyword() {
        return "text.regex";
    }

    @Override
    public String generateOutput(String parameters) {
        DataType resolvedParameters = SpringContext.getBean(DataTypeHandler.class).resolve(parameters, executionRuntime);
        if (!(resolvedParameters instanceof Text)) {
            throw new IllegalArgumentException(String.format("text cannot be a type of %s", resolvedParameters.getClass()));
        }

        Matcher matcher = pattern.matcher(((Text) resolvedParameters).getString());

        if (matcher.find()) {
            Pattern providedPattern = Pattern.compile(matcher.group(PATTERN));

            Matcher providedMatcher = providedPattern.matcher(matcher.group(TEXT));

            if (providedMatcher.find()) {
                return providedMatcher.group();
            }
        }

        return "";
    }
}
