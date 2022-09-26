package io.metadew.iesi.common.configuration.framework;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrameworkFolder {

    private String path;
    private String label;
    private String description;
    private String permissions;

    Configuration configuration = SpringContext.getBean(Configuration.class);

    public Path getAbsolutePath() {
        return Paths.get((String) configuration
                .getMandatoryProperty("iesi.home"))
                .resolve(path)
                .toAbsolutePath();
    }

}