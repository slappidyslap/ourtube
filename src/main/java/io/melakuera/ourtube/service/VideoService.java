package io.melakuera.ourtube.service;

import io.melakuera.ourtube.dto.CommentReqDto;
import io.melakuera.ourtube.dto.UploadVideoReqDto;
import io.melakuera.ourtube.entity.Comment;
import io.melakuera.ourtube.entity.User;
import io.melakuera.ourtube.entity.Video;
import io.melakuera.ourtube.entity.VideoStatus;
import io.melakuera.ourtube.repo.CommentRepo;
import io.melakuera.ourtube.repo.UserRepo;
import io.melakuera.ourtube.repo.VideoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

	private final UserService userService;
	private final FileService fileService;
	private final VideoRepo videoRepo;
	private final UserRepo userRepo;
	private final CommentRepo commentRepo;

	public Video newVideo(UploadVideoReqDto dto, User user) {
		// Загружаем видео и превью в хранилище
		String videoName = fileService.uploadVideo(dto.getVideo());
		String thumbnailName = fileService.uploadThumbnail(dto.getThumbnail());

		// Создаем нового видео на основе dto
		Video video = new Video();
		video.setTitle(dto.getTitle());
		video.setDescription(dto.getDescription());
		video.setTags(dto.getTags());
		video.setUser(user);
		video.setVideoStatus(VideoStatus.valueOf(dto.getVideoStatus()));
		video.setVideoName(videoName);
		video.setThumbnailName(thumbnailName);

		// Сохраненяем видео и юзера в бд
		userRepo.save(user);
		videoRepo.save(video);

		// Возвращаем новосозданное видео
		log.info("Видео {} сохранено", video.getId());
		return video;
	}

	public void deleteById(Long videoId) {
		// Удаляем по videoId если таковое видео существует
		if (videoRepo.findById(videoId).isPresent()) {
			videoRepo.deleteById(videoId);

			log.info("Видео {} удалено", videoId);
		}
		// Иначе ошибка
		else {
			throw new IllegalArgumentException(
					String.format("Видео %s не существует", videoId));
		}
	}

	public List<Video> findAll() {
		// Получаем всех видосов
		return videoRepo.findAll();
	}

	public Video findById(Long videoId) {
		// Ошибка если таковое видео не существует
		return videoRepo.findById(videoId).orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Видео %s не существует", videoId)
				));
	}

	//====================================

	public Comment addComment(Long videoId, CommentReqDto dto, User user) {

		// Получаем видое по id
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Видео %s не существует", videoId)
				));
		// Создаем коммент соответствующему видео
		Comment comment = new Comment();
		comment.setContent(dto.getContent());
		comment.setUser(user);
		comment.setVideo(video);

		// Добавляем этому соответствующему видосу коммент тот что сверху
		video.getComments().add(comment);

		// Сохраненяем коммент и юзера в бд
		videoRepo.save(video);
		userRepo.save(user);
		commentRepo.save(comment);

		// Возвращаем новосозданный коммент
		log.info("Комент {} сохранен", comment.getId());
		return comment;
	}

	public void deleteCommentById(Long videoId, Long commentId) {
		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Видео %s не существует", videoId)
				));

		// Находим у это видоса коммент по id, иначе ошибка
		video.getComments().stream().filter(it ->
				it.getId().equals(commentId)).findFirst().orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Коммент %s не существует", commentId)));

		// Если нашлось, то удаляем этот коммент
		commentRepo.deleteById(commentId);
	}

	public List<Comment> findAllVideoCommentsByVideoId(Long videoId) {
		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Видео %s не существует", videoId)
				));
		// Возвращаем его комменты
		return video.getComments();
	}

	//====================================

	public String likeToVideo(Long videoId, User user) {
		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Видео %s не существует", videoId)
				));
		// Результат для вывода
		StringBuilder result = new StringBuilder();

		// Получаем кол-во лайков и дизлайков у этого видоса
		long likesCount = video.getLikesCount();
		long dislikesCount = video.getDislikesCount();

		// Если в это видео авторизованный юзер не ставил лайк
		if (!user.isLikedVideo(videoId)) {

			// И если авторизованный юзер уже ставил дизлайк
			if (user.isDislikedVideo(videoId)) {
				// То убираем дизлайк у видео
				user.removeVideosDislike(videoId);

				// И декрементируем кол-во дизлайков у видео
				video.setDislikesCount(--dislikesCount);

				result.append(String.format("Дизлайк к видео %s убран\n", videoId));
			}
			// И тогда ставим лайк
			user.likeToVideo(video);

			// И инкрементируем кол-во лайков у видео
			video.setLikesCount(++likesCount);

			// Результат
			result.append(String.format("Лайк к видео %s поставлен", videoId));

		} else {
			// Иначе юзер ставил лайк, тогда убираем лайк у видео
			user.removeVideosLike(videoId);

			// И декрементируем кол-во лайков у видео
			video.setLikesCount(--likesCount);

			// Результат
			result.append(String.format("Лайк к видео %s убран", videoId));
		}

		// Сохраняем видео
		userRepo.save(user);
		videoRepo.save(video);
//		System.out.println("//=================================================");
//		System.out.println(user);
//		System.out.println(user.getLikedVideos());
		return result.toString();
	}

	public String dislikeToVideo(Long videoId, User user) {
		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Видео %s не существует", videoId)
				));
		// Результат для вывода
		StringBuilder result = new StringBuilder();

		// Получаем кол-во дизлайков и лайков у этого видоса
		long dislikesCount = video.getDislikesCount();
		long likesCount = video.getLikesCount();

		// Если в это видео авторизованный юзер не ставил дизлайк
		if (!user.isDislikedVideo(videoId)) {

			// И если авторизованный юзер уже ставил лайк
			if (user.isLikedVideo(videoId)) {
				// То убираем лайк у видео
				user.removeVideosLike(videoId);

				// И декрементируем кол-во лайков у видео
				video.setLikesCount(--likesCount);

				result.append(String.format("Лайк к видео %s убран\n", videoId));
			}
			// И тогда ставим дизлайк
			user.disLikeToVideo(video);

			// И инкрементируем кол-во дизлайков у видео
			video.setDislikesCount(++dislikesCount);

			// Результат
			result.append(String.format("Дизлайк к видео %s поставлен", videoId));

		} else {
			// Иначе юзер ставил дизлайк, тогда убираем дизлайк у видео
			user.removeVideosDislike(videoId);

			// И декрементируем кол-во дизлайков у видео
			video.setDislikesCount(--dislikesCount);

			// Результат
			result.append(String.format("Дизлайк к видео %s убран", videoId));
		}

		// Сохраняем видео
		userRepo.save(user);
		videoRepo.save(video);

		return result.toString();
	}
}
