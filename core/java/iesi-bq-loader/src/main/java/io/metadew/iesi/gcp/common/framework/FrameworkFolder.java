package io.metadew.iesi.gcp.common.framework;

import io.metadew.iesi.gcp.common.configuration.Configuration;
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

    public Path getAbsolutePath() {
        return Paths.get((String) Configuration.getInstance()
                .getMandatoryProperty("iesi.home"))
                .resolve(path)
                .toAbsolutePath();
    }
}