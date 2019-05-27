package io.metadew.iesi.common.list;

import java.util.ArrayList;
import java.util.List;

public final class ListTools {

    public static boolean inList(ArrayList<String> list, String checkItem) {
        boolean tempResult = false;

        for (String curVal : list) {
            if (curVal.equalsIgnoreCase(checkItem)) {
                tempResult = true;
            }
        }

        return tempResult;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<String> convertStringList(String input, String delimiter) {
        List<String> output = new ArrayList();

        String[] parts = input.split(delimiter);
        for (int i = 0; i < parts.length; i++) {
            String innerpart = parts[i];
            output.add(innerpart);
        }

        return output;
    }
}