package io.metadew.iesi.connection.operation;

import com.jcraft.jsch.*;
import io.metadew.iesi.common.text.ParsingTools;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.LinuxHostUserInfo;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.filetransfer.FileToTransfer;
import io.metadew.iesi.connection.operation.filetransfer.FileTransferResult;
import io.metadew.iesi.connection.operation.filetransfer.FileTransfered;
import io.metadew.iesi.metadata.definition.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FileTransferOperation {

    private static final Logger LOGGER = LogManager.getLogger();

    public FileTransferOperation() {
    }

    // File Transfer
    @SuppressWarnings({"rawtypes", "unchecked", "unused"})
    public FileTransferResult transferLocalToRemote(String sourceFilePath, String sourceFileName,
                                                    Connection sourceConnection, String targetFilePath, String targetFileName,
                                                    Connection targetConnection) throws Exception {

        List<FileTransfered> fileTransferedList = new ArrayList();
        ConnectionOperation connectionOperation = new ConnectionOperation();
        HostConnection sourceConnectionConnection = connectionOperation.getHostConnection(sourceConnection);
        LOGGER.trace("fho.transfer.source.connection=" + sourceConnection.getMetadataKey().getName());
        HostConnection targetConnectionConnection = connectionOperation.getHostConnection(targetConnection);
        LOGGER.trace("fho.transfer.target.connection=" + targetConnection.getMetadataKey().getName());

        try {
            String filepath = sourceFilePath + File.separator + sourceFileName;
            JSch jsch = new JSch();
            Session session = jsch.getSession(targetConnectionConnection.getUserName(),
                    targetConnectionConnection.getHostName(), targetConnectionConnection.getPortNumber());
            session.setConfig("StrictHostKeyChecking", "no");
            UserInfo ui = new LinuxHostUserInfo(targetConnectionConnection.getUserPassword());
            session.setUserInfo(ui);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp c = (ChannelSftp) channel;

            c.lcd(sourceFilePath);
            c.cd(targetFilePath);
            final File folder = new File(sourceFilePath);

            if (sourceFileName.equalsIgnoreCase("*")) {
                for (final File file : folder.listFiles()) {
                    if (file.isDirectory()) {
                        // Ignore
                    } else {
                        c.put(file.getName(), file.getName());
                        FileTransfered fileTransfered = new FileTransfered(sourceFilePath, file.getName(),
                                targetFilePath, file.getName());
                        //FrameworkLog.getInstance().log(fileTransfered, Level.TRACE);
                        fileTransferedList.add(fileTransfered);
                        filepath = sourceFilePath + File.separator + file.getName();
                    }
                }
            } else if (ParsingTools.isRegexFunction(sourceFileName)) {
                final String file_filter = ParsingTools.getRegexFunctionValue(sourceFileName);
                //final String file_filter = ".+\\.csv";
                final File[] files = folder.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(final File dir, final String name) {
                        return name.matches(file_filter);
                    }
                });
                // .+\\.vsd

                for (final File file : files) {
                    if (file.isDirectory()) {
                        // Ignore
                    } else {
                        c.put(file.getName(), file.getName());
                        FileTransfered fileTransfered = new FileTransfered(sourceFilePath, file.getName(),
                                targetFilePath, file.getName());
                        //FrameworkLog.getInstance().log(fileTransfered, Level.TRACE);
                        fileTransferedList.add(fileTransfered);
                        filepath = sourceFilePath + File.separator + file.getName();
                    }
                }
            } else {
                final String file_filter = sourceFileName;
                final File[] files = folder.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(final File dir, final String name) {
                        return name.contentEquals(file_filter);
                    }
                });
                for (

                        final File file : files) {
                    if (file.isDirectory()) {
                        // Ignore
                    } else {
                        c.put(file.getName(), targetFileName);
                        FileTransfered fileTransfered = new FileTransfered(sourceFilePath, file.getName(),
                                targetFilePath, targetFileName);
                        //FrameworkLog.getInstance().log(fileTransfered, Level.TRACE);
                        fileTransferedList.add(fileTransfered);
                        filepath = sourceFileName + File.separator + file.getName();
                    }
                }
            }

            c.disconnect();
            channel.disconnect();
            session.disconnect();

        } catch (SftpException | JSchException e) {
            throw new RuntimeException(e.getMessage());
        }

        return new FileTransferResult(0, fileTransferedList);
    }

    @SuppressWarnings({"rawtypes", "unchecked", "unused"})
    public FileTransferResult transferRemoteToLocal(String sourceFilePath, String sourceFileName,
                                                    Connection sourceConnection, String targetFilePath, String targetFileName,
                                                    Connection targetConnection) throws Exception {

        List<FileTransfered> fileTransferedList = new ArrayList();
        ConnectionOperation connectionOperation = new ConnectionOperation();
        HostConnection sourceConnectionConnection = connectionOperation.getHostConnection(sourceConnection);
        LOGGER.trace("fho.transfer.source.connection=" + sourceConnection.getMetadataKey().getName());
        HostConnection targetConnectionConnection = connectionOperation.getHostConnection(targetConnection);
        LOGGER.trace("fho.transfer.target.connection=" + targetConnection.getMetadataKey().getName());

        try {
            String filepath = targetFilePath + File.separator + targetFileName;

            JSch jsch = new JSch();
            Session session = jsch.getSession(sourceConnectionConnection.getUserName(),
                    sourceConnectionConnection.getHostName(), sourceConnectionConnection.getPortNumber());
            session.setConfig("StrictHostKeyChecking", "no");
            UserInfo ui = new LinuxHostUserInfo(sourceConnectionConnection.getUserPassword());
            session.setUserInfo(ui);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp c = (ChannelSftp) channel;
            c.lcd(targetFilePath);
            c.cd(sourceFilePath);

            List<FileToTransfer> fileToTransferList = new ArrayList();
            String file_filter;

            Vector vv = null;

            if (sourceFileName.equalsIgnoreCase("*")) {
                vv = c.ls(sourceFilePath);
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {

                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            fileToTransferList
                                    .add(new FileToTransfer(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getLongname(),
                                            ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename(),
                                            ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString()));
                        }
                    }
                    // Loop files
                    for (FileToTransfer fileToTransfer : fileToTransferList) {

                        if (fileToTransfer.getAttributes().substring(0, 1).equalsIgnoreCase("d")) {
                            // Ignore
                        } else {
                            c.get(fileToTransfer.getFileName(), fileToTransfer.getFileName());
                            FileTransfered fileTransfered = new FileTransfered(sourceFilePath,
                                    fileToTransfer.getFileName(), targetFilePath, fileToTransfer.getFileName());
                            //FrameworkLog.getInstance().log(fileTransfered, Level.TRACE);
                            fileTransferedList.add(fileTransfered);
                            filepath = sourceFilePath + "/" + fileToTransfer.getFileName();
                        }
                    }
                }

            } else if (ParsingTools.isRegexFunction(sourceFileName)) {
                file_filter = ParsingTools.getRegexFunctionValue(sourceFileName);
                //final String file_filter = ".+\\.csv";

                vv = c.ls(sourceFilePath);
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {

                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            String file_match = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename();
                            if (file_match.matches(file_filter)) {
                                fileToTransferList.add(
                                        new FileToTransfer(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getLongname(),
                                                ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename(),
                                                ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString()));
                            }
                        }
                    }

                    // Loop files
                    for (FileToTransfer fileToTransfer : fileToTransferList) {
                        if (fileToTransfer.getAttributes().substring(0, 1).equalsIgnoreCase("d")) {
                            // Ignore
                        } else {
                            c.get(fileToTransfer.getFileName(), fileToTransfer.getFileName());
                            FileTransfered fileTransfered = new FileTransfered(sourceFilePath,
                                    fileToTransfer.getFileName(), targetFilePath, fileToTransfer.getFileName());
                            //FrameworkLog.getInstance().log(fileTransfered, Level.TRACE);
                            fileTransferedList.add(fileTransfered);
                            filepath = sourceFilePath + "/" + fileToTransfer.getFileName();
                        }
                    }
                }

            } else {
                file_filter = sourceFileName;

                vv = c.ls(sourceFileName);
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {

                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            String file_match = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename();
                            if (file_match.equalsIgnoreCase(file_filter)) {
                                fileToTransferList.add(
                                        new FileToTransfer(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getLongname(),
                                                ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename(),
                                                ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString()));
                            }
                        }
                    }

                    // Loop files
                    for (FileToTransfer fileToTransfer : fileToTransferList) {
                        if (fileToTransfer.getAttributes().substring(0, 1).equalsIgnoreCase("d")) {
                            // Ignore
                        } else {
                            c.get(fileToTransfer.getFileName(), targetFileName);
                            FileTransfered fileTransfered = new FileTransfered(sourceFilePath,
                                    fileToTransfer.getFileName(), targetFilePath, targetFileName);
                            //FrameworkLog.getInstance().log(fileTransfered, Level.TRACE);
                            fileTransferedList.add(fileTransfered);
                            filepath = sourceFilePath + "/" + targetFileName;
                        }
                    }

                }
            }

            c.disconnect();
            channel.disconnect();
            session.disconnect();

        } catch (SftpException | JSchException e) {
            throw new RuntimeException(e.getMessage());
        }
        return new FileTransferResult(0, fileTransferedList);
    }

    public FileTransferResult transferLocalToLocal(String sourceFilePath, String sourceFileName,
                                                   Connection sourceConnection, String targetFilePath, String targetFileName,
                                                   Connection targetConnection) throws Exception {

        ConnectionOperation connectionOperation = new ConnectionOperation();
        HostConnection sourceConnectionConnection = connectionOperation.getHostConnection(sourceConnection);
        HostConnection targetConnectionConnection = connectionOperation.getHostConnection(targetConnection);

        FileTransferResult fileTransferResult = null;
        if (sourceConnectionConnection.getType().equalsIgnoreCase("windows")
                && targetConnectionConnection.getType().equalsIgnoreCase("windows")) {
            fileTransferResult = this.transferLocalToLocalWindows(sourceFilePath, sourceFileName,
                    sourceConnectionConnection, targetFilePath, targetFileName, targetConnectionConnection);
        } else if (sourceConnectionConnection.getType().equalsIgnoreCase("linux")
                && targetConnectionConnection.getType().equalsIgnoreCase("linux")) {
            fileTransferResult = this.transferLocalToRemote(sourceFilePath, sourceFileName,
                    sourceConnection, targetFilePath, targetFileName, targetConnection);
        } else {
            throw new RuntimeException("Incorrect configuration");
        }

        return fileTransferResult;
    }

    @SuppressWarnings({"rawtypes", "unchecked", "unused"})
    private FileTransferResult transferLocalToLocalWindows(String sourceFilePath, String sourceFileName,
                                                           HostConnection sourceConnectionConnection, String targetFilePath, String targetFileName,
                                                           HostConnection targetConnectionConnection) {

        List<FileTransfered> fileTransferedList = new ArrayList();
        ShellCommandSettings shellCommandSettings = new ShellCommandSettings();

        String filepath = sourceFilePath + File.separator + sourceFileName;
        final File folder = new File(sourceFilePath);

        if (sourceFileName.equalsIgnoreCase("*")) {
            for (final File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    // Ignore
                } else {
                    String command = "copy /Y" + sourceFilePath + File.separator + file.getName() + " "
                            + targetFilePath + File.separator + file.getName();
                    targetConnectionConnection.executeLocalCommand("", command);
                    fileTransferedList.add(
                            new FileTransfered(sourceFilePath, file.getName(), targetFilePath, file.getName()));
                    filepath = sourceFilePath + File.separator + file.getName();
                }
            }
        } else if (sourceFileName.contains("�r.")) {
            final String file_filter = ".+\\.vsd";
            final File[] files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String name) {
                    return name.matches(file_filter);
                }
            });
            // .+\\.vsd

            for (final File file : files) {
                if (file.isDirectory()) {
                    // Ignore
                } else {
                    String command = "copy /Y" + sourceFilePath + File.separator + file.getName() + " "
                            + targetFilePath + File.separator + file.getName();
                    targetConnectionConnection.executeLocalCommand("", command);
                    fileTransferedList.add(
                            new FileTransfered(sourceFilePath, file.getName(), targetFilePath, file.getName()));
                    filepath = sourceFilePath + File.separator + file.getName();
                }
            }
        } else {
            final String file_filter = sourceFileName;
            final File[] files = folder.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(final File dir, final String name) {
                    return name.contentEquals(file_filter);
                }
            });
            for (

                    final File file : files) {
                if (file.isDirectory()) {
                    // Ignore
                } else {
                    String command = "copy /Y" + sourceFilePath + File.separator + file.getName() + " "
                            + targetFilePath + File.separator + targetFileName;
                    targetConnectionConnection.executeLocalCommand("", command);
                    fileTransferedList.add(
                            new FileTransfered(sourceFilePath, file.getName(), targetFilePath, targetFileName));
                    filepath = sourceFileName + File.separator + file.getName();
                }
            }
        }

        return new FileTransferResult(0, fileTransferedList);
    }


    // Remote to Remote
    public FileTransferResult transferRemoteToRemote() {

        throw new RuntimeException("method not supported");
    }

}