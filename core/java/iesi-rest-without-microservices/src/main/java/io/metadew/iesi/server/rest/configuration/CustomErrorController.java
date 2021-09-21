package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.server.rest.configuration.security.ErrorJson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Log4j2
@RestController
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    @Value("${iesi.debug}")
    private boolean debug;

    private final ErrorAttributes errorAttributes;

    @Autowired
    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(value = PATH)
    public ResponseEntity<ErrorJson> error(WebRequest webRequest, HttpServletResponse response) {
        return ResponseEntity.status(response.getStatus())
                .body(
                        new ErrorJson(response.getStatus(), getErrorAttributes(webRequest, debug)
                        )
                );
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        return errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
    }
}