package io.melakuera.ourtube.controller;

import io.melakuera.ourtube.dto.CommentReqDto;
import io.melakuera.ourtube.dto.UploadVideoReqDto;
import io.melakuera.ourtube.entity.Comment;
import io.melakuera.ourtube.entity.User;
import io.melakuera.ourtube.entity.Video;
import io.melakuera.ourtube.repo.UserRepo;
import io.melakuera.ourtube.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

	private final VideoService videoService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	Video newVideo(UploadVideoReqDto dto) {
		// Mock User, для тестинга
		User user = new User();
		user.setUsername("Eld");

		return videoService.newVideo(dto, user);
	}

	@DeleteMapping("/{videoId}")
	String deleteVideo(@PathVariable long videoId) {
		videoService.deleteById(videoId);

		return String.format("Видео %s удалено", videoId);
	}

	// patch

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
			CommentReqDto dto) {
		// Mock User, для тестинга
		User user = new User();
		user.setUsername("Aktan");

		return videoService.addComment(videoId, dto, user);
	}

	@DeleteMapping("/{videoId}/comments/{commentId}")
	String deleteComment(
			@PathVariable long videoId,
			@PathVariable long commentId) {
		videoService.deleteCommentById(videoId, commentId);

		return String.format("Коммент %s удален", videoId);
	}

	@GetMapping("/{videoId}/comments")
	List<Comment> getAllComments(@PathVariable long videoId) {
		return videoService.findAllVideoCommentsByVideoId(videoId);
	}

	//=====================================================

	// Mock User, для тестинга
	// Тут надо, потому что нужен один и тот же юзер
	User user1 = new User();
	{
		user1.setUsername("Dastan");

	}

	@PostMapping("/{videoId}/like")
	String likeVideo(@PathVariable long videoId) {
		return videoService.likeVideo(videoId, user1);
	}

	@PostMapping("/{videoId}/dislike")
	String dislikeVideo(@PathVariable long videoId) {
		return videoService.dislikeVideo(videoId, user1);
	}

	//=====================================================

	// Mock User, для тестинга
	// Тут надо, потому что нужен один и тот же юзер
	User user2 = new User();
	{
		user2.setUsername("Salman");

	}

	@PostMapping("/{videoId}/comments/{commentId}/like")
	String likeComment(
			@PathVariable long videoId,
			@PathVariable long commentId) {
		return videoService.likeComment(videoId, commentId, user2);
	}

	@PostMapping("/{videoId}/comments/{commentId}/dislike")
	String dislikeComment(
			@PathVariable long videoId,
			@PathVariable long commentId) {
		return videoService.dislikeComment(videoId, commentId, user2);
	}
}
