package io.metadew.iesi.connection.operation;

import com.jcraft.jsch.*;
import io.metadew.iesi.common.text.ParsingTools;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.LinuxHostUserInfo;
import io.metadew.iesi.connection.operation.filetransfer.FileToTransfer;
import io.metadew.iesi.connection.operation.filetransfer.FileTransferResult;
import io.metadew.iesi.connection.operation.filetransfer.FileTransfered;
import io.metadew.iesi.metadata.definition.connection.Connection;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Log4j2
@Service
public class FileTransferService {

    private final ConnectionOperation connectionOperation;

    public FileTransferService(ConnectionOperation connectionOperation) {
        this.connectionOperation = connectionOperation;
    }

    // File Transfer
    public FileTransferResult transferLocalToRemote(String sourceFilePath, String sourceFileName, String targetFilePath,
                                                    String targetFileName,
                                                    Connection targetConnection) {

        List<FileTransfered> fileTransferedList = new ArrayList<>();
        HostConnection targetConnectionConnection = connectionOperation.getHostConnection(targetConnection);
        log.trace("fho.transfer.target.connection=" + targetConnection.getMetadataKey().getName());

        try {
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
                    if (!file.isDirectory()) {
                        c.put(file.getName(), file.getName());
                        FileTransfered fileTransfered = new FileTransfered(sourceFilePath, file.getName(),
                                targetFilePath, file.getName());
                        fileTransferedList.add(fileTransfered);
                    }
                }
            } else if (ParsingTools.isRegexFunction(sourceFileName)) {
                final String fileFilter = ParsingTools.getRegexFunctionValue(sourceFileName);
                final File[] files = folder.listFiles((dir, name) -> name.matches(fileFilter));

                for (final File file : files) {
                    if (!file.isDirectory()) {
                        c.put(file.getName(), file.getName());
                        FileTransfered fileTransfered = new FileTransfered(sourceFilePath, file.getName(),
                                targetFilePath, file.getName());
                        fileTransferedList.add(fileTransfered);
                    }
                }
            } else {
                final String fileFilter = sourceFileName;
                final File[] files = folder.listFiles((dir, name) -> name.contentEquals(fileFilter));
                for (final File file : files) {
                    if (!file.isDirectory()) {
                        c.put(file.getName(), targetFileName);
                        FileTransfered fileTransfered = new FileTransfered(sourceFilePath, file.getName(),
                                targetFilePath, targetFileName);
                        fileTransferedList.add(fileTransfered);
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

    public FileTransferResult transferRemoteToLocal(String sourceFilePath, String sourceFileName,
                                                    Connection sourceConnection, String targetFilePath,
                                                    String targetFileName) {

        List<FileTransfered> fileTransferedList = new ArrayList<>();
        HostConnection sourceConnectionConnection = connectionOperation.getHostConnection(sourceConnection);
        log.trace("fho.transfer.source.connection=" + sourceConnection.getMetadataKey().getName());

        try {
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

            List<FileToTransfer> fileToTransferList = new ArrayList<>();
            String fileFilter;

            Vector vv;

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

                        if (!fileToTransfer.getAttributes().substring(0, 1).equalsIgnoreCase("d")) {
                            c.get(fileToTransfer.getFileName(), fileToTransfer.getFileName());
                            FileTransfered fileTransfered = new FileTransfered(sourceFilePath,
                                    fileToTransfer.getFileName(), targetFilePath, fileToTransfer.getFileName());
                            fileTransferedList.add(fileTransfered);
                        }
                    }
                }

            } else if (ParsingTools.isRegexFunction(sourceFileName)) {
                fileFilter = ParsingTools.getRegexFunctionValue(sourceFileName);

                vv = c.ls(sourceFilePath);
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {

                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            String fileMatch = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename();
                            if (fileMatch.matches(fileFilter)) {
                                fileToTransferList.add(
                                        new FileToTransfer(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getLongname(),
                                                ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename(),
                                                ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString()));
                            }
                        }
                    }

                    // Loop files
                    for (FileToTransfer fileToTransfer : fileToTransferList) {
                        if (!fileToTransfer.getAttributes().substring(0, 1).equalsIgnoreCase("d")) {
                            c.get(fileToTransfer.getFileName(), fileToTransfer.getFileName());
                            FileTransfered fileTransfered = new FileTransfered(sourceFilePath,
                                    fileToTransfer.getFileName(), targetFilePath, fileToTransfer.getFileName());
                            fileTransferedList.add(fileTransfered);
                        }
                    }
                }

            } else {
                fileFilter = sourceFileName;

                vv = c.ls(sourceFileName);
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {

                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            String fileMatch = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename();
                            if (fileMatch.equalsIgnoreCase(fileFilter)) {
                                fileToTransferList.add(
                                        new FileToTransfer(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getLongname(),
                                                ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename(),
                                                ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString()));
                            }
                        }
                    }

                    // Loop files
                    for (FileToTransfer fileToTransfer : fileToTransferList) {
                        if (!fileToTransfer.getAttributes().substring(0, 1).equalsIgnoreCase("d")) {
                            c.get(fileToTransfer.getFileName(), targetFileName);
                            FileTransfered fileTransfered = new FileTransfered(sourceFilePath,
                                    fileToTransfer.getFileName(), targetFilePath, targetFileName);
                            fileTransferedList.add(fileTransfered);
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

    public FileTransferResult transferLocalToLocal(String sourceFilePath,
                                                   String sourceFileName,
                                                   String targetFilePath,
                                                   String targetFileName) {


        FileTransferResult fileTransferResult = null;
//        if (sourceConnectionConnection.getType().equalsIgnoreCase("windows")
//                && targetConnectionConnection.getType().equalsIgnoreCase("windows")) {
//            fileTransferResult = this.transferLocalToLocalWindows(sourceFilePath, sourceFileName,
//                    sourceConnectionConnection, targetFilePath, targetFileName, targetConnectionConnection);
//        } else if (sourceConnectionConnection.getType().equalsIgnoreCase("linux")
//                && targetConnectionConnection.getType().equalsIgnoreCase("linux")) {
//            fileTransferResult = this.transferLocalToRemote(sourceFilePath, sourceFileName,
//                    sourceConnection, targetFilePath, targetFileName, targetConnection);
//        } else {
//            throw new RuntimeException("Incorrect configuration");
//        }

        return fileTransferResult;
    }

    private FileTransferResult transferLocalToLocalWindows(String sourceFilePath, String sourceFileName,
                                                           HostConnection sourceConnectionConnection, String targetFilePath, String targetFileName,
                                                           HostConnection targetConnectionConnection) {

        List<FileTransfered> fileTransferedList = new ArrayList<>();
        final File folder = new File(sourceFilePath);

        if (sourceFileName.equalsIgnoreCase("*")) {
            for (final File file : folder.listFiles()) {
                if (!file.isDirectory()) {
                    String command = "copy /Y" + sourceFilePath + File.separator + file.getName() + " "
                            + targetFilePath + File.separator + file.getName();
                    targetConnectionConnection.executeLocalCommand("", command);
                    fileTransferedList.add(
                            new FileTransfered(sourceFilePath, file.getName(), targetFilePath, file.getName()));
                }
            }
        } else if (sourceFileName.contains("�r.")) {
            final String file_filter = ".+\\.vsd";
            final File[] files = folder.listFiles((dir, name) -> name.matches(file_filter));
            // .+\\.vsd

            for (final File file : files) {
                if (!file.isDirectory()) {
                    String command = "copy /Y" + sourceFilePath + File.separator + file.getName() + " "
                            + targetFilePath + File.separator + file.getName();
                    targetConnectionConnection.executeLocalCommand("", command);
                    fileTransferedList.add(
                            new FileTransfered(sourceFilePath, file.getName(), targetFilePath, file.getName()));
                }
            }
        } else {
            final String file_filter = sourceFileName;
            final File[] files = folder.listFiles((dir, name) -> name.contentEquals(file_filter));
            for (final File file : files) {
                if (!file.isDirectory()) {
                    String command = "copy /Y" + sourceFilePath + File.separator + file.getName() + " "
                            + targetFilePath + File.separator + targetFileName;
                    targetConnectionConnection.executeLocalCommand("", command);
                    fileTransferedList.add(
                            new FileTransfered(sourceFilePath, file.getName(), targetFilePath, targetFileName));
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