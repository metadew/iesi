package io.metadew.iesi.server.rest.error;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(DataNotFoundException.class)
	public void HandleNotFound(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value());

	}
	@ExceptionHandler(DatasNotFoundExceptions.class)
	public void HandleNotFoundSeveral(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value());

	}
	

}