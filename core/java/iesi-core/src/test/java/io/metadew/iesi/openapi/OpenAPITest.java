package io.metadew.iesi.openapi;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

public class OpenAPITest {


    @Before
    public void instantiate() throws IOException {
        Files.createFile(Paths.get("doc.yaml"));
    }

    @After
    public void clear() throws IOException {
        Files.deleteIfExists(Paths.get("doc.yaml"));
    }

    @Test
    public void initWithNoMessages() throws IOException {
        File docFile = new File("doc.yaml");
        SwaggerParseResult result = new OpenAPIParser().readLocation(String.valueOf(docFile), null, null);
        assertNull(result.getMessages());
    }

    @Test
    public void initWithMessages() throws IOException {
        Files.write(Paths.get("doc.yaml"), Collections.singleton("openapi: 3.0.2 \ninfo: \n  description: A description"));
        File docFile = new File("doc.yaml");
        SwaggerParseResult result = new OpenAPIParser().readLocation(String.valueOf(docFile), null, null);
        assertNotNull(result.getMessages());
    }

}
