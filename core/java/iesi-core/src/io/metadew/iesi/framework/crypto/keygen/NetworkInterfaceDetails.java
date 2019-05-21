package io.metadew.iesi.framework.crypto.keygen;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetworkInterfaceDetails {

    public static String getIPAddress() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        return ip.getHostAddress();
    }

    public static String getHostName() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        return ip.getHostName();
    }

    public static String getHostNameIncludingDomain() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        return ip.getCanonicalHostName();
    }

    public static byte[] getMACAddresses() throws UnknownHostException {
        byte[] mac = null;
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                System.out.println(network.getName());
                mac = network.getHardwareAddress();
            }
        } catch (

                SocketException e) {
            e.printStackTrace();
        }
        return mac;
    }

    public static String getMACAddress(String name) throws UnknownHostException {
        String output = "";
        byte[] mac = null;
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                if (network.getName().trim().equalsIgnoreCase(name.trim().toLowerCase())) {
                    mac = network.getHardwareAddress();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    output = sb.toString();

                }
                mac = network.getHardwareAddress();
            }
        } catch (

                SocketException e) {
            e.printStackTrace();
        }
        return output;
    }

    public static void main(String[] args) throws UnknownHostException, SocketException {
        try {

            System.out.println(getHostNameIncludingDomain());

            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();

                if (mac != null) {
                    System.out.println(network.getDisplayName());
                    System.out.print("Current MAC address : ");

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    System.out.println(sb.toString());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

}