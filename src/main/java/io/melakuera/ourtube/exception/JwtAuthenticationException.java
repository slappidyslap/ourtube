package io.melakuera.ourtube.exception;

import javax.naming.AuthenticationException;
public class JwtAuthenticationException extends AuthenticationException {

	public JwtAuthenticationException(String message) {
		super(message);
	}
}
