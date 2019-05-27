package io.metadew.iesi.connection.tools;

import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


public final class OutputTools {

    // Methods
    public static String getDelimitedFormat(ResultSet rs, String delimiter) {
        String delim_format = "";
        try {

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            ;
            // Get result set meta data
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();

            // Get the column names; column indices start from 1
            for (int i = 1; i < cols + 1; i++) {
                line = line + rsmd.getColumnName(i);
                if (i != cols) {
                    line = line + (delimiter);
                }
            }
            stringBuffer.append(line);
            stringBuffer.append("\n");

            rs.beforeFirst();
            while (rs.next()) {
                line = "";
                for (int i = 1; i < cols + 1; i++) {
                    line = line + rs.getObject(i);
                    if (i != cols) {
                        line = line + (delimiter);
                    }
                }
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }

            delim_format = stringBuffer.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return delim_format;

    }

    public static void appendOutputFile(String fileName, String filePath, String header, String record) {
        try {
            PrintWriter out = null;
            String absoluteFileName = filePath + File.separator + fileName;
            File f = new File(absoluteFileName);
            if (!f.exists()) {
                out = new PrintWriter(new BufferedWriter(new FileWriter(absoluteFileName, true)));
                if (!header.trim().equalsIgnoreCase("")) out.println(header);
            } else {
                out = new PrintWriter(new BufferedWriter(new FileWriter(absoluteFileName, true)));
            }
            out.println(record);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createOutputFile(String fileName, String filePath, String header, String record, boolean overwrite) {
        try {
            PrintWriter out = null;
            String absoluteFileName = filePath + File.separator + fileName;
            File f = new File(absoluteFileName);

            if (f.exists()) {
                if (overwrite) {
                    f.delete();
                } else {
                    throw new RuntimeException("File " + absoluteFileName + " already exists");
                }
            }

            out = new PrintWriter(new BufferedWriter(new FileWriter(absoluteFileName, true)));
            if (!header.trim().equalsIgnoreCase("")) out.println(header);

            out.println(record);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createOutputFile(String fileName, String filePath, ResultSet rs, String delimiter, boolean includeFieldNames) throws IOException {
        try {
            String absoluteFileName = filePath + File.separator + fileName;
            OutputStream out = new FileOutputStream(absoluteFileName);
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
                    // ps.print(rs.getObject(i));
                    if (i != cols) {
                        ps.print(delimiter);
                    }
                }
                ps.println();
            }
            ps.close();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}