package io.metadew.iesi.common.configuration.framework;

import io.metadew.iesi.common.configuration.Configuration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrameworkFolder {

    private String path;
    private String label;
    private String description;
    private String permissions;

    public String getAbsolutePath() {
        return Configuration.getInstance().getMandatoryProperty("home") + path;
    }

}