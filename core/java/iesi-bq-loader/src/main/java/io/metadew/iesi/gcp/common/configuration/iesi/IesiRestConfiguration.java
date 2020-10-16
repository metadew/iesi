package io.metadew.iesi.gcp.common.configuration.iesi;

import io.metadew.iesi.gcp.common.configuration.Configuration;

public final class IesiRestConfiguration {

    public static String getUrl() {
        if (Configuration.getInstance().getProperty("iesi.gcp.iesi-rest.api").isPresent()) {
            String setting = (String) Configuration.getInstance().getProperty("iesi.gcp.iesi-rest.api").orElse("");
            if (!setting.substring(setting.length() - 1).equalsIgnoreCase("/")) {
                setting = setting + "/";
            }
            return setting;
        } else {
            return "";
        }

    }


}