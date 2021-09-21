package io.metadew.iesi.script.action.socket;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.connection.network.SocketConnection;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ActionPerformanceLogger;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class SocketTransmitMessage extends ActionTypeExecution {

    private static Logger LOGGER = LogManager.getLogger();
    private static final String SOCKET_KEY = "socket";
    private static final String MESSAGE_KEY = "message";
    private static final String PROTOCOL_KEY = "protocol";
    private static final String OUTPUT_KEY = "output";
    private static final String TIMEOUT_KEY = "timeout";

    private String message;
    private String protocol;
    private SocketConnection socket;
    private DatabaseDatasetImplementation outputDataset;
    private Integer timeout;

    public SocketTransmitMessage(ExecutionControl executionControl,
                                 ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
        this.message = convertMessage(getParameterResolvedValue(MESSAGE_KEY));
        this.protocol = convertProtocol(getParameterResolvedValue(PROTOCOL_KEY));
        this.socket = convertSocket(getParameterResolvedValue(SOCKET_KEY));
        this.outputDataset = (DatabaseDatasetImplementation) convertOutputDataset(getParameterResolvedValue(OUTPUT_KEY));
    }

    private Integer convertTimeout(DataType timeout) {
        if (timeout == null || timeout instanceof Null) {
            return null;
        } else if (timeout instanceof Text) {
            try {
                return Integer.parseInt(((Text) timeout).getString());
            } catch (NumberFormatException e) {
                throw new RuntimeException(MessageFormat.format("timeout not defined as an integer: ''{0}''", ((Text) timeout).getString()));
            }
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for timeout",
                    timeout.getClass()));
            throw new RuntimeException(MessageFormat.format("timeout does not allow type ''{0}''", timeout.getClass()));
        }
    }

    private DatasetImplementation convertOutputDataset(DataType dataset) {
        if (dataset == null || dataset instanceof Null) {
            return null;
        } else if (dataset instanceof Text) {
            return getExecutionControl().getExecutionRuntime().getDataset(((Text) dataset).getString())
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("No dataset found with name ''{0}''", ((Text) dataset).getString())));
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for output dataset",
                    dataset.getClass()));
            throw new RuntimeException(MessageFormat.format("Output dataset does not allow type ''{0}''", dataset.getClass()));
        }
    }

    protected boolean executeAction() throws IOException, InterruptedException {
        if (protocol.equalsIgnoreCase("tcp")) {
            sendTCPMessage();
        } else if (protocol.equalsIgnoreCase("udp")) {
            sendUDPMessage();
        }
        return true;
    }

    @Override
    protected String getKeyword() {
        return "socket.transmitMessage";
    }

    private void sendUDPMessage() throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress(socket.getHostName(), socket.getPort());
        DatagramSocket datagramSocket = new DatagramSocket(socketAddress);
        DatagramPacket datagramPacketToSend = new DatagramPacket(message.getBytes(Charset.forName(socket.getEncoding())),
                message.getBytes(Charset.forName(socket.getEncoding())).length);
        datagramSocket.send(datagramPacketToSend);
        if (getOutputDataset().isPresent()) {
            DatabaseDatasetImplementationService.getInstance().clean(outputDataset, getExecutionControl().getExecutionRuntime());
            byte[] buffer = new byte[65508];
            DatagramPacket datagramPacketToReceive = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(datagramPacketToReceive);
            DatabaseDatasetImplementationService.getInstance().setDataItem(outputDataset, "response", new Text(new String(datagramPacketToReceive.getData(), 0, datagramPacketToReceive.getLength(), Charset.forName(socket.getEncoding()))));
        }
    }

    private void sendTCPMessage() throws IOException {
        Socket tcpSocket = new Socket(socket.getHostName(), socket.getPort());
        DataOutputStream dOut = new DataOutputStream(tcpSocket.getOutputStream());
        DataInputStream dIn = new DataInputStream(tcpSocket.getInputStream());
        LocalDateTime start = LocalDateTime.now();
        dOut.write(message.getBytes(Charset.forName(socket.getEncoding())));
        dOut.flush();
        if (getOutputDataset().isPresent()) {
            DatabaseDatasetImplementationService.getInstance().clean(outputDataset, getExecutionControl().getExecutionRuntime());
            LocalDateTime endDateTime;
            if (timeout == null) {
                endDateTime = LocalDateTime.now()
                        .plus((Integer) Configuration.getInstance().getMandatoryProperty("iesi.actions.socket.transmitMessage.timeout.default"),
                                ChronoUnit.SECONDS);
            } else {
                endDateTime = LocalDateTime.now().plus(timeout, ChronoUnit.SECONDS);
            }
            while (LocalDateTime.now().isBefore(endDateTime)) {
                if (dIn.available() > 0) {
                    byte[] bytes = new byte[dIn.available()];
                    int bytesRead = dIn.read(bytes);
                    LocalDateTime end = LocalDateTime.now();
                    DatabaseDatasetImplementationService.getInstance().setDataItem(outputDataset, "response", new Text(new String(bytes, 0, bytesRead, Charset.forName(socket.getEncoding()))));
                    ActionPerformanceLogger.getInstance().log(getActionExecution(), "response", start, end);
                    break;
                }
            }
        }
        dOut.close();
        dIn.close();
    }

    private SocketConnection convertSocket(DataType socket) {
        if (socket instanceof Text) {
            return ConnectionConfiguration.getInstance()
                    .get(new ConnectionKey(((Text) socket).getString(), getExecutionControl().getEnvName()))
                    .map(SocketConnection::from)
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find connection {0}", ((Text) socket).getString())));
        } else {
            throw new RuntimeException(MessageFormat.format("{0} does not accept {1} as type for socket name",
                    getActionExecution().getAction().getType(), socket.getClass()));
        }
    }

    private String convertProtocol(DataType protocol) {
        if (protocol instanceof Text) {
            return ((Text) protocol).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("{0} does not accept {1} as type for protocol",
                    getActionExecution().getAction().getType(), protocol.getClass()));
        }
    }

    private String convertMessage(DataType message) {
        if (message instanceof Text) {
            return ((Text) message).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("{0} does not accept {1} as type for message",
                    getActionExecution().getAction().getType(), message.getClass()));
        }
    }

    private Optional<DatabaseDatasetImplementation> getOutputDataset() {
        return Optional.ofNullable(outputDataset);
    }

}
