package io.metadew.iesi.gcp.common.tools;

import io.metadew.iesi.gcp.connection.filesystem.FileConnection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class FolderTools {

    public static File[] getFilesInFolder(String folderName, String filterType, String filterExpression) {
        File[] files = null;

        if (filterType.equalsIgnoreCase("all")) {
            files = getAllFilesInFolder(folderName);
        } else if (filterType.equalsIgnoreCase("match")) {
            files = getFilesInFolderUsingMatch(folderName, filterExpression);
        } else if (filterType.equalsIgnoreCase("regex")) {
            files = getFilesInFolderUsingRegex(folderName, filterExpression);
        }

        return files;
    }

    public static File[] mergeFileArrays(File[] array1, File[] array2) {
        File[] result = ArrayUtils.addAll(array1, array2);
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<FileConnection> getFilesInFolder(String folderName, String filterExpression) {
        File[] files = new File[0];
        List<FileConnection> connectionsFound = new ArrayList();
        String filterType = "";

        // Define filterType
        if (filterExpression.equalsIgnoreCase("*") || filterExpression.equalsIgnoreCase("")) {
            filterType = "all";
        } else if (ParsingTools.isRegexFunction(filterExpression)) {
            filterType = "regex";
        } else {
            filterType = "match";
        }

        // Get files
        if (filterType.equalsIgnoreCase("all")) {
            files = getAllFilesInFolder(folderName);
        } else if (filterType.equalsIgnoreCase("match")) {
            files = getFilesInFolderUsingMatch(folderName, filterExpression);
        } else if (filterType.equalsIgnoreCase("regex")) {
            files = getFilesInFolderUsingRegex(folderName, filterExpression);
        }



        for (File file : files) {
            FileConnection connectionFound = new FileConnection();
            connectionFound.setFileName(file.getName());
            connectionFound.setFilePath(file.getAbsolutePath());
            connectionFound.setExtension(FileTools.getFileExtension(file));
            connectionFound.setDirectory(file.isDirectory());
            connectionsFound.add(connectionFound);
        }

        return connectionsFound;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<FileConnection> getConnectionsInFolder(String folderName, String filterType, String filterExpression,
                                                              List<FileConnection> fileConnectionList) {
        File[] files = null;

        if (filterType.equalsIgnoreCase("all")) {
            files = getAllFilesInFolder(folderName);
        } else if (filterType.equalsIgnoreCase("match")) {
            files = getFilesInFolderUsingMatch(folderName, filterExpression);
        } else if (filterType.equalsIgnoreCase("regex")) {
            files = getFilesInFolderUsingRegex(folderName, filterExpression);
        }

        // Fill list
        fileConnectionList = new ArrayList();
        for (final File file : files) {
            if (file.isDirectory()) {
                // Ignore
            } else {
                FileConnection connectionFound = new FileConnection();
                connectionFound.setLongName(file.getAbsolutePath());
                connectionFound.setFileName(file.getName());
                connectionFound.setFilePath(file.getPath());
                fileConnectionList.add(connectionFound);
            }
        }

        return fileConnectionList;
    }

    private static File[] getAllFilesInFolder(String folderName) {
        final File folder = new File(folderName);

        return folder.listFiles();
    }

    private static File[] getFilesInFolderUsingRegex(String folderName, String filterExpression) {
        final File folder = new File(folderName);

        final String fileFilter = filterExpression;
        final File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.matches(fileFilter);
            }
        });

        return files;
    }

    private static File[] getFilesInFolderUsingMatch(String folderName, String filterExpression) {
        final File folder = new File(folderName);

        final String fileFilter = filterExpression;
        final File[] files = folder.listFiles((dir, name) -> name.contentEquals(fileFilter));
        return files == null ? new File[0] : files;
    }

    // Copy operations
    public static void copyFromFolderToFolder(String sourceFolder, String targetFolder, boolean createFolders) {
        if (createFolders) {
            copyFromFolderToFolderWithFolderCreation(sourceFolder, targetFolder);
        } else {
            copyFromFolderToFolderWithoutFolderCreation(sourceFolder, targetFolder);
        }

    }

    public static void copyFromFolderToFolderWithFolderCreation(String sourceFolder, String targetFolder) {
        File source = new File(sourceFolder);
        File target = new File(targetFolder);

        try {
            FileUtils.copyDirectory(source, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("resource")
    public static void copyFromFolderToFolderWithoutFolderCreation(String sourceFolder, String targetFolder) {
        final File folder = new File(sourceFolder);

        for (final File file : folder.listFiles()) {
            if (file.isDirectory()) {
                // Ignore
            } else {
                // Copy file
                File sourceFileName = new File(sourceFolder + File.separator + file.getName());
                File targetFileName = new File(targetFolder + File.separator + file.getName());
                FileChannel inputChannel = null;
                FileChannel outputChannel = null;
                try {
                    inputChannel = new FileInputStream(sourceFileName).getChannel();
                    outputChannel = new FileOutputStream(targetFileName).getChannel();
                    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    try {
                        inputChannel.close();
                        outputChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
    }

    // Create Folder
    @Deprecated
    public static void createFolderold(String folderName, boolean errorIfExists) {
        File folder = new File(folderName);

        // if the directory does not exist, create it
        if (!folder.exists()) {
            boolean result = false;

            try {
                folder.mkdir();
                result = true;
            } catch (SecurityException se) {
                // handle
            }
            if (result) {
                // System.out.println("Directory created");
            }
        } else {
            if (errorIfExists) {
                throw new RuntimeException("folder.exists");
            }
        }
    }

    @Deprecated
    public static void createFolderOld(String folderName) {
        File folder = new File(folderName);

        // if the directory does not exist, create it
        if (!folder.exists()) {
            // System.out.println("creating directory: " + folder);
            boolean result = false;

            try {
                folder.mkdir();
                result = true;
            } catch (SecurityException se) {
                // handle
            }
            if (result) {
                // System.out.println("Directory created");
            }
        } else {
            // handle
        }
    }

    public static void createFolder(String folderPath) {
        FolderTools.createFolder(folderPath, false);
    }

    public static void createFolder(String folderPath, boolean errorIfExists) {
        Path path = Paths.get(folderPath);

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("folder.create.error");
            }
        } else {
            if (errorIfExists) {
                throw new RuntimeException("folder.exists");
            }
        }
    }

    // Delete Folder
    public static void deleteFolder(String folderName, boolean deleteFolder) {
        File folder = new File(folderName);
        if (folder.exists()) {
            if (deleteFolder) {
                deleteRecursive(folderName);
            } else {
                for (File c : folder.listFiles())
                    deleteRecursive(c.getAbsolutePath());
            }
        } else {
        }
    }

    private static void deleteRecursive(String folder) {
        File del_folder = new File(folder);
        try {
            deleteAllFiles(del_folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteAllFiles(File file) throws IOException {
        if (file.isDirectory()) {
            for (File c : file.listFiles())
                deleteRecursive(c.getAbsolutePath());
        }
        if (!file.delete())
            throw new FileNotFoundException("Failed to delete file: " + file);
    }

    // Copy Folder
    public static void copyFolderAndSubfolders(String sourceFolderName, String targetFolderName) {
        File folder = new File(sourceFolderName);
        if (folder.exists()) {
            for (File c : folder.listFiles())
                copyRecursive(c.getAbsolutePath(), sourceFolderName, targetFolderName);
        }
    }

    private static void copyRecursive(String folder, String sourceFolderName, String targetFolderName) {
        File copy_folder = new File(folder);
        try {
            copyAllFiles(copy_folder, sourceFolderName, targetFolderName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyAllFiles(File file, String sourceFolderName, String targetFolderName) throws IOException {
        if (file.isDirectory()) {
            for (File c : file.listFiles())
                copyRecursive(c.getAbsolutePath(), sourceFolderName, targetFolderName);
        }
        if (!file.isDirectory()) {
            String sourceFile = file.getAbsolutePath().replace("\\", "/");
            String targetFolder = targetFolderName.replace("\\", "/");
            String sourceFolder = sourceFolderName.replace("\\", "/");
            String incrementPath = sourceFile.substring(sourceFolder.length());
            FileTools.copyFromFileToFile(sourceFile, targetFolder + incrementPath);
        }
    }

    public static boolean isFolder(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean exists(String path) {
        return new File(path).exists();
    }

}