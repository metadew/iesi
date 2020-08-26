package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Getter
@Setter
public class ApiError {

    private HttpStatus status;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private HttpServletResponse response;
    private String message;
    private String debugMessage;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status, Throwable ex) {
        this();
        this.status = status;
//        this.response = response;
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    public String convertToJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper.writeValueAsString(this);
    }

}