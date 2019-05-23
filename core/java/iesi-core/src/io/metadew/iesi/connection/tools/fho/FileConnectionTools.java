package io.metadew.iesi.connection.tools.fho;

import com.jcraft.jsch.*;
import io.metadew.iesi.common.text.ParsingTools;
import io.metadew.iesi.connection.FileConnection;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.LinuxHostUserInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public final class FileConnectionTools {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<FileConnection> getFileConnections(HostConnection hostConnection, String folderPath,
                                                          String fileExpression, boolean onlyFolders) {
        List<FileConnection> connectionsFound = new ArrayList();
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

            c.cd(folderPath);

            String fileFilter;

            Vector vv = null;

            if (fileExpression.equalsIgnoreCase("*") || fileExpression.equalsIgnoreCase("")) {
                // Check all files
                vv = c.ls(folderPath);
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {
                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            FileConnection connectionFound = createFileConnection(folderPath, obj, onlyFolders);
                            if (connectionFound != null)
                                connectionsFound.add(connectionFound);
                        }
                    }
                }

            } else if (ParsingTools.isRegexFunction(fileExpression)) {
                // Check regex expression files
                fileFilter = fileExpression;
                vv = c.ls(folderPath);
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {

                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            String file_match = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename();
                            if (file_match.matches(fileFilter)) {
                                FileConnection connectionFound = createFileConnection(folderPath, obj, onlyFolders);
                                if (connectionFound != null)
                                    connectionsFound.add(connectionFound);
                            }
                        }
                    }
                }

            } else {
                // Check exact file name
                fileFilter = fileExpression;
                vv = c.ls(folderPath);
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {
                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            if (((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename()
                                    .equalsIgnoreCase(fileExpression)) {
                                FileConnection connectionFound = createFileConnection(folderPath, obj, onlyFolders);
                                if (connectionFound != null)
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

    private static FileConnection createFileConnection(String folderPath, Object obj, boolean onlyFolders) {
        FileConnection fileConnection = null;
        String attributes = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString();

        if (!((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename().equalsIgnoreCase(".")
                && !((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename().equalsIgnoreCase("..")) {
            fileConnection = new FileConnection();

            fileConnection.setLongName(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getLongname());
            fileConnection.setFileName(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename());
            fileConnection.setAttributes(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs().toString());
            fileConnection.setFilePath(folderPath + "/" + fileConnection.getFileName());

            if (attributes.substring(0, 1).equalsIgnoreCase("d")) {
                fileConnection.setDirectory(true);
            } else {
                fileConnection.setDirectory(false);
            }
        } else {
            return null;
        }

        if (onlyFolders && !fileConnection.isDirectory()) {
            return null;
        } else {
            return fileConnection;
        }

    }

}