package io.melakuera.ourtube.exception;

public class VideoNotFoundException extends RuntimeException {

	public VideoNotFoundException(Long id) {
		super(String.format("Видео с ID %s не найдено", id));
	}
}
