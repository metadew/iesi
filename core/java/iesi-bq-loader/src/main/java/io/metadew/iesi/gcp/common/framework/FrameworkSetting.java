package io.metadew.iesi.gcp.common.framework;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrameworkSetting {

    private String path;
    private String label;
    private String description;
    private String group;
    private String category;

}