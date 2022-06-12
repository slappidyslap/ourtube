package io.melakuera.ourtube.controller;

import io.melakuera.ourtube.dto.CommentReqDto;
import io.melakuera.ourtube.dto.EditVideoReqDto;
import io.melakuera.ourtube.dto.NewVideoReqDto;
import io.melakuera.ourtube.entity.Comment;
import io.melakuera.ourtube.entity.User;
import io.melakuera.ourtube.entity.Video;
import io.melakuera.ourtube.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

	private final VideoService videoService;

	@PostMapping("/uploadVideo")
	ResponseEntity<?> uploadVideo(@RequestParam("video") MultipartFile video) {

		return videoService.uploadVideo(video);
	}

	@PostMapping("/uploadThumbnail")
	ResponseEntity<?> uploadThumbnail(@RequestParam("thumbnail") MultipartFile thumbnail) {

		return videoService.uploadThumbnail(thumbnail);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	Video newVideo(@RequestBody NewVideoReqDto dto) {

		return videoService.newVideo(dto);
	}

	@DeleteMapping("/{videoId}")
	String deleteVideo(@PathVariable long videoId, @AuthenticationPrincipal User authUser) {
		videoService.deleteById(videoId, authUser);

		return String.format("Видео %s удалено", videoId);
	}

	@PatchMapping("/{videoId}")
	Video editVideo(
			@PathVariable long videoId,
			EditVideoReqDto dto,
			@AuthenticationPrincipal User authUser) {

		return videoService.editVideo(videoId, dto, authUser);
	}

	@GetMapping
	List<Video> getVideos(
			@RequestParam(value = "tag", required = false) Set<String> tags) {
		if (tags != null) {
			//TODO Я хз как реализваоть
			System.out.println(tags);
		}
		return videoService.findAll();
	}

	@GetMapping("/{videoId}")
	Video getVideoById(@PathVariable long videoId) {
		return videoService.findById(videoId);
	}

	//=====================================================


	@PostMapping("/{videoId}/comments")
	@ResponseStatus(HttpStatus.CREATED)
	Comment commentVideo(
			@PathVariable long videoId,
			@RequestBody CommentReqDto dto,
			@AuthenticationPrincipal User authUser) {

		return videoService.addComment(videoId, dto, authUser);
	}

	@PatchMapping("/{videoId}/comments/{commentId}")
	Comment editComment(
			@PathVariable long videoId,
			@PathVariable long commentId,
			@RequestBody CommentReqDto dto,
			@AuthenticationPrincipal User authUser) {

		return videoService.editComment(videoId, commentId, dto, authUser);
	}

	@DeleteMapping("/{videoId}/comments/{commentId}")
	String deleteComment(
			@PathVariable long videoId,
			@PathVariable long commentId,
			@AuthenticationPrincipal User authUser) {
		videoService.deleteCommentById(videoId, commentId, authUser);

		return String.format("Коммент %s удален", videoId);
	}

	@GetMapping("/{videoId}/comments")
	List<Comment> getAllComments(@PathVariable long videoId) {
		return videoService.findAllVideoCommentsByVideoId(videoId);
	}

	//=====================================================

	@PostMapping("/{videoId}/like")
	String likeVideo(@PathVariable long videoId, @AuthenticationPrincipal User authUser) {
		return videoService.likeVideo(videoId, authUser);
	}

	@PostMapping("/{videoId}/dislike")
	String dislikeVideo(@PathVariable long videoId, @AuthenticationPrincipal User authUser) {
		return videoService.dislikeVideo(videoId, authUser);
	}

	//=====================================================

	@PostMapping("/{videoId}/comments/{commentId}/like")
	String likeComment(
			@PathVariable long videoId,
			@PathVariable long commentId,
			@AuthenticationPrincipal User authUser) {
		return videoService.likeComment(videoId, commentId, authUser);
	}

	@PostMapping("/{videoId}/comments/{commentId}/dislike")
	String dislikeComment(
			@PathVariable long videoId,
			@PathVariable long commentId,
			@AuthenticationPrincipal User authUser) {
		return videoService.dislikeComment(videoId, commentId, authUser);
	}
}
