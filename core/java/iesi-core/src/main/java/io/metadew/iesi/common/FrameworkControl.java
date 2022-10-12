package io.metadew.iesi.common;

import io.metadew.iesi.common.configuration.Configuration;
import org.springframework.stereotype.Component;

@Component
public class FrameworkControl {

    private final Configuration configuration;

    public FrameworkControl(Configuration configuration) {
        this.configuration = configuration;
    }

    public String resolveConfiguration(String input) {
        int openPos;
        int closePos;
        String variable_char = "#";
        String midBit;
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            openPos = temp.indexOf(variable_char);
            closePos = temp.indexOf(variable_char, openPos + 1);

            if (openPos < 0 || closePos < 0) {
                return input;
            }

            midBit = temp.substring(openPos + 1, closePos);

            // Replacing the value if found
            if (configuration.getProperty(midBit).isPresent()) {
                input = input.replaceAll(variable_char + midBit + variable_char, configuration.getProperty(midBit)
                        .map(o -> (String) o)
                        .get());
            }
            temp = temp.substring(closePos + 1, temp.length());

        }
        return input;
    }


}