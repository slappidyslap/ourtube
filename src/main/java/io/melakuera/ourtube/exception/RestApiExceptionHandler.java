package io.melakuera.ourtube.exception;

import io.melakuera.ourtube.dto.ExceptionHandlerResDto;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class RestApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {
			VideoNotFoundException.class,
			CommentNotFoundException.class
	})
	public ResponseEntity<ExceptionHandlerResDto> handleNotFound(
			HttpServletRequest req, Exception ex) {
		ExceptionHandlerResDto dto = new ExceptionHandlerResDto();
		dto.setTimestamp(LocalDateTime.now().format(
				DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")));
		dto.setStatus(HttpStatus.NOT_FOUND.value());
		dto.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
		dto.setMessage(ex.getMessage());
		dto.setPath(req.getRequestURI());

		return new ResponseEntity<>(dto, HttpStatus.NOT_FOUND);
	}
}
