package io.metadew.iesi.framework.crypto.keygen;

import io.metadew.iesi.framework.crypto.tools.CryptoTools;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HostContextIdentifier {

    public static String getHostContextIdentifier() {
        try {
            String OSName = System.getProperty("os.name");
            if (OSName.contains("Windows")) {
                return (CryptoTools.formatKeyString(getWindowsSystemInfo(), 16));
            } else {
                return (CryptoTools.formatKeyString(GetLinuxSystemInfo(), 16));
            }
        } catch (Exception E) {
            throw new RuntimeException("Not possible to generte host context identifier");
        }
    }

    private static String getWindowsSystemInfo() throws UnknownHostException {
        String output = "";
        try {
            File file = File.createTempFile("tempGetWindowsMotherboardSerialNumber", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n" + "   (\"Select * from Win32_BaseBoard\") \n"
                    + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.SerialNumber \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            fw.write(vbs);
            fw.close();

            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                output += line;
            }
            input.close();
        } catch (Exception E) {
            System.err.println("Windows MotherBoard Exp : " + E.getMessage());
        }
        output = output.trim();
        output += NetworkInterfaceDetails.getHostNameIncludingDomain();

        return generateMD5Hash(output);
    }

    private static String GetLinuxSystemInfo() throws UnknownHostException {
        String output = "";
        output += getSystemFileInfo("modalias");
        output += NetworkInterfaceDetails.getHostNameIncludingDomain();
        // output +=
        // NetworkInterfaceDetails.getMACAddress(configTools.getProperty(configTools.getSettingsConfig().getEncryptionHostContextNetworkName()));
        return generateMD5Hash(output);
    }

    private static String getSystemFileInfo(String fileInfo) {
        String output = "";
        String systemInfoPath = "/sys/class/dmi/id/";
        File file = new File(systemInfoPath + fileInfo);
        try {
            @SuppressWarnings("resource")
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                output += readLine.trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;

    }

    public static String generateMD5Hash(String input) {
        MessageDigest md = null;
        String output = "";
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            output = DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Not possible to generte hash value");
        }

        return output;

    }
}
