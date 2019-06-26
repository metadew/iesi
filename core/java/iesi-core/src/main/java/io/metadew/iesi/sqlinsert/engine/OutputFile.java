package io.metadew.iesi.sqlinsert.engine;

import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Operations for writing to output files.
 *
 * @author peter.billen
 */
public class OutputFile {

    public OutputFile() {

    }

    public void appendToFile(String fileName, String header, String record) {
        try {
            PrintWriter out = null;
            File f = new File(fileName);
            if (!f.exists()) {
                out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
                out.println(header);
            } else {
                out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
            }
            out.println(record);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void PrintToFile(ResultSet rs, String path) throws IOException {
        try {
            OutputStream out = new FileOutputStream(path);
            PrintStream ps = new PrintStream(out);
            String temp = "";

            // Get result set meta data
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();

            // Get the column names; column indices start from 1
            for (int i = 1; i < cols + 1; i++) {
                ps.print(rsmd.getColumnName(i));
                if (i != cols) {
                    ps.print("|");
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
                        //Remove the CRLF from the db to allow importing into an excel file
                        temp = temp.replaceAll("[\n\r]", "");
                        //Remove the delimiter from the string to allow importing into an excel file
                        temp = temp.replaceAll("[|]", "");
                        ps.print(temp);
                    }
                    //ps.print(rs.getObject(i));
                    if (i != cols) {
                        ps.print("|");
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