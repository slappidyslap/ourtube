package io.melakuera.ourtube.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class FileService {

	@Value("${spring.servlet.multipart.location}")
	private String pathToVideo;

	public String uploadVideo(MultipartFile video) {
		try {
			video.transferTo(new File(
					pathToVideo+"/videos/"+video.getOriginalFilename()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return video.getOriginalFilename();
	}

	public String uploadThumbnail(MultipartFile thumbnail) {
		try {
			thumbnail.transferTo(new File(
					pathToVideo+"/thumbnail/"+thumbnail.getOriginalFilename()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return thumbnail.getOriginalFilename();
	}

	public String editThumbnail(
			String originalThumbnailName, MultipartFile thumbnail) {
		boolean result = new File(
				pathToVideo+"/thumbnail/"+originalThumbnailName).delete();
		if (!result)
			throw new IllegalArgumentException("Ошибка! Файла не существует!");
		try {
			thumbnail.transferTo(new File(
					pathToVideo+"/thumbnail/"+thumbnail.getOriginalFilename()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return thumbnail.getOriginalFilename();
	}
}
