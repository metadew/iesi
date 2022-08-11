package io.metadew.iesi.connection;

import com.jcraft.jsch.*;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.FrameworkRuntime;
import io.metadew.iesi.connection.host.LinuxHostUserInfo;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Connection object for hosts. This is extended depending on the host type.
 *
 * @author peter.billen
 */
public class HostConnection {

    private String type;
    private String hostName;
    private int portNumber;
    private String userName;
    private String userPassword;
    private String tempPath;
    private String terminalFlag;
    private String jumphostConnectionName;
    private String allowLocalhostExecution;
    private String outputSystemOutput;
    private String outputReturnCode;
    private String outputRuntimeVariablesOutput;
    private List<String> systemOutputKeywordList;

    private final ConnectionOperation connectionOperation = SpringContext.getBean(ConnectionOperation.class);
    private final ConnectionConfiguration connectionConfiguration = SpringContext.getBean(ConnectionConfiguration.class);

    // Session management
    // private Session[] sessions;
    public HostConnection() {
        this("", "", -1, "", null, "", "Y", "", "Y",
                "", "", "");
    }

    public HostConnection(String type, String hostName, int portNumber, String userName, String userPassword, String tempPath,
                          String terminalFlag, String jumphostConnectionName, String allowLocalhostExecution,
                          String outputSystemOutput, String outputReturnCode, String outputRuntimeVariablesOutput) {
        this.type = type;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.userName = userName;
        this.userPassword = userPassword;
        this.tempPath = tempPath;
        this.terminalFlag = terminalFlag;
        this.jumphostConnectionName = jumphostConnectionName;
        this.allowLocalhostExecution = allowLocalhostExecution;
        this.outputSystemOutput = outputSystemOutput;
        this.outputReturnCode = outputReturnCode;
        this.outputRuntimeVariablesOutput = outputRuntimeVariablesOutput;

        this.systemOutputKeywordList = new ArrayList<>();
        this.systemOutputKeywordList.add("SHELL_RUN_CMD");
        this.systemOutputKeywordList.add("SHELL_RUN_CMD_RC");
        this.systemOutputKeywordList.add("SHELL_RUN_CMD_RUN_VAR");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public HostConnection(String type, String hostName, int portNumber, String userName, String userPassword,
                          String tempPath, String terminalFlag, String jumphostConnectionName, String allowLocalhostExecution) {
        this(type, hostName, portNumber, userName, userPassword, tempPath, terminalFlag, jumphostConnectionName,
                allowLocalhostExecution, "", "", "");
    }

    // Constructor for localhost override
    public HostConnection(String type) {
        this.setType(type);
    }

    // Methods
    public String getFileSeparator() {
        String output = "";

        if (this.getType().equalsIgnoreCase("windows")) {
            output = "\\";
        } else {
            output = "/";
        }

        return output;
    }

    private String formatCommand(String input) {
        String output = "";

        if (this.getType().equalsIgnoreCase("windows")) {
            // For Windows Commands, "cmd /c" needs to be put in front of the command
            output = "cmd /c " + "\"" + input + "\"";
        } else {
            output = input;
        }

        return output;
    }

    private String getMultiCommandAppender() {
        String output = "";

        if (this.getType().equalsIgnoreCase("windows")) {
            output = "&";
        } else {
            output = "&&";
        }

        return output;
    }

    public boolean isOnLocalhost() {
        boolean result = true;

        // Check if execution can be performed as being on localhost
        if (this.getAllowLocalhostExecution().equalsIgnoreCase("y")) {
            try {
                if (this.localhostFileExists(FrameworkRuntime.getInstance().getLocalHostChallengeFileName().toString())) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            result = false;
        }
        return result;
    }

    private boolean localhostFileExists(String fileName) throws IOException, InterruptedException {
        String command = "";

        if (this.getType().equalsIgnoreCase("windows")) {
            command = this.formatCommand("dir " + fileName);
        } else {
            command = this.formatCommand("ls " + fileName);
        }

        // Execute command
        int rc;
        final Process p = Runtime.getRuntime().exec(command);

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        String lines = "";

        try {
            while ((line = input.readLine()) != null) {
                if (!lines.equalsIgnoreCase(""))
                    lines = lines + "\n";
                lines = lines + line;
            }
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        rc = p.waitFor();

        if (rc == 0) {
            return true;
        } else {
            return false;
        }

    }

    public ShellCommandResult executeLocalCommand(String shellPath, String shellCommand) {
        String executionShellPath = "";
        String executionShellCommand = "";
        this.formatCommand("");
        executionShellPath = shellPath.trim();
        if (executionShellPath.equalsIgnoreCase("")) {
            executionShellCommand = this.formatCommand(shellCommand);
        } else {
            executionShellCommand = this.formatCommand(
                    "cd " + executionShellPath + " " + this.getMultiCommandAppender() + " " + shellCommand);
        }

        // Command execution
        int rc;
        String systemOutput = "";
        String errorOutput = "";
        try {
            final Process p = Runtime.getRuntime().exec(executionShellCommand);

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            String lines = "";

            try {
                while ((line = input.readLine()) != null) {
                    if (!lines.equalsIgnoreCase(""))
                        lines = lines + "\n";
                    lines = lines + line;
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            rc = p.waitFor();
            systemOutput = lines;

            errorOutput = IOUtils.toString(p.getErrorStream());
            input.close();
        } catch (InterruptedException | IOException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            rc = 1;
            errorOutput = StackTrace.toString();
        }

        return new ShellCommandResult(rc, systemOutput, errorOutput);
    }

    public ShellCommandResult executeRemoteCommand(String shellPath, String shellCommand,
                                                   ShellCommandSettings shellCommandSettings) {
        return this.executeRemoteCommandExec(shellPath, shellCommand, shellCommandSettings);
    }

    // TODO allow for capturing set command when user simulated terminal (otherwise
    // not possible) - too error prone.
    public ShellCommandResult executeRemoteCommandExec(String shellPath, String shellCommand,
                                                       ShellCommandSettings shellCommandSettings) {
        String executionShellPath = "";
        String executionShellCommand = "";

        // CompiledShellCommand
        if (shellPath == null)
            shellPath = "";
        executionShellPath = shellPath.trim();
        if (executionShellPath.equalsIgnoreCase("")) {
            executionShellCommand = shellCommand;
        } else {
            executionShellCommand = "cd " + executionShellPath + " && " + shellCommand;
        }

        // Execution
        int rc = -1;
        String systemOutput = "";
        String errorOutput = "";
        try {

            JSch jsch = this.jschConnect();

            Session session = null;
            Session[] sessions = null;
            if (this.getJumphostConnectionName().trim().equalsIgnoreCase("")) {
                sessions = new Session[1];
                sessions[0] = session = this.sessionConnect(jsch, this.getHostName(), this.getPortNumber(),
                        this.getUserName(), this.getUserPassword());
            } else {
                String[] jumphostConnections = this.getJumphostConnectionName().split(",");
                sessions = new Session[jumphostConnections.length + 1];
                for (int i = 0; i <= jumphostConnections.length; i++) {
                    String jumphostConnection = "";
                    HostConnection hostConnection = null;
                    if (i < jumphostConnections.length) {
                        jumphostConnection = jumphostConnections[i];
                        String finalJumphostConnection = jumphostConnection;
                        Connection connection = connectionConfiguration
                                .get(new ConnectionKey(jumphostConnection, shellCommandSettings.getEnvironment()))
                                .orElseThrow(() -> new RuntimeException(String.format("Unable to find %s", new ConnectionKey(finalJumphostConnection, shellCommandSettings.getEnvironment()))));
                        hostConnection = connectionOperation.getHostConnection(connection);
                    } else {
                        hostConnection = this;
                    }

                    int assignedPort = -1;
                    if (i == 0) {
                        assignedPort = hostConnection.getPortNumber();
                    } else {
                        assignedPort = session.setPortForwardingL(0, hostConnection.getHostName(),
                                hostConnection.getPortNumber());
                    }
                    // System.out.println("portforwarding: " + "localhost:" + assignedPort + " -> "
                    // + dcHost.getHostName()
                    // + ":" + assignedPort);

                    if (i == 0) {
                        sessions[i] = session = this.sessionConnect(jsch, hostConnection.getHostName(), assignedPort,
                                hostConnection.getUserName(), hostConnection.getUserPassword());
                    } else {
                        sessions[i] = session = this.sessionJumpConnect(jsch, hostConnection.getHostName(),
                                assignedPort, hostConnection.getUserName(), hostConnection.getUserPassword());
                    }

                    // System.out.println("The session has been established to " +
                    // dcHost.getUserName() + "@" + dcHost.getHostName());

                }

            }

            Channel channel = session.openChannel("exec");

            if (this.getTerminalFlag().equalsIgnoreCase("y")) {
                ((ChannelExec) channel).setPty(true);
            } else {
                ((ChannelExec) channel).setPty(false);
            }

            ((ChannelExec) channel).setCommand(executionShellCommand);
            channel.setInputStream(null);
            // TODO get error output
            // ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();

            channel.connect();

            systemOutput = "";
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    systemOutput = systemOutput + new String(tmp, 0, i);
                    // No screen output
                    // System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0)
                        continue;
                    rc = channel.getExitStatus();
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }

            channel.disconnect();
            this.sessionDisconnect(sessions);
        } catch (IOException | JSchException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        if (rc == 0) {
            // TODO review shellcommandresult object
            // this.splitOutput(systemOutput);
            return new ShellCommandResult(rc, systemOutput, errorOutput, "");
        } else {
            return new ShellCommandResult(rc, systemOutput, errorOutput);
        }

    }

    @SuppressWarnings("unused")
    public ShellCommandResult executeRemoteCommandShell2(String shellPath, String shellCommand,
                                                         ShellCommandSettings shellCommandSettings) {
        String compiledShellCommand = "";
        String executionShellPath = "";
        String executionShellCommand = "";

        compiledShellCommand += "echo SHELL_RUN_CMD";
        compiledShellCommand += "\n ";
        compiledShellCommand += shellCommand;
        compiledShellCommand += "\n ";
        compiledShellCommand += "SHELL_RUN_CMD_RC=$?";
        compiledShellCommand += "\n ";
        compiledShellCommand += "echo SHELL_RUN_CMD_RC";
        compiledShellCommand += "\n ";
        compiledShellCommand += "echo $SHELL_RUN_CMD_RC";
        compiledShellCommand += "\n ";
        compiledShellCommand += "sx su - dsadm";
        compiledShellCommand += "\n ";
        compiledShellCommand += "sleep 1s ";

        if (shellCommandSettings.getSetRunVar()) {
            compiledShellCommand += " && ";
            compiledShellCommand += "echo SHELL_RUN_CMD_RUN_VAR";
            compiledShellCommand += " && ";
            compiledShellCommand += "set";
        }

        // CompiledShellCommand
        if (shellPath == null)
            shellPath = "";
        executionShellPath = shellPath.trim();
        if (executionShellPath.equalsIgnoreCase("")) {
            executionShellCommand = compiledShellCommand;
        } else {
            executionShellCommand = "cd " + executionShellPath + " && " + compiledShellCommand;
        }

        // Execution
        int rc = -1;
        String systemOutput = "";
        String errorOutput = "";
        try {

            JSch jsch = this.jschConnect();

            Session session = null;
            Session[] sessions = null;
            if (this.getJumphostConnectionName().trim().equalsIgnoreCase("")) {
                sessions = new Session[1];
                sessions[0] = session = this.sessionConnect(jsch, this.getHostName(), this.getPortNumber(),
                        this.getUserName(), this.getUserPassword());
            } else {
                String[] jumphostConnections = this.getJumphostConnectionName().split(",");
                sessions = new Session[jumphostConnections.length + 1];
                for (int i = 0; i <= jumphostConnections.length; i++) {
                    String jumphostConnection = "";
                    HostConnection hostConnection = null;
                    if (i < jumphostConnections.length) {
                        jumphostConnection = jumphostConnections[i];
                        Connection connection = connectionConfiguration
                                .get(new ConnectionKey(jumphostConnection, shellCommandSettings.getEnvironment()))
                                .get();
                        hostConnection = connectionOperation.getHostConnection(connection);
                    } else {
                        hostConnection = this;
                    }

                    int assignedPort = -1;
                    if (i == 0) {
                        assignedPort = hostConnection.getPortNumber();
                    } else {
                        assignedPort = session.setPortForwardingL(0, hostConnection.getHostName(),
                                hostConnection.getPortNumber());
                    }
                    // System.out.println("portforwarding: " + "localhost:" + assignedPort + " -> "
                    // + dcHost.getHostName()
                    // + ":" + assignedPort);

                    if (i == 0) {
                        sessions[i] = session = this.sessionConnect(jsch, hostConnection.getHostName(), assignedPort,
                                hostConnection.getUserName(), hostConnection.getUserPassword());
                    } else {
                        sessions[i] = session = this.sessionJumpConnect(jsch, hostConnection.getHostName(),
                                assignedPort, hostConnection.getUserName(), hostConnection.getUserPassword());
                    }

                    // System.out.println("The session has been established to " +
                    // dcHost.getUserName() + "@" + dcHost.getHostName());

                }

            }

            Channel channel = session.openChannel("shell");

            // Enable agent-forwarding.
            // ((ChannelShell)channel).setAgentForwarding(true);

            // channel.setInputStream(this.convertToInputStream(executionShellCommand + "\n
            // exit"));
            channel.setInputStream(System.in);
            InputStream in = channel.getInputStream();

            // Choose the pty-type "vt102".
            ((ChannelShell) channel).setPtyType("vt102");

            channel.connect();

            systemOutput = "";
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    systemOutput = systemOutput + new String(tmp, 0, i);
                    // No screen output
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0)
                        continue;
                    rc = channel.getExitStatus();
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }

            channel.disconnect();
            this.sessionDisconnect(sessions);
        } catch (IOException | JSchException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        if (rc == 0) {
            this.splitOutput(systemOutput);
            return new ShellCommandResult(Integer.parseInt(this.getOutputReturnCode()), this.getOutputSystemOutput(),
                    errorOutput, this.getOutputRuntimeVariablesOutput());
        } else {
            return new ShellCommandResult(rc, systemOutput, errorOutput);
        }

    }

    private void splitOutput(String systemOutput) {
        BufferedReader bufReader = new BufferedReader(new StringReader(systemOutput));
        String line = null;
        String tempCommandOutput = "";
        boolean boolCommandOutput = false;
        String tempCommandReturnCode = "-1";
        boolean boolCommandReturnCode = false;
        String tempCommandRuntimeVariables = "";
        boolean boolCommandRuntimeVariables = false;

        try {
            while ((line = bufReader.readLine()) != null) {
                // Text
                if (!this.inList(this.getSystemOutputKeywordList(), line.trim())) {
                    if (boolCommandOutput) {
                        if (!tempCommandOutput.equalsIgnoreCase(""))
                            tempCommandOutput += "\n";
                        tempCommandOutput += line;
                    }
                    if (boolCommandReturnCode) {
                        if (!tempCommandReturnCode.equalsIgnoreCase(""))
                            tempCommandReturnCode += "\n";
                        tempCommandReturnCode += line;
                    }
                    if (boolCommandRuntimeVariables) {
                        if (!tempCommandRuntimeVariables.equalsIgnoreCase(""))
                            tempCommandRuntimeVariables += "\n";
                        tempCommandRuntimeVariables += line;
                    }
                }

                // Keywords
                if (line.trim().equalsIgnoreCase("SHELL_RUN_CMD")) {
                    boolCommandOutput = true;
                    boolCommandReturnCode = false;
                    boolCommandRuntimeVariables = false;
                }
                if (line.trim().equalsIgnoreCase("SHELL_RUN_CMD_RC")) {
                    boolCommandOutput = false;
                    boolCommandReturnCode = true;
                    boolCommandRuntimeVariables = false;
                }
                if (line.trim().equalsIgnoreCase("SHELL_RUN_CMD_RUN_VAR")) {
                    boolCommandOutput = false;
                    boolCommandReturnCode = false;
                    boolCommandRuntimeVariables = true;
                }

                this.setOutputSystemOutput(tempCommandOutput);
                this.setOutputReturnCode(tempCommandReturnCode);
                this.setOutputRuntimeVariablesOutput(tempCommandRuntimeVariables);
            }
            bufReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean inList(List<String> list, String checkItem) {
        boolean tempResult = false;

        for (String curVal : list) {
            if (curVal.equalsIgnoreCase(checkItem)) {
                tempResult = true;
            }
        }

        return tempResult;
    }

    // Session management
    private JSch jschConnect() throws JSchException {
        JSch jsch = new JSch();
        jsch.removeAllIdentity();

        // Defines number of tries made in background for validating credentials
        JSch.setConfig("MaxAuthTries", "1");

        return jsch;
    }

    private Session sessionConnect(JSch jsch, String hostName, int portNumber, String userName, String userPassword)
            throws JSchException {
        Session session = jsch.getSession(userName, hostName, portNumber);
        session.setConfig("StrictHostKeyChecking", "no");
        UserInfo ui = new LinuxHostUserInfo(userPassword);
        session.setUserInfo(ui);
        session.connect();
        return session;
    }

    private Session sessionJumpConnect(JSch jsch, String hostName, int portNumber, String userName, String userPassword)
            throws JSchException {
        Session session = jsch.getSession(userName, "127.0.0.1", portNumber);
        session.setConfig("StrictHostKeyChecking", "no");
        UserInfo ui = new LinuxHostUserInfo(userPassword);
        session.setUserInfo(ui);
        session.setHostKeyAlias(hostName);
        session.connect();
        return session;
    }

    @SuppressWarnings("unused")
    private void sessionDisconnect(Session session) {
        session.disconnect();
    }

    private void sessionDisconnect(Session[] sessions) {
        for (int i = sessions.length - 1; i >= 0; i--) {
            sessions[i].disconnect();
        }
    }

    // Getters and Setters
    public String getHostName() {
        return hostName;
    }

    public String getTerminalFlag() {
        return terminalFlag;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOutputSystemOutput() {
        return outputSystemOutput;
    }

    public void setOutputSystemOutput(String outputSystemOutput) {
        this.outputSystemOutput = outputSystemOutput;
    }

    public String getOutputReturnCode() {
        return outputReturnCode;
    }

    public void setOutputReturnCode(String outputReturnCode) {
        this.outputReturnCode = outputReturnCode;
    }

    public String getOutputRuntimeVariablesOutput() {
        return outputRuntimeVariablesOutput;
    }

    public void setOutputRuntimeVariablesOutput(String outputRuntimeVariablesOutput) {
        this.outputRuntimeVariablesOutput = outputRuntimeVariablesOutput;
    }

    public List<String> getSystemOutputKeywordList() {
        return systemOutputKeywordList;
    }

    public String getJumphostConnectionName() {
        return jumphostConnectionName;
    }

    public String getAllowLocalhostExecution() {
        return allowLocalhostExecution;
    }

}
