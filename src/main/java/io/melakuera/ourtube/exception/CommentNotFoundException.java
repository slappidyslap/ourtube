package io.melakuera.ourtube.exception;

public class CommentNotFoundException extends RuntimeException {

	public CommentNotFoundException(Long id) {
		super(String.format("Коммент с ID %s не найдено", id));
	}
}
