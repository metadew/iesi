package io.metadew.iesi.common.configuration.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameworkPlugin {

    private String runtime;
    private String libraries;

}