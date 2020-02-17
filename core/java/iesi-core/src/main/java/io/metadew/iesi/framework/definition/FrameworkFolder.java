package io.metadew.iesi.framework.definition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrameworkFolder {

    private String path;
    private String absolutePath;
    private String label;
    private String description;
    private String permissions;

}