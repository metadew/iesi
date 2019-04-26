package io.metadew.iesi.connection;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import io.metadew.iesi.connection.host.LinuxHostUserInfo;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.Connection;

/**
 * Connection object for hosts. This is extended depending on the host type.
 * 
 * @author peter.billen
 *
 */
public class HostConnection {

	private String type = "";
	private String hostName = "";
	private int portNumber;
	private String userName = "";
	private String userPassword = null;
	private String tempPath = "";
	private String terminalFlag = "Y";
	private String jumphostConnectionName = "";
	private String outputSystemOutput = "";
	private String outputReturnCode = "";
	private String outputRuntimeVariablesOutput = "";
	private ArrayList<String> systemOutputKeywordList = null;

	// Session management
	// private Session[] sessions;

	public HostConnection() {
		super();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HostConnection(String type, String hostName, int portNumber, String userName, String userPassword, String tempPath,
			String terminalFlag, String jumphostConnectionName) {
		super();
		this.setType(type);
		this.setHostName(hostName);
		this.setPortNumber(portNumber);
		this.setUserName(userName);
		this.setUserPassword(userPassword);
		this.setTempPath(tempPath);
		this.setTerminalFlag(terminalFlag);
		this.setJumphostConnectionName(jumphostConnectionName);
		this.setSystemOutputKeywordList(new ArrayList());
		this.getSystemOutputKeywordList().add("SHELL_RUN_CMD");
		this.getSystemOutputKeywordList().add("SHELL_RUN_CMD_RC");
		this.getSystemOutputKeywordList().add("SHELL_RUN_CMD_RUN_VAR");
	}

	// Constructor for localhost override
	public HostConnection(String type) {
		super();
		this.setType(type);
	}

	// Methods
	public ShellCommandResult executeLocalCommand(String shellPath, String shellCommand,
			ShellCommandSettings shellCommandSettings) {
		String executionShellPath = "";
		String executionShellCommand = "";

		executionShellPath = shellPath.trim();
		if (this.getType().equalsIgnoreCase("windows")) {
			// For Windows Commands, "cmd /c" needs to be put in front of the
			// command
			if (executionShellPath.equalsIgnoreCase("")) {
				executionShellCommand = "cmd /c " + "\"" + shellCommand + "\"";
			} else {
				executionShellCommand = "cmd /c " + "\"cd " + executionShellPath + " & " + shellCommand + "\"";
			}
		} else {
			if (executionShellPath.equalsIgnoreCase("")) {
				executionShellCommand = shellCommand;
			} else {
				executionShellCommand = "cd " + executionShellPath + " && " + shellCommand;
			}
		}

		//
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

		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return new ShellCommandResult(rc, systemOutput, errorOutput);
	}

	public ShellCommandResult executeRemoteCommand(String shellPath, String shellCommand,
			ShellCommandSettings shellCommandSettings) {
		//return this.executeRemoteCommandShell2(shellPath, shellCommand, dcCommandSettings);
		return this.executeRemoteCommandExec2(shellPath, shellCommand,
		 shellCommandSettings);
	}

	public ShellCommandResult executeRemoteCommandExec(String shellPath, String shellCommand,
			ShellCommandSettings shellCommandSettings) {
		String compiledShellCommand = "";
		String executionShellPath = "";
		String executionShellCommand = "";

		compiledShellCommand += "echo SHELL_RUN_CMD";
		compiledShellCommand += " && ";
		compiledShellCommand += shellCommand;
		compiledShellCommand += " && ";
		compiledShellCommand += "SHELL_RUN_CMD_RC=$?";
		compiledShellCommand += " && ";
		compiledShellCommand += "echo SHELL_RUN_CMD_RC";
		compiledShellCommand += " && ";
		compiledShellCommand += "echo $SHELL_RUN_CMD_RC";

		if (shellCommandSettings.getSetRunVar().trim().equalsIgnoreCase("y")) {
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

			JSch jsch = new JSch();
			jsch.removeAllIdentity();

			// Defines number of tries made in background for validating credentials
			JSch.setConfig("MaxAuthTries", "1");
			Session session = jsch.getSession(this.getUserName(), this.getHostName(), this.getPortNumber());
			session.setConfig("StrictHostKeyChecking", "no");
			UserInfo ui = new LinuxHostUserInfo(this.getUserPassword());
			session.setUserInfo(ui);
			session.connect();
			Channel channel = session.openChannel("exec");

			if (this.getTerminalFlag().equalsIgnoreCase("y")) {
				((ChannelExec) channel).setPty(true);
			} else {
				((ChannelExec) channel).setPty(false);
			}

			((ChannelExec) channel).setCommand(executionShellCommand);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
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
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}

			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
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

	public ShellCommandResult executeRemoteCommandExec2(String shellPath, String shellCommand,
			ShellCommandSettings shellCommandSettings) {
		String compiledShellCommand = "";
		String executionShellPath = "";
		String executionShellCommand = "";

		compiledShellCommand += "echo SHELL_RUN_CMD";
		compiledShellCommand += " && ";
		compiledShellCommand += shellCommand;
		compiledShellCommand += " && ";
		compiledShellCommand += "SHELL_RUN_CMD_RC=$?";
		compiledShellCommand += " && ";
		compiledShellCommand += "echo SHELL_RUN_CMD_RC";
		compiledShellCommand += " && ";
		compiledShellCommand += "echo $SHELL_RUN_CMD_RC";

		if (shellCommandSettings.getSetRunVar().trim().equalsIgnoreCase("y")) {
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
						ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(shellCommandSettings.getFrameworkExecution());
						Connection connection = connectionConfiguration.getConnection(jumphostConnection,
								shellCommandSettings.getEnvironment()).get();
						ConnectionOperation connectionOperation = new ConnectionOperation(shellCommandSettings.getFrameworkExecution());
						hostConnection = connectionOperation.getHostConnection(connection);
					} else {
						hostConnection = this;
					}

					int assignedPort = -1;
					if (i == 0) {
						assignedPort = hostConnection.getPortNumber();
					} else {
						assignedPort = session.setPortForwardingL(0, hostConnection.getHostName(), hostConnection.getPortNumber());
					}
					// System.out.println("portforwarding: " + "localhost:" + assignedPort + " -> "
					// + dcHost.getHostName()
					// + ":" + assignedPort);

					if (i == 0) {
						sessions[i] = session = this.sessionConnect(jsch, hostConnection.getHostName(), assignedPort,
								hostConnection.getUserName(), hostConnection.getUserPassword());
					} else {
						sessions[i] = session = this.sessionJumpConnect(jsch, hostConnection.getHostName(), assignedPort,
								hostConnection.getUserName(), hostConnection.getUserPassword());
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
			((ChannelExec) channel).setErrStream(System.err);
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
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}

			channel.disconnect();
			this.sessionDisconnect(sessions);
		} catch (Exception e) {
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

	public ShellCommandResult executeRemoteCommandShell(String shellPath, String shellCommand,
			ShellCommandSettings shellCommandSettings) {

		String executionShellPath = "";
		String executionShellCommand = "";

		if (shellPath == null)
			shellPath = "";
		executionShellPath = shellPath.trim();
		if (executionShellPath.equalsIgnoreCase("")) {
			executionShellCommand = shellCommand;
		} else {
			executionShellCommand = "cd " + executionShellPath + " && " + shellCommand;
		}

		//
		int rc = -1;
		String systemOutput = "";
		String errorOutput = "";
		try {

			JSch jsch = new JSch();
			Session session = jsch.getSession(this.getUserName(), this.getHostName(), this.getPortNumber());
			session.setConfig("StrictHostKeyChecking", "no");
			UserInfo ui = new LinuxHostUserInfo(this.getUserPassword());
			session.setUserInfo(ui);
			session.connect();
			Channel channel = session.openChannel("shell");

			channel.setInputStream(this.convertToInputStream(executionShellCommand + "\n exit"));

			InputStream in = channel.getInputStream();
			// OutputStream out = channel.getOutputStream();

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
					System.out.println("rc1 " + rc);
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}

			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return new ShellCommandResult(rc, systemOutput, errorOutput);

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
		

		if (shellCommandSettings.getSetRunVar().trim().equalsIgnoreCase("y")) {
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
						ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(shellCommandSettings.getFrameworkExecution());
						Connection connection = connectionConfiguration.getConnection(jumphostConnection,
								shellCommandSettings.getEnvironment()).get();
						ConnectionOperation connectionOperation = new ConnectionOperation(shellCommandSettings.getFrameworkExecution());
						hostConnection = connectionOperation.getHostConnection(connection);
					} else {
						hostConnection = this;
					}

					int assignedPort = -1;
					if (i == 0) {
						assignedPort = hostConnection.getPortNumber();
					} else {
						assignedPort = session.setPortForwardingL(0, hostConnection.getHostName(), hostConnection.getPortNumber());
					}
					// System.out.println("portforwarding: " + "localhost:" + assignedPort + " -> "
					// + dcHost.getHostName()
					// + ":" + assignedPort);

					if (i == 0) {
						sessions[i] = session = this.sessionConnect(jsch, hostConnection.getHostName(), assignedPort,
								hostConnection.getUserName(), hostConnection.getUserPassword());
					} else {
						sessions[i] = session = this.sessionJumpConnect(jsch, hostConnection.getHostName(), assignedPort,
								hostConnection.getUserName(), hostConnection.getUserPassword());
					}

					// System.out.println("The session has been established to " +
					// dcHost.getUserName() + "@" + dcHost.getHostName());

				}

			}

			Channel channel = session.openChannel("shell");

			// Enable agent-forwarding.
			// ((ChannelShell)channel).setAgentForwarding(true);

			//channel.setInputStream(this.convertToInputStream(executionShellCommand + "\n exit"));
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
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}

			channel.disconnect();
			this.sessionDisconnect(sessions);
		} catch (Exception e) {
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

	public InputStream convertToInputStream(String input) {
		String output = "";
		try {
			Reader inputString = new StringReader(input);
			BufferedReader bufferedReader = new BufferedReader(inputString);
			String readLine = "";
			while ((readLine = bufferedReader.readLine()) != null) {
				output += readLine;
				output += "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean inList(ArrayList<String> list, String checkItem) {
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

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getTerminalFlag() {
		return terminalFlag;
	}

	public void setTerminalFlag(String terminalFlag) {
		this.terminalFlag = terminalFlag;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getTempPath() {
		return tempPath;
	}

	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
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

	public ArrayList<String> getSystemOutputKeywordList() {
		return systemOutputKeywordList;
	}

	public void setSystemOutputKeywordList(ArrayList<String> systemOutputKeywordList) {
		this.systemOutputKeywordList = systemOutputKeywordList;
	}

	public String getJumphostConnectionName() {
		return jumphostConnectionName;
	}

	public void setJumphostConnectionName(String jumphostConnectionName) {
		this.jumphostConnectionName = jumphostConnectionName;
	}

}
