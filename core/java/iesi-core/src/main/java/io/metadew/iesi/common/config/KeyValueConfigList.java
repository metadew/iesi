package io.metadew.iesi.common.config;

import io.metadew.iesi.framework.execution.FrameworkControl;

public class KeyValueConfigList extends ConfigFile {

    public KeyValueConfigList(FrameworkControl frameworkControl, String input) {
        super();
        try {
            String[] parts = input.split(",");
            for (int i = 0; i < parts.length; i++) {
                String innerpart = parts[i];
                int delim = innerpart.indexOf("=");
                if (delim > 0) {
                    String key = innerpart.substring(0, delim);
                    String value = innerpart.substring(delim + 1);
                    this.setProperty(key, frameworkControl.resolveConfiguration(value));
                } else {
                    // Not a valid configuration
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
