package io.melakuera.ourtube.exception;

public class UserAlreadyExistsException extends RuntimeException {
	public UserAlreadyExistsException() {
		super("Такой юзер уже существует");
	}
}
