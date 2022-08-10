package io.metadew.iesi.launch;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The encryption launcher is entry point to launch the encryption utility from commandline.
 * All passwords will be encrypted using this utility.
 *
 * @author peter.billen
 */
public class EncryptionLauncher {

    public static void main(String[] args) {

        //Configuration.getInstance();

        Console console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.exit(1);
        }

        char[] passwordArray = console.readPassword("Enter the password to encrypt: ");

        String input = new String(passwordArray);
        // Configuration.getInstance();
        // FrameworkCrypto frameworkCrypto = FrameworkCrypto.getInstance();

        String output = "";
        // output = frameworkCrypto.encrypt(input);

        System.out.println("The encrypted password is: " + output);

        try {
            StringSelection stringSelection = new StringSelection(output);
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);

            System.out.println("The encrypted password has been copied to the clipboard");
        } catch (HeadlessException e) {
            // do nothing, copy to clipboard failed
            System.out.println("The encrypted password not been copied to the clipboard");
        }

        System.out.println("Press any key to exit...");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            input = br.readLine();
            br.close();
        } catch (IOException ioe) {
            System.out.println("IO error waiting for any key to be pressed");
            System.exit(1);
        }
    }

}
