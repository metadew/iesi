package io.metadew.iesi.server.rest.openapi;

import io.metadew.iesi.openapi.TransformResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IOpenAPIService {

    TransformResult transform(MultipartFile multipartFile) throws IOException;
    TransformResult transform(String jsonContent);

}
