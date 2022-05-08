package io.melakuera.ourtube.service;

import io.melakuera.ourtube.entity.User;
import io.melakuera.ourtube.entity.Video;
import io.melakuera.ourtube.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepo userRepo;

	public boolean isLikedVideo(Long videoId, User user) {
		return user.getLikedVideos().stream().anyMatch(it ->
				it.getId().equals(videoId));
	}

	public boolean isDislikedVideo(Long videoId, User user) {
		return user.getDislikedVideos().stream().anyMatch(it ->
				it.getId().equals(videoId));
	}

	public void removeVideosLike(Long videoId, User user) {
		user.getLikedVideos().removeIf(it -> it.getId().equals(videoId));

		userRepo.save(user);
	}

	public void removeVideosDislike(Long videoId, User user) {
		user.getDislikedVideos().removeIf(it -> it.getId().equals(videoId));

		userRepo.save(user);
	}

	public void likeToVideo(Video video, User user) {
		user.getLikedVideos().add(video);

		User user1 = userRepo.save(user);
		log.info(user1.toString());
		log.info(user1.getLikedComment().toString());
	}

	public void disLikeToVideo(Video video, User user) {
		user.getDislikedVideos().add(video);

		userRepo.save(user);
	}
}

