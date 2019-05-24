package io.metadew.iesi.common.text;

public final class TextTools {

    public static String setFirstLetterToUpperCase(String input, boolean toLowerCase) {
        String temp = input;
        if (toLowerCase) temp = input.toLowerCase();
        temp = Character.toUpperCase(temp.charAt(0)) + temp.substring(1);
        return temp;
    }

    public static String shortenTextForDatabase(String input, int columnLength) {
        if (input != null && input.length() >= columnLength) {
            input = input.substring(0, columnLength - 50);
            input += " ... (for more info, check the logfile)";
        }
        return input;
    }

}