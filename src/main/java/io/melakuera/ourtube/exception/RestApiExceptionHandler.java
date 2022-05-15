package io.melakuera.ourtube.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class RestApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {
			VideoNotFoundException.class,
			CommentNotFoundException.class
	})
	public ResponseEntity<?> handleNotFound(HttpServletRequest req, Exception ex) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().format(
				DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")));
		body.put("status", HttpStatus.NOT_FOUND.value());
		body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
		body.put("message", ex.getMessage());
		body.put("path", req.getRequestURI());

		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<?> handleUserExists(HttpServletRequest req, Exception ex) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().format(
				DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")));
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		body.put("message", ex.getMessage());
		body.put("path", req.getRequestURI());

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
}
