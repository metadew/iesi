package io.metadew.iesi.server.rest.openapi;

import io.metadew.iesi.openapi.OpenAPIGenerator;
import io.metadew.iesi.openapi.TransformResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
@ConditionalOnWebApplication
public class OpenAPIService implements IOpenAPIService {

    private final OpenAPIGenerator openAPIGenerator;

    @Autowired
    public OpenAPIService(OpenAPIGenerator openAPIGenerator) {
        this.openAPIGenerator = openAPIGenerator;
    }

    @Override
    public TransformResult transform(MultipartFile multipartFile) throws IOException {
        File file = File.createTempFile(multipartFile.getName(), null);

        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        }

        return openAPIGenerator.transformFromFile(file.getAbsolutePath());
    }

    @Override
    public TransformResult transform(String jsonContent) {
        return openAPIGenerator.transformFromJsonContent(jsonContent);
    }

}
