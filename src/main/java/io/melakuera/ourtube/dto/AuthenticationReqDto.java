package io.melakuera.ourtube.dto;

import lombok.Data;

@Data
public class AuthenticationReqDto {
	private String login;
	private String password;
}
