package io.metadew.iesi.gcp.common.tools;

import io.metadew.iesi.gcp.common.configuration.Configuration;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public final class FileTools {

    public static void delete(String fileName) {
        File f = new File(fileName);
        if (f.exists()) {
            if (!f.delete()) {
                throw new RuntimeException("Unable to delete file " + f.getAbsolutePath());
            }
            ;
        }
    }

    public static void delete(String fileName, boolean force) {
        File f = new File(fileName);
        try {
            FileUtils.forceDelete(f);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to delete file " + f.getAbsolutePath());
        }
    }

    public static boolean exists(String fileName) {
        File f = new File(fileName);
        return f.exists();
    }

    public static void appendToFile(String fileName, String header, String record) {
        try {
            PrintWriter out = null;
            File f = new File(fileName);
            if (!f.exists()) {
                out = new PrintWriter(new BufferedWriter(new java.io.FileWriter(fileName, true)));
                if (!header.equalsIgnoreCase(""))
                    out.println(header);
            } else {
                out = new PrintWriter(new BufferedWriter(new java.io.FileWriter(fileName, true)));
            }
            out.println(record);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printToFile(ResultSet rs, String fileName, String delimiter) throws IOException {
        try {
            OutputStream out = new FileOutputStream(fileName);
            PrintStream ps = new PrintStream(out);
            String temp = "";

            // Get result set meta data
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();

            // Get the column names; column indices start from 1
            for (int i = 1; i < cols + 1; i++) {
                ps.print(rsmd.getColumnName(i));
                if (i != cols) {
                    ps.print(delimiter);
                }
            }
            ps.println();

            int rsType = rs.getType();
            if (rsType != java.sql.ResultSet.TYPE_FORWARD_ONLY) {
                rs.beforeFirst();
            }
            while (rs.next()) {
                for (int i = 1; i < cols + 1; i++) {
                    temp = rs.getString(i);
                    if (temp != null && !temp.isEmpty()) {
                        // Remove the CRLF from the db to allow importing into
                        // an excel file
                        temp = temp.replaceAll("[\n\r]", "");
                        // Remove the delimiter from the string to allow
                        // importing into an excel file
                        temp = temp.replaceAll("[" + delimiter + "]", "");
                        ps.print(temp);
                    }
                    if (i != cols) {
                        ps.print(delimiter);
                    }
                }
                ps.println();
            }
            ps.close();
            out.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    // Convert to inputStream
    public static InputStream getInputStream(File file) {
        String output = "";
        try {
            @SuppressWarnings("resource")
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                output += readLine;
                output += "\n";
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
    }


    // Convert to inputsteeam and resolve configuration
    public static InputStream convertToInputStream(File file) {
        String output = "";
        try {
            @SuppressWarnings("resource")
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                output += Configuration.getInstance().resolve(readLine);
                output += "\n";
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
    }

    public static InputStream convertToInputStream(String input) {
        String output = "";
        try {
            Reader inputString = new StringReader(input);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                output += Configuration.getInstance().resolve(readLine);
                output += "\n";
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
    }

    // Copy operations
    @SuppressWarnings("resource")
    public static void copyFromFileToFile(String sourceFile, String targetFile) {
        File src_file_nm = new File(sourceFile);
        File tgt_file_nm = new File(targetFile);
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(src_file_nm).getChannel();
            outputChannel = new FileOutputStream(tgt_file_nm).getChannel();
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

    public static String getFileName(File file) {
        return getFileName(file, true);
    }

    public static String getFileName(File file, boolean extension) {
        String name = "";

        if (file != null && file.exists()) {
            name = file.getName();
        }

        if (!extension) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        return name;

    }


    public static String getFileExtension(File file) {
        String extension = "";

        if (file != null && file.exists()) {
            String name = file.getName();
            extension = name.substring(name.lastIndexOf(".") + 1);
        }

        return extension;

    }

    public static String getFolderPath(File file) {
        return file.getParent();
    }

}