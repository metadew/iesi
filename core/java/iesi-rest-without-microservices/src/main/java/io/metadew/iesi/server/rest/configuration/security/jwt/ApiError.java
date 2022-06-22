package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;

@Getter
@Setter
public class ApiError {

    private int status;
    private LocalDateTime timestamp;
    private String message;

    public ApiError(HttpStatus status, Throwable ex, LocalDateTime timestamp) {
        super();
        // TODO: inject clock bean
        this.timestamp = timestamp;
        this.status = status.value();
        this.message = ex.getMessage();
    }

    public String convertToJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsString(this);
    }
}