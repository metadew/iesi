package io.metadew.iesi.connection.tools;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class CompressionTools {

    final static int BUFFER = 2048;

    public static void unzip() {
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream("/path");
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println("Extracting: " + entry);
                int count;
                byte data[] = new byte[BUFFER];
                // write the files to the disk
                FileOutputStream fos = new FileOutputStream(entry.getName());
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unTarFile(FrameworkExecution frameworkExecution, String sourcePath, String sourceFile) {
        // Path to input file, which is a tar file
        String inputFilePath = sourcePath + File.separator + sourceFile;

        // This folder should exist - temp folder - that's where .tar file will go
        String tmpFolderPath = sourcePath + File.separator + "tmp";
        FolderTools.deleteFolder(tmpFolderPath, true);
        FolderTools.createFolder(tmpFolderPath);

        // After untar files will go to this folder
        String outputFolderPath = sourcePath;

        try {
            File inputFile = new File(inputFilePath);

            //String outputFile = this.getFileName(inputFile, tmpFolderPath);
            //File tarFile = new File(outputFile);
            // Calling method to decompress file
            // tarFile = this.deCompressGZipFile(inputFile, tarFile);

            File destFile = new File(outputFolderPath);
            FolderTools.createFolder(outputFolderPath);

            // Calling method to untar file
            unTarFile(inputFile, destFile);

            // remove temp folder
            FolderTools.deleteFolder(tmpFolderPath, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void unTarFile(File tarFile, File destFile) throws IOException {
        FileInputStream fis = new FileInputStream(tarFile);
        TarArchiveInputStream tis = new TarArchiveInputStream(fis);
        TarArchiveEntry tarEntry = null;

        // tarIn is a TarArchiveInputStream
        while ((tarEntry = tis.getNextTarEntry()) != null) {
            File outputFile = new File(destFile + File.separator + tarEntry.getName());

            if (tarEntry.isDirectory()) {

                //System.out.println("outputFile Directory ---- " + outputFile.getAbsolutePath());
                if (!outputFile.exists()) {
                    outputFile.mkdirs();
                }
            } else {
                // File outputFile = new File(destFile + File.separator + tarEntry.getName());
                // System.out.println("outputFile File ---- " + outputFile.getAbsolutePath());
                outputFile.getParentFile().mkdirs();
                // outputFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(outputFile);
                IOUtils.copy(tis, fos);
                fos.close();
            }
        }
        tis.close();
    }

    @SuppressWarnings("unused")
    private static File deCompressGZipFile(File gZippedFile, File tarFile) throws IOException {
        FileInputStream fis = new FileInputStream(gZippedFile);
        GZIPInputStream gZIPInputStream = new GZIPInputStream(fis);

        FileOutputStream fos = new FileOutputStream(tarFile);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = gZIPInputStream.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }

        fos.close();
        gZIPInputStream.close();
        return tarFile;

    }

    @SuppressWarnings("unused")
    private static String getFileName(File inputFile, String outputFolder) {
        return outputFolder + File.separator + inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
    }

}