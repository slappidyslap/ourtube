package io.melakuera.ourtube.dto;

import java.util.Set;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class NewVideoReqDto {
	private String title;
	private String description;
	private Set<String> tags;
	private String videoStatus;
}
