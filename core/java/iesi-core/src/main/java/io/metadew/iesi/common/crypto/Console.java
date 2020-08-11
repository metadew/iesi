package io.metadew.iesi.common.crypto;

public class Console {

    private static Console INSTANCE;

    public static Console getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Console();
        }
        return INSTANCE;
    }

    private Console() {}

    public char[] readPassword(String text) {
        java.io.Console console = System.console();
        return console.readPassword(text);
    }

}
