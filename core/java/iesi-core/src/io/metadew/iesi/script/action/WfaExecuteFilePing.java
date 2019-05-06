package io.metadew.iesi.script.action;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.common.text.ParsingTools;
import io.metadew.iesi.connection.FileConnection;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.LinuxHostUserInfo;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

public class WfaExecuteFilePing {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation filePath;
	private ActionParameterOperation fileName;
	private ActionParameterOperation expectedResult;
	private ActionParameterOperation setRuntimeVariables;
	private ActionParameterOperation connectionName;
	private int waitInterval;
	private int timeoutInterval;
	private long startTime;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public WfaExecuteFilePing() {
		
	}
	
	public WfaExecuteFilePing(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
	}
	
	public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setActionExecution(actionExecution);
		this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
	}

	public void prepare() {
		// Set Parameters
		this.setFilePath(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
				this.getActionExecution().getAction().getType(), "filePath"));
		this.setFileName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
				this.getActionExecution().getAction().getType(), "fileName"));
		this.setExpectedResult(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
				this.getActionExecution().getAction().getType(), "hasResult"));
		this.setSetRuntimeVariables(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "setRuntimeVariables"));
		this.setConnectionName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
				this.getActionExecution().getAction().getType(), "connection"));
		this.setWaitInterval(1000);
		this.setTimeoutInterval(-1);

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("filepath")) {
				this.getFilePath().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("filename")) {
				this.getFileName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("hasresult")) {
				this.getExpectedResult().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("setruntimevariables")) {
				this.getSetRuntimeVariables().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("connection")) {
				this.getConnectionName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("wait")) {
				this.setWaitInterval(Integer.parseInt(actionParameter.getValue()));
			} else if (actionParameter.getName().equalsIgnoreCase("timeout")) {
				this.setTimeoutInterval(Integer.parseInt(actionParameter.getValue()));
			}
		}
	
		//Create parameter list
		this.getActionParameterOperationMap().put("filePath", this.getFilePath());
		this.getActionParameterOperationMap().put("fileName", this.getFileName());
		this.getActionParameterOperationMap().put("hasResult", this.getExpectedResult());
		this.getActionParameterOperationMap().put("setRuntimeVariables", this.getSetRuntimeVariables());
		this.getActionParameterOperationMap().put("connection", this.getConnectionName());
	}
	
	public void execute() {
		try {
			// Get Connection
			ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution());
			Connection connection = connectionConfiguration.getConnection(this.getConnectionName().getValue(),
					this.getExecutionControl().getEnvName()).get();
			ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
			HostConnection dcConnection = connectionOperation.getHostConnection(connection);

			// Check if connection is localhost
			boolean connectionIsLocalHost = connectionOperation.isOnLocalConnection(dcConnection);

			// Run the action
			int i = 1;
			long wait = this.getWaitInterval() * 1000;
			if (wait <= 0)
				wait = 1000;
			boolean checkTimeout = false;
			long timeout = this.getTimeoutInterval() * 1000;
			long timeoutCounter = 0;
			if (timeout > 0)
				checkTimeout = true;

			boolean done = false;
			this.setStartTime(System.currentTimeMillis());
			while (i == 1) {
				if (this.doneWaiting(connection, connectionIsLocalHost)) {
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

				this.getActionExecution().getActionControl().logOutput("out","result found");
				this.getActionExecution().getActionControl().logOutput("time",Long.toString(elapsedTime));
			} else {
				this.getActionExecution().getActionControl().increaseErrorCount();

				this.getActionExecution().getActionControl().logOutput("out","time-out");
				this.getActionExecution().getActionControl().logOutput("time",Long.toString(elapsedTime));
			}
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			this.getActionExecution().getActionControl().increaseErrorCount();

			this.getActionExecution().getActionControl().logOutput("exception",e.getMessage());
			this.getActionExecution().getActionControl().logOutput("stacktrace",StackTrace.toString());

		}

	}

	private boolean doneWaiting(Connection connection, boolean connectionIsLocalHost) {
		try {
			List<FileConnection> connectionsFound = null;
			if (connectionIsLocalHost) {
				connectionsFound = this.checkLocalFolder();
			} else {
				connectionsFound = this.checkRemoteFolder(connection);
			}

			if (connectionsFound.size() > 0) {
				if (this.getExpectedResult().getValue().equalsIgnoreCase("y")) {
					// this.setRuntimeVariable(crs);
					return true;
				} else {
					return false;
				}
			} else {
				if (this.getExpectedResult().getValue().equalsIgnoreCase("n")) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			this.getActionExecution().getActionControl().increaseErrorCount();
			
			this.getActionExecution().getActionControl().logOutput("exception",e.getMessage());
			this.getActionExecution().getActionControl().logOutput("stacktrace",StackTrace.toString());

			throw new RuntimeException(e.getMessage());
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked"})
	private List<FileConnection> checkLocalFolder() {
		List<FileConnection> connectionsFound = new ArrayList();
		final File folder = new File(this.getFilePath().getValue());
		if (this.getFileName().getValue().equalsIgnoreCase("*") || this.getFileName().getValue().equalsIgnoreCase("")) {
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
		} else if (ParsingTools.isRegexFunction(this.getFileName().getValue())) {
			// Check regex expression files
			final String fileFilter = this.getFileName().getValue();
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
			final String fileFilter = this.getFileName().getValue();
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<FileConnection> checkRemoteFolder(Connection connection) {
		List<FileConnection> connectionsFound = new ArrayList();
		ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
		HostConnection hostConnection = connectionOperation.getHostConnection(connection);
		this.getActionExecution().getActionControl().logOutput("conn.name",connection.getName());

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

			c.cd(this.getFilePath().getValue());

			String fileFilter;

			Vector vv = null;

			if (this.getFileName().getValue().equalsIgnoreCase("*") || this.getFileName().getValue().equalsIgnoreCase("")) {
				// Check all files
				vv = c.ls(this.getFilePath().getValue());
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

			} else if (ParsingTools.isRegexFunction(this.getFileName().getValue())) {
				// Check regex expression files
				fileFilter = this.getFileName().getValue();
				vv = c.ls(this.getFilePath().getValue());
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
				// TODO check if not an issue on the filter
				fileFilter = this.getFileName().getValue();
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
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return connectionsFound;

	}
	
	@SuppressWarnings("unused")
	private void setRuntimeVariable(CachedRowSet crs) {
		if (this.getSetRuntimeVariables().getValue().equalsIgnoreCase("y")) {
			try {
				this.getExecutionControl().getExecutionRuntime().setRuntimeVariables(crs);
			} catch (Exception e) {
				this.getActionExecution().getActionControl().increaseWarningCount();
				this.getActionExecution().getActionControl().logWarning("set.runvar",e.getMessage());
			}
		}
	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public ExecutionControl getExecutionControl() {
		return executionControl;
	}

	public void setExecutionControl(ExecutionControl executionControl) {
		this.executionControl = executionControl;
	}

	public int getWaitInterval() {
		return waitInterval;
	}

	public void setWaitInterval(int waitInterval) {
		this.waitInterval = waitInterval;
	}

	public int getTimeoutInterval() {
		return timeoutInterval;
	}

	public void setTimeoutInterval(int timeoutInterval) {
		this.timeoutInterval = timeoutInterval;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public ActionExecution getActionExecution() {
		return actionExecution;
	}

	public void setActionExecution(ActionExecution actionExecution) {
		this.actionExecution = actionExecution;
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

	public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
		return actionParameterOperationMap;
	}

	public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
		this.actionParameterOperationMap = actionParameterOperationMap;
	}

	public ActionParameterOperation getSetRuntimeVariables() {
		return setRuntimeVariables;
	}

	public void setSetRuntimeVariables(ActionParameterOperation setRuntimeVariables) {
		this.setRuntimeVariables = setRuntimeVariables;
	}

}