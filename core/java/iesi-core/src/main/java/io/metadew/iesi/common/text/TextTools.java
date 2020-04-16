package io.metadew.iesi.common.text;

public final class TextTools {

    public static String shortenTextForDatabase(String input, int columnLength) {
        if (input != null && input.length() >= columnLength) {
            input = input.substring(0, columnLength - 50);
            input += " ... (for more info, check the logfile)";
        }
        return input;
    }

}