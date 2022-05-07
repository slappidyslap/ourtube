package io.melakuera.ourtube.dto;

import java.util.Set;

import lombok.Data;

@Data
public class UploadVideoReqDto {
	private String title;
	private String description;
	private Set<String> tags;
}
