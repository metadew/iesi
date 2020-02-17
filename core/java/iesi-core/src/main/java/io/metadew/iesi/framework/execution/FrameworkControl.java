package io.metadew.iesi.framework.execution;

import io.metadew.iesi.framework.configuration.Configuration;

import java.util.Optional;
import java.util.Properties;

public class FrameworkControl {

    private Properties properties;


    private static FrameworkControl INSTANCE;

    public synchronized static FrameworkControl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkControl();
        }
        return INSTANCE;
    }

    private FrameworkControl() {
    }

    public String resolveConfiguration(String input) {
        int openPos;
        int closePos;
        String variable_char = "#";
        String midBit;
        Optional<String> replaceValue;
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            openPos = temp.indexOf(variable_char);
            closePos = temp.indexOf(variable_char, openPos + 1);
            midBit = temp.substring(openPos + 1, closePos);

            // Replacing the value if found
            if (Configuration.getInstance().getProperty(midBit).isPresent()) {
                input = input.replaceAll(variable_char + midBit + variable_char, Configuration.getInstance().getProperty(midBit)
                        .map(o -> (String) o)
                        .get());
            }
            temp = temp.substring(closePos + 1, temp.length());

        }
        return input;
    }


}