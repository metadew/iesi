package io.metadew.iesi.script.action.conn;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;

@Log4j2
public class ConnIsAlive extends ActionTypeExecution {

    private final ConnectionConfiguration connectionConfiguration = SpringContext.getBean(ConnectionConfiguration.class);

    // Parameters

    private final static String CONNECTION_KEY = "connection";
    public ConnIsAlive(ExecutionControl executionControl, ScriptExecution scriptExecution,
                       ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
    }

    protected boolean executeAction() throws InterruptedException {
        // Get Connection
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_KEY));
        Connection connection = connectionConfiguration
                .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .get();

        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    @Override
    protected String getKeyword() {
        return "conn.isAlive";
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            log.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }


}