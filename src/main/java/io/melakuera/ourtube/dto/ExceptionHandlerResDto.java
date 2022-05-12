package io.melakuera.ourtube.dto;

import lombok.Data;

@Data
public class ExceptionHandlerResDto {
	private String timestamp;
	private Integer status;
	private String error;
	private String message;
	private String path;
}
