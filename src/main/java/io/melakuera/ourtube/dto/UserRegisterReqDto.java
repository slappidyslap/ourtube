package io.melakuera.ourtube.dto;

import lombok.Data;

@Data
public class UserRegisterReqDto {
	private String email;
	private String username;
	private String password;
}