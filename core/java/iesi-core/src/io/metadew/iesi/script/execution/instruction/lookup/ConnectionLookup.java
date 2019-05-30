package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.metadata.configuration.ConnectionParameterConfiguration;
import io.metadew.iesi.script.execution.ExecutionControl;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionLookup implements LookupInstruction {

    private final ExecutionControl executionControl;

    private final String CONNECTION_NAME_KEY = "name";

    private final String CONNECTION_PARAMETER_NAME_KEY = "parameterName";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + CONNECTION_NAME_KEY + ">(\\w|\\.)+)\"?\\s*,\\s*(?<" + CONNECTION_PARAMETER_NAME_KEY + ">(\\w|\\.)+)\\s*");

    public ConnectionLookup(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    @Override
    public String getKeyword() {
        return "connection";
    }

    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to connection lookup: {0}", parameters));
        }
        String connectionName = inputParameterMatcher.group(CONNECTION_NAME_KEY);
        String connectionParameterName = inputParameterMatcher.group(CONNECTION_PARAMETER_NAME_KEY);

        ConnectionParameterConfiguration connectionParameterConfiguration = new ConnectionParameterConfiguration(
                executionControl.getFrameworkExecution().getFrameworkInstance());
        Optional<String> connectionParameterValue = connectionParameterConfiguration.getConnectionParameterValue(connectionName,
                executionControl.getEnvName(), connectionParameterName);

        if (!connectionParameterValue.isPresent()) {
            throw new IllegalArgumentException(MessageFormat.format("No connection parameter {0} is attached to connection {1}", connectionParameterName, connectionName));
        } else {
            return connectionParameterValue.get();
        }
    }
}
