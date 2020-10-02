package io.metadew.iesi.gcp.common.tools;

public final class ParsingTools {

    public static boolean isRegexFunction(String input) {
        if (input == null) {
            input = "";
        }
        input = input.trim();

        String typeChar = "=regex(";
        if (input.startsWith(typeChar)) {
            // Check last character
            if (!input.substring(input.length() - 1).equalsIgnoreCase(")")) {
                throw new RuntimeException("Incorrect regex syntax for: " + input);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static String getRegexFunctionValue(String input) {
        if (input == null) {
            input = "";
        }
        input = input.trim();

        String typeChar = "=regex(";
        if (input.startsWith(typeChar)) {
            // Check last character
            if (!input.substring(input.length() - 1).equalsIgnoreCase(")")) {
                throw new RuntimeException("Incorrect parameter syntax for: " + input);
            }

            // Get type
            int openPos;
            int closePos;
            String startTypeChar = "(";
            // String type;
            String temp = input;
            if (input.indexOf(startTypeChar) > 0) {
                openPos = temp.indexOf(typeChar);
                closePos = temp.indexOf(startTypeChar, openPos + 1);
                // type = temp.substring(openPos + 1, closePos);
            } else {
                throw new RuntimeException("Incorrect parameter syntax for: " + input);
            }

            // Get Type Value
            String typeValue = input.substring(closePos + 1, input.length() - 1);
            // System.out.println(input);
            // System.out.println(typeValue);
            // System.out.println(type);
            return typeValue;
        } else {
            return input;
        }

    }

}