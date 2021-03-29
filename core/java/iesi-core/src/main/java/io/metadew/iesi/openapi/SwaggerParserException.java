package io.metadew.iesi.openapi;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;



@Data
@EqualsAndHashCode(callSuper = true)
public class SwaggerParserException extends RuntimeException  {
    private final List<String> messages;

    public SwaggerParserException(List<String> messages) {
        this.messages = messages;
    }
}
