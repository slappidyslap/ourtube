package io.melakuera.ourtube.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
public class EditVideoReqDto {
	private String title;
	private String description;
	private Set<String> tags;
	private String videoStatus;
	private MultipartFile thumbnail;
}
