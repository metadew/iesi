package io.metadew.iesi.server.rest.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(DataNotFoundException.class)
	public void HandleNotFound(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value());

	}

	@ExceptionHandler(SqlNotFoundException.class)
	public void SqlNotFound(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());

	}

	@ExceptionHandler(value = { MethodArgumentTypeMismatchException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String, String> handleException(MethodArgumentTypeMismatchException e) {
		Map<String, String> errMessages = new HashMap<>();
		errMessages.put("error", "Wrong query parameter for '" + e.getName() + "'");
		errMessages.put("query_parameter", e.getName());
		errMessages.put("errorCode", e.getErrorCode() + " , " + e.getRootCause().toString());
		errMessages.put("message", e.getMessage());
		return errMessages;
	}
}