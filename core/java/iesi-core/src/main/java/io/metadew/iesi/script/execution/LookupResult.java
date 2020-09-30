package io.metadew.iesi.script.execution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LookupResult {

    private String value;
    private String context;
    private String inputValue;

}