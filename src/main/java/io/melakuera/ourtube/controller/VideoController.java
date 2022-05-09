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
@Slf4j
public class VideoController {

	private final VideoService videoService;
	private final UserRepo userRepo;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	Video newVideo(UploadVideoReqDto dto) {
		// Mock User, для тестинга
		User user = new User();
		user.setUsername("Eld");

		return videoService.newVideo(dto, user);
	}

	@DeleteMapping("/{videoId}")
	String deleteVideo(@PathVariable Long videoId) {
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
	Video getVideoById(@PathVariable Long videoId) {
		return videoService.findById(videoId);
	}

	//=====================================================


	@PostMapping("/{videoId}/comment")
	@ResponseStatus(HttpStatus.CREATED)
	Comment commentVideo(
			@PathVariable Long videoId,
			CommentReqDto dto) {
		// Mock User, для тестинга
		User user = new User();
		user.setUsername("Aktan");

		return videoService.addComment(videoId, dto, user);
	}

	@DeleteMapping("/{videoId}/comment/{commentId}")
	String deleteComment(
			@PathVariable Long videoId,
			@PathVariable Long commentId) {
		videoService.deleteCommentById(videoId, commentId);

		return String.format("Коммент %s удален", videoId);
	}

	@GetMapping("/{videoId}/comment")
	List<Comment> getAllComments(@PathVariable Long videoId) {
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
	String likeToVideo(@PathVariable Long videoId) {
		userRepo.save(user1);
		String r = videoService.likeToVideo(videoId, user1);
		log.info(user1.getLikedVideos().toString());
		log.info(user1.getDislikedVideos().toString());
		return r;
	}

	@PostMapping("/{videoId}/dislike")
	String dislikeToVideo(@PathVariable Long videoId) {

		String r = videoService.dislikeToVideo(videoId, user1);
		log.info(user1.getLikedVideos().toString());
		log.info(user1.getDislikedVideos().toString());
		return r;
	}

	@GetMapping("/test")
	Set<Video> test() {
		return userRepo.findById(2L).orElse(null).getLikedVideos();
	}
}
