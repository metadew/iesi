package io.metadew.iesi.connection.host;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

/**
 * Connection object to manage the Linux host user info.
 * Modified from http://www.jcraft.com/jsch/examples/Exec.java.html
 */
public class LinuxHostUserInfo implements UserInfo, UIKeyboardInteractive {
    private String passwd;

    public LinuxHostUserInfo(String passwd) {
        this.passwd = passwd;
    }

    public String getPassword() {
        return passwd;
    }

    public boolean promptYesNo(String str) {
        System.out.println(str);
        System.out.println("default answer: yes");
        return true;
    }

    public String getPassphrase() {
        return null;
    }

    public boolean promptPassphrase(String message) {
        return true;
    }

    public boolean promptPassword(String message) {
        return true;
    }

    public void showMessage(String message) {

    }

    public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
                                              boolean[] echo) {
        String[] response = new String[1];
        response[0] = passwd;
        return response;
    }
}