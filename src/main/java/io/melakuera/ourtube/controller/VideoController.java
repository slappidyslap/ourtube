package io.melakuera.ourtube.controller;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import io.melakuera.ourtube.dto.UploadVideoReqDto;
import io.melakuera.ourtube.entity.Video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

	@Value("${spring.servlet.multipart.location}")
	private String pathToVideo;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void uploadVideo(
			@RequestParam("file") MultipartFile file,
			UploadVideoReqDto dto
	) throws IllegalStateException, IOException {
		
		file.transferTo(new File(pathToVideo+file.getOriginalFilename()));
		Video video = new Video();
		video.setId(new Random().nextLong(100, 999));
		video.setTitle(dto.getTitle());
		video.setDescription(dto.getDescription());
		video.setTags(dto.getTags());;
		
	}
}
