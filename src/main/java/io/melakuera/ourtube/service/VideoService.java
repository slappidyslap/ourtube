package io.melakuera.ourtube.service;

import io.melakuera.ourtube.dto.CommentReqDto;
import io.melakuera.ourtube.dto.EditVideoReqDto;
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

	public Video editVideo(long videoId, EditVideoReqDto dto) {
		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Видео %s не существует", videoId)
				));
		// Заменяем обложку этого видоса
		String thumbnailName = fileService.editThumbnail(
				video.getThumbnailName(), dto.getThumbnail());

		// Заполняем новыми данными
		video.setTitle(dto.getTitle());
		video.setDescription(dto.getDescription());
		video.setTags(dto.getTags());
		video.setVideoStatus(VideoStatus.valueOf(dto.getVideoStatus()));
		video.setThumbnailName(thumbnailName);

		// Сохраняем видео
		videoRepo.save(video);

		return video;
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

		// Получаем видeo по id
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Видео %s не существует", videoId)
				));
		// Создаем коммент соответствующему видео
		Comment comment = new Comment();
		comment.setContent(dto.getContent());
		comment.setUser(user);
		comment.setVideo(video);

		// Добавляем этому соответствующему видосу коммент
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

	public Comment editComment(
			long videoId, long commentId, CommentReqDto dto) {
		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Видео %s не существует", videoId)
				));
		// Находим коммент по id, иначе ошибка
		Comment comment = video.getComments().stream().filter(it ->
				it.getId().equals(commentId)).findFirst().orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Коммент %s не существует", commentId)
				));
		// Изменяем текущий коммент на основе dto
		comment.setContent(dto.getContent());

		// Сохраняем коммент
		return commentRepo.save(comment);
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

	public String likeVideo(Long videoId, User user) {
		// Пока что так
		userRepo.save(user);

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
			user.likeVideo(video);

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

		// Сохраняем видео и юзера
		userRepo.save(user);
		videoRepo.save(video);
		return result.toString();
	}

	public String dislikeVideo(Long videoId, User user) {

		// Пока что так
		userRepo.save(user);

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
			user.dislikeVideo(video);

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

		// Сохраняем видео и юзера
		userRepo.save(user);
		videoRepo.save(video);

		return result.toString();
	}

	public String likeComment(long videoId, long commentId, User user) {
		// Пока что так
		userRepo.save(user);

		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Видео %s не существует", videoId)
				));
		// Находим коммент по id, иначе ошибка
		Comment comment = video.getComments().stream().filter(it ->
				it.getId().equals(commentId)).findFirst().orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Коммент %s не существует", commentId)
				));
		// Строка для вывода результата
		StringBuilder result = new StringBuilder();

		// Получаем кол-во лайков и дизлайков у этого коммента
		long likesCount = comment.getLikesCount();
		long dislikesCount = comment.getDislikesCount();

		// Если в этот коммент авторизованный юзер не ставил лайк
		if (!user.isLikedComment(commentId)) {

			// И если авторизованный юзер уже ставил дизлайк
			if (user.isDislikedComment(commentId)) {
				// То убираем дизлайк у коммента
				user.removeCommentsDislike(commentId);

				// И декрементируем кол-во дизлайков у коммента
				comment.setDislikesCount(--dislikesCount);

				result.append(String.format("Дизлайк к комменту %s убран\n", videoId));
			}
			// И тогда ставим лайк
			user.likeComment(comment);

			// И инкрементируем кол-во лайков у коммента
			comment.setLikesCount(++likesCount);

			// Результат
			result.append(String.format("Лайк к комменту %s поставлен", videoId));

		} else {
			// Иначе юзер ставил лайк, тогда убираем лайк у коммента
			user.removeCommentsLike(commentId);

			// И декрементируем кол-во лайков у коммента
			comment.setLikesCount(--likesCount);

			// Результат
			result.append(String.format("Лайк к комменту %s убран", videoId));
		}

		// Сохраняем коммент и юзера
		userRepo.save(user);
		commentRepo.save(comment);

		return result.toString();
	}

	public String dislikeComment(long videoId, long commentId, User user) {
		// Пока что так
		userRepo.save(user);

		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new IllegalArgumentException(
						String.format("Видео %s не существует", videoId)
				));
		// Находим коммент по id, иначе ошибка
		Comment comment = video.getComments().stream().filter(it -> it.getId().equals(commentId))
				.findFirst().orElseThrow(() ->
						new IllegalArgumentException(
								String.format("Коммент %s не существует", commentId)
						));
		// Строка для вывода результата
		StringBuilder result = new StringBuilder();

		// Получаем кол-во лайков и дизлайков у этого коммента
		long dislikesCount = comment.getDislikesCount();
		long likesCount = comment.getLikesCount();

		// Если в этот коммент авторизованный юзер не ставил дизлайк
		if (!user.isDislikedComment(commentId)) {

			// И если авторизованный юзер уже ставил лайк
			if (user.isLikedComment(commentId)) {
				// То убираем лайк у коммента
				user.removeCommentsLike(commentId);

				// И декрементируем кол-во лайков у коммента
				comment.setLikesCount(--likesCount);

				// Результат
				result.append(String.format("Лайк к комменту %s убран\n", videoId));
			}
			// И тогда ставим дизлайк
			user.dislikeComment(comment);

			// И инкрементируем кол-во дизлайков у коммента
			comment.setDislikesCount(++dislikesCount);

			// Результат
			result.append(String.format("Дизлайк к комменту %s поставлен", videoId));

		} else {
			// Иначе юзер ставил дизлайк, тогда убираем дизлайк у коммента
			user.removeCommentsDislike(commentId);

			// И декрементируем кол-во дизлайков у коммента
			comment.setDislikesCount(--dislikesCount);

			// Результат
			result.append(String.format("Дизлайк к комменту %s убран", videoId));
		}

		// Сохраняем коммент и юзера
		userRepo.save(user);
		commentRepo.save(comment);

		return result.toString();
	}
}
