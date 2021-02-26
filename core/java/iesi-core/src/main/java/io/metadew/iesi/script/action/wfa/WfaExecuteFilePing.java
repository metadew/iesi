package io.metadew.iesi.script.action.wfa;

import com.jcraft.jsch.*;
import io.metadew.iesi.common.text.ParsingTools;
import io.metadew.iesi.connection.FileConnection;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.LinuxHostUserInfo;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WfaExecuteFilePing extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation filePath;
    private ActionParameterOperation fileName;
    private ActionParameterOperation expectedResult;
    private ActionParameterOperation setRuntimeVariables;
    private ActionParameterOperation connectionName;
    private ActionParameterOperation waitInterval;
    private ActionParameterOperation timeoutInterval;
    private long startTime;
    private final int defaultWaitInterval = 1000;
    private final int defaultTimeoutInterval = -1;
    private static final Logger LOGGER = LogManager.getLogger();


    public WfaExecuteFilePing(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }


    public void prepare() {
        // Set Parameters
        this.setFilePath(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "filePath"));
        this.setFileName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "fileName"));
        this.setExpectedResult(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "hasResult"));
        this.setSetRuntimeVariables(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "setRuntimeVariables"));
        this.setConnectionName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "connection"));
        this.setWaitInterval(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "wait"));
        this.setTimeoutInterval(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "timeout"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("filepath")) {
                this.getFilePath().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("filename")) {
                this.getFileName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("hasresult")) {
                this.getExpectedResult().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("setruntimevariables")) {
                this.getSetRuntimeVariables().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("wait")) {
                this.getWaitInterval().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("timeout")) {
                this.getTimeoutInterval().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("filePath", this.getFilePath());
        this.getActionParameterOperationMap().put("fileName", this.getFileName());
        this.getActionParameterOperationMap().put("hasResult", this.getExpectedResult());
        this.getActionParameterOperationMap().put("setRuntimeVariables", this.getSetRuntimeVariables());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
        this.getActionParameterOperationMap().put("wait", this.getWaitInterval());
        this.getActionParameterOperationMap().put("timeout", this.getTimeoutInterval());
    }

    protected boolean executeAction() throws InterruptedException {
        // Get Connection

        String filePath = convertFilePath(getFilePath().getValue());
        String fileName = convertFileName(getFileName().getValue());
        boolean hasResult = convertHasResult(getExpectedResult().getValue());
        boolean setRuntimeVariables = converSetRuntimeVariables(getSetRuntimeVariables().getValue());
        String connectionName = convertConnectionName(getConnectionName().getValue());
        int timeoutInterval = convertTimeoutInterval(getTimeoutInterval().getValue());
        int waitInterval = convertWaitInterval(getWaitInterval().getValue());
        Connection connection = ConnectionConfiguration.getInstance()
                .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .orElseThrow(InterruptedException::new);
        ConnectionOperation connectionOperation = new ConnectionOperation();
        HostConnection dcConnection = connectionOperation.getHostConnection(connection);

        // Check if connection is localhost
        boolean connectionIsLocalHost = connectionOperation.isOnLocalConnection(dcConnection);

        // Run the action
        int i = 1;
        long wait = waitInterval * 1000;
        if (wait <= 0)
            wait = 1000;
        boolean checkTimeout = false;
        long timeout = timeoutInterval * 1000;
        long timeoutCounter = 0;
        if (timeout > 0)
            checkTimeout = true;

        boolean done = false;
        this.setStartTime(System.currentTimeMillis());
        while (i == 1) {
            if (this.doneWaiting(connection, connectionIsLocalHost, filePath, fileName, hasResult, setRuntimeVariables)) {
                done = true;
                break;
            }

            if (checkTimeout) {
                timeoutCounter += wait;
                if (timeoutCounter >= timeout)
                    break;
            }

            try {
                Thread.sleep(wait);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        }

        long elapsedTime = System.currentTimeMillis() - this.getStartTime();
        if (done) {
            this.getActionExecution().getActionControl().increaseSuccessCount();

            this.getActionExecution().getActionControl().logOutput("out", "result found");
            this.getActionExecution().getActionControl().logOutput("time", Long.toString(elapsedTime));
        } else {
            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("action.error", "File ping timed out");
            this.getActionExecution().getActionControl().logOutput("time", Long.toString(elapsedTime));
        }
        return true;
    }


    private int convertWaitInterval(DataType waitInterval) {
        if (waitInterval == null) {
            return defaultWaitInterval;
        }
        if (waitInterval instanceof Text) {
            return Integer.parseInt(waitInterval.toString());
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for wait interval",
                    waitInterval.getClass()));
            return defaultWaitInterval;
        }
    }

    private int convertTimeoutInterval(DataType timeoutInterval) {
        if (timeoutInterval == null) {
            return defaultTimeoutInterval;
        }
        if (timeoutInterval instanceof Text) {
            return Integer.parseInt(timeoutInterval.toString());
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for timeout interval",
                    timeoutInterval.getClass()));
            return defaultTimeoutInterval;
        }
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private boolean converSetRuntimeVariables(DataType setRuntimeVariables) {
        if (setRuntimeVariables == null) {
            return false;
        }
        if (setRuntimeVariables instanceof Text) {
            return setRuntimeVariables.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for setRuntimeVariables",
                    setRuntimeVariables.getClass()));
            return false;
        }
    }

    private boolean convertHasResult(DataType hasResult) {
        if (hasResult == null) {
            return false;
        }
        if (hasResult instanceof Text) {
            return hasResult.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for hasResult",
                    hasResult.getClass()));
            return false;
        }
    }

    private String convertFileName(DataType fileName) {
        if (fileName instanceof Text) {
            return fileName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for file name",
                    fileName.getClass()));
            return fileName.toString();
        }
    }

    private String convertFilePath(DataType filePath) {
        if (filePath instanceof Text) {
            return filePath.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for file path",
                    filePath.getClass()));
            return filePath.toString();
        }
    }

    private boolean doneWaiting(Connection connection, boolean connectionIsLocalHost, String filePath, String fileName, boolean hasResult, boolean setRuntimeVariables) {
        List<FileConnection> connectionsFound;
        if (connectionIsLocalHost) {
            connectionsFound = this.checkLocalFolder(filePath, fileName);
        } else {
            connectionsFound = this.checkRemoteFolder(connection, filePath, fileName);
        }

        if (connectionsFound.size() > 0) {
            if (hasResult) {
                // this.setRuntimeVariable(crs, setRuntimeVariables);
                return true;
            } else {
                return false;
            }
        } else {
            if (hasResult) {
                return true;
            } else {
                return false;
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<FileConnection> checkLocalFolder(String filePath, String fileName) {
        List<FileConnection> connectionsFound = new ArrayList();
        final File folder = new File(filePath);
        if (fileName.equalsIgnoreCase("*") || fileName.equalsIgnoreCase("")) {
            // Check all files
            for (final File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    // Ignore
                } else {
                    FileConnection connectionFound = new FileConnection();
                    connectionFound.setLongName(file.getAbsolutePath());
                    connectionFound.setFileName(file.getName());
                    connectionFound.setFilePath(file.getPath());
                    connectionsFound.add(connectionFound);
                }
            }
        } else if (ParsingTools.isRegexFunction(fileName)) {
            // Check regex expression files
            final String fileFilter = fileName;
            final File[] files = FolderTools.getFilesInFolder(folder.getAbsolutePath(), "regex", fileFilter);

            for (final File file : files) {
                if (file.isDirectory()) {
                    // Ignore
                } else {
                    FileConnection connectionFound = new FileConnection();
                    connectionFound.setLongName(file.getAbsolutePath());
                    connectionFound.setFileName(file.getName());
                    connectionFound.setFilePath(file.getPath());
                    connectionsFound.add(connectionFound);
                }
            }
        } else {
            // Check exact file name
            final String fileFilter = fileName;
            final File[] files = FolderTools.getFilesInFolder(folder.getAbsolutePath(), "match", fileFilter);
            for (final File file : files) {
                if (file.isDirectory()) {
                    // Ignore
                } else {
                    FileConnection connectionFound = new FileConnection();
                    connectionFound.setLongName(file.getAbsolutePath());
                    connectionFound.setFileName(file.getName());
                    connectionFound.setFilePath(file.getPath());
                    connectionsFound.add(connectionFound);
                }
            }
        }

        return connectionsFound;

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<FileConnection> checkRemoteFolder(Connection connection, String filePath, String fileName) {
        List<FileConnection> connectionsFound = new ArrayList();
        ConnectionOperation connectionOperation = new ConnectionOperation();
        HostConnection hostConnection = connectionOperation.getHostConnection(connection);
        this.getActionExecution().getActionControl().logOutput("conn.name", connection.getMetadataKey().getName());

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(hostConnection.getUserName(), hostConnection.getHostName(),
                    hostConnection.getPortNumber());
            session.setConfig("StrictHostKeyChecking", "no");
            UserInfo ui = new LinuxHostUserInfo(hostConnection.getUserPassword());
            session.setUserInfo(ui);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp c = (ChannelSftp) channel;

            c.cd(filePath);

            String fileFilter;

            Vector vv = null;

            if (fileName.equalsIgnoreCase("*") || fileName.equalsIgnoreCase("")) {
                // Check all files
                vv = c.ls(filePath);
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {

                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            String attributes = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString();
                            if (attributes.substring(0, 1).equalsIgnoreCase("d")) {
                                // Ignore directories
                            } else {
                                FileConnection connectionFound = new FileConnection();
                                connectionFound.setLongName(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getLongname());
                                connectionFound.setFileName(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename());
                                connectionFound.setAttributes(
                                        ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString());
                                connectionsFound.add(connectionFound);
                            }
                        }
                    }
                }

            } else if (ParsingTools.isRegexFunction(fileName)) {
                // Check regex expression files
                fileFilter = fileName;
                vv = c.ls(filePath);
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {

                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            String file_match = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename();
                            if (file_match.matches(fileFilter)) {
                                String attributes = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString();
                                if (attributes.substring(0, 1).equalsIgnoreCase("d")) {
                                    // Ignore directories
                                } else {
                                    FileConnection connectionFound = new FileConnection();
                                    connectionFound.setLongName(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getLongname());
                                    connectionFound.setFileName(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename());
                                    connectionFound.setAttributes(
                                            ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString());
                                    connectionsFound.add(connectionFound);
                                }
                            }
                        }
                    }
                }

            } else {
                // Check exact file name
                fileFilter = fileName;
                vv = c.ls(this.getFilePath().getName());
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {

                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            String attributes = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString();
                            if (attributes.substring(0, 1).equalsIgnoreCase("d")) {
                                // Ignore directories
                            } else {
                                FileConnection connectionFound = new FileConnection();
                                connectionFound.setLongName(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getLongname());
                                connectionFound.setFileName(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename());
                                connectionFound.setAttributes(
                                        ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString());
                                connectionsFound.add(connectionFound);
                            }
                        }
                    }
                }

            }
        } catch (SftpException | JSchException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return connectionsFound;

    }

    public ActionParameterOperation getWaitInterval() {
        return waitInterval;
    }

    public void setWaitInterval(ActionParameterOperation waitInterval) {
        this.waitInterval = waitInterval;
    }

    public ActionParameterOperation getTimeoutInterval() {
        return timeoutInterval;
    }

    public void setTimeoutInterval(ActionParameterOperation timeoutInterval) {
        this.timeoutInterval = timeoutInterval;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public ActionParameterOperation getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(ActionParameterOperation expectedResult) {
        this.expectedResult = expectedResult;
    }

    public ActionParameterOperation getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(ActionParameterOperation connectionName) {
        this.connectionName = connectionName;
    }

    public ActionParameterOperation getFilePath() {
        return filePath;
    }

    public void setFilePath(ActionParameterOperation filePath) {
        this.filePath = filePath;
    }

    public ActionParameterOperation getFileName() {
        return fileName;
    }

    public void setFileName(ActionParameterOperation fileName) {
        this.fileName = fileName;
    }


    public ActionParameterOperation getSetRuntimeVariables() {
        return setRuntimeVariables;
    }

    public void setSetRuntimeVariables(ActionParameterOperation setRuntimeVariables) {
        this.setRuntimeVariables = setRuntimeVariables;
    }

}