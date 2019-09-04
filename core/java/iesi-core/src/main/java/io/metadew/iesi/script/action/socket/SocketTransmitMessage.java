package io.metadew.iesi.script.action.socket;

import io.metadew.iesi.connection.network.SocketConnection;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Optional;

public class SocketTransmitMessage {

    private static Logger LOGGER = LogManager.getLogger();
    private static final String socketKey = "socket";
    private static final String messageKey = "message";
    private static final String protocolKey = "protocol";
    private static final String outputKey = "output";

    private final ExecutionControl executionControl;
    private final ActionExecution actionExecution;
    private final HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private String message;
    private String protocol;
    private SocketConnection socket;
    private Dataset outputDataset;

    public SocketTransmitMessage(ExecutionControl executionControl,
                                 ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
    }

    public void prepare() {
        // Reset Parameters
        ActionParameterOperation socketActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), socketKey);
        ActionParameterOperation messageActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), messageKey);
        ActionParameterOperation protocolActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), protocolKey);
        ActionParameterOperation outputActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), outputKey);

        // Get Parameters
        for (ActionParameter actionParameter : actionExecution.getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase(messageKey)) {
                messageActionParameterOperation.setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase(socketKey)) {
                socketActionParameterOperation.setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase(protocolKey)) {
                protocolActionParameterOperation.setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase(outputKey)) {
                outputActionParameterOperation.setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        actionParameterOperationMap.put(socketKey, messageActionParameterOperation);
        actionParameterOperationMap.put(messageKey, socketActionParameterOperation);
        actionParameterOperationMap.put(protocolKey, protocolActionParameterOperation);
        actionParameterOperationMap.put(outputKey, outputActionParameterOperation);

        this.message = convertMessage(messageActionParameterOperation.getValue());
        this.protocol = convertProtocol(protocolActionParameterOperation.getValue());
        this.socket = convertSocket(socketActionParameterOperation.getValue());
        this.outputDataset = convertOutputDataset(outputActionParameterOperation.getValue());
    }

    private Dataset convertOutputDataset(DataType dataset) {
        if (dataset == null) {
            return null;
        } else if (dataset instanceof Text) {
            return executionControl.getExecutionRuntime().getDataset(((Text) dataset).getString())
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("No dataset found with name ''{0}''", ((Text) dataset).getString())));
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for output dataset",
                    dataset.getClass()));
            throw new RuntimeException(MessageFormat.format("Output dataset does not allow type ''{0}''", dataset.getClass()));
        }
    }

    public boolean execute() {
        try {
            if (protocol.equalsIgnoreCase("tcp")) {
                sendTCPMessage();
            } else if (protocol.equalsIgnoreCase("udp")) {
                sendUDPMessage();
            }
            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            actionExecution.getActionControl().increaseErrorCount();

            actionExecution.getActionControl().logOutput("exception", e.getMessage());
            actionExecution.getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
    }

    private void sendUDPMessage() throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress(socket.getHostName(), socket.getPort());
        DatagramSocket datagramSocket = new DatagramSocket(socketAddress);
        DatagramPacket datagramPacketToSend = new DatagramPacket(message.getBytes(), message.getBytes().length);
        datagramSocket.send(datagramPacketToSend);
        if (getOutputDataset().isPresent()) {
                byte[] buffer = new byte[65508];
                DatagramPacket datagramPacketToReceive = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacketToReceive);
                outputDataset.setDataItem("response", new Text(new String(datagramPacketToReceive.getData(), 0, datagramPacketToReceive.getLength())));
        }
    }

    private void sendTCPMessage() throws IOException {
        Socket tcpSocket = new Socket(socket.getHostName(), socket.getPort());
        DataOutputStream dOut = new DataOutputStream(tcpSocket.getOutputStream());
        DataInputStream dIn = new DataInputStream(tcpSocket.getInputStream());
        dOut.writeUTF(message);
        dOut.flush();
        if (getOutputDataset().isPresent()) {
            LocalDateTime endDateTime = LocalDateTime.now().plus(1000, ChronoUnit.MILLIS);
            while (LocalDateTime.now().isBefore(endDateTime)) {
                if (dIn.available() > 0) {
                    String response = dIn.readUTF();
                    outputDataset.setDataItem("response", new Text(response));
                    break;
                }
            }
        }
        dOut.close();
        dIn.close();
    }

    private SocketConnection convertSocket(DataType socket) {
        if (socket instanceof Text) {
            ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
            return connectionConfiguration.get(((Text) socket).getString(), executionControl.getEnvName())
                    .map(SocketConnection::from)
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find connection {0}", ((Text) socket).getString())));
        } else {
            throw new RuntimeException(MessageFormat.format("{0} does not accept {1} as type for socket name",
                    actionExecution.getAction().getType(), socket.getClass()));
        }
    }

    private String convertProtocol(DataType protocol) {
        if (protocol instanceof Text) {
            return ((Text) protocol).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("{0} does not accept {1} as type for protocol",
                    actionExecution.getAction().getType(), protocol.getClass()));
        }
    }

    private String convertMessage(DataType message) {
        if (message instanceof Text) {
            return ((Text) message).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("{0} does not accept {1} as type for message",
                    actionExecution.getAction().getType(), message.getClass()));
        }
    }

    private Optional<Dataset> getOutputDataset() {
        return Optional.ofNullable(outputDataset);
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }
}
