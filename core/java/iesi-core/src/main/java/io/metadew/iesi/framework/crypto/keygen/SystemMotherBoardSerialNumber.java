package io.metadew.iesi.framework.crypto.keygen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class SystemMotherBoardSerialNumber {

    public static String getSystemMotherBoardSerialNumber() {
        try {
            String OSName = System.getProperty("os.name");
            if (OSName.contains("Windows")) {
                return (getWindowsMotherBoard_SerialNumber());
            } else {
                return (GetLinuxMotherBoard_serialNumber());
            }
        } catch (Exception E) {
            System.err.println("System MotherBoard Exp : " + E.getMessage());
            return null;
        }
    }

    private static String getWindowsMotherBoard_SerialNumber() {
        String result = "";
        try {
            File file = File.createTempFile("tempGetWindowsMotherboardSerialNumber", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs =
                    "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                            + "Set colItems = objWMIService.ExecQuery _ \n"
                            + "   (\"Select * from Win32_BaseBoard\") \n"
                            + "For Each objItem in colItems \n"
                            + "    Wscript.Echo objItem.SerialNumber \n"
                            + "    exit for  ' do the first cpu only! \n"
                            + "Next \n";

            fw.write(vbs);
            fw.close();

            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception E) {
            System.err.println("Windows MotherBoard Exp : " + E.getMessage());
        }
        return result.trim();
    }


    private static String GetLinuxMotherBoard_serialNumber() {
        String command = "dmidecode -s baseboard-serial-number";
        String sNum = null;
        try {
            Process SerNumProcess = Runtime.getRuntime().exec(command);
            BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));
            sNum = sNumReader.readLine().trim();
            SerNumProcess.waitFor();
            sNumReader.close();
        } catch (Exception ex) {
            System.err.println("Linux Motherboard Exp : " + ex.getMessage());
            sNum = null;
        }
        return sNum;
    }
}
