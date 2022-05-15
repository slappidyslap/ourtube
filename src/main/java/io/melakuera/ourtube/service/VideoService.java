package io.melakuera.ourtube.service;

import io.melakuera.ourtube.dto.CommentReqDto;
import io.melakuera.ourtube.dto.EditVideoReqDto;
import io.melakuera.ourtube.dto.UploadVideoReqDto;
import io.melakuera.ourtube.entity.Comment;
import io.melakuera.ourtube.entity.User;
import io.melakuera.ourtube.entity.Video;
import io.melakuera.ourtube.entity.VideoStatus;
import io.melakuera.ourtube.exception.CommentNotFoundException;
import io.melakuera.ourtube.exception.OurtubeException;
import io.melakuera.ourtube.exception.VideoNotFoundException;
import io.melakuera.ourtube.repo.CommentRepo;
import io.melakuera.ourtube.repo.UserRepo;
import io.melakuera.ourtube.repo.VideoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VideoService {

	private final FileService fileService;
	private final UserService userService;
	private final VideoRepo videoRepo;
	private final UserRepo userRepo;
	private final CommentRepo commentRepo;

	@Transactional(readOnly = false)
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

	@Transactional(readOnly = false)
	public void deleteById(Long videoId, User authUser) {
		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new VideoNotFoundException(videoId)
		);
		// Проверяем является ли авторизованный юзер автором этого видео
		if (video.getUser().equals(authUser)) {
			videoRepo.deleteById(videoId);
			log.info("Видео {} удалено", videoId);
			// Иначе он не явл. автором, посему ошибка
		} else {
			throw new OurtubeException("Только автор видео может удалять свои видео");
		}
	}

	@Transactional(readOnly = false)
	public Video editVideo(long videoId, EditVideoReqDto dto, User authUser) {
		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new VideoNotFoundException(videoId)
		);
		// Проверяем является ли авторизованный юзер автором этого видео
		if (video.getUser().equals(authUser)) {
			// Заменяем обложку этого видоса
			String thumbnailName = fileService.editThumbnail(
					video.getThumbnailName(), dto.getThumbnail());

			// Заполняем новыми данными
			video.setTitle(dto.getTitle());
			video.setDescription(dto.getDescription());
			video.setTags(dto.getTags());
			video.setVideoStatus(VideoStatus.valueOf(dto.getVideoStatus()));
			video.setThumbnailName(thumbnailName);

			// Иначе он не явл. автором, посему ошибка
		} else {
			throw new OurtubeException("Только автор видео может редактировать свои видео");
		}
		// Сохраняем видео
		return videoRepo.save(video);
	}

	public List<Video> findAll() {
		// Получаем всех видосов
		return videoRepo.findAll();
	}

	public Video findById(Long videoId) {
		// Ошибка если таковое видео не существует
		return videoRepo.findById(videoId).orElseThrow(() ->
				new VideoNotFoundException(videoId)
		);
	}

	//====================================

	@Transactional(readOnly = false)
	public Comment addComment(Long videoId, CommentReqDto dto, User user) {

		// Получаем видeo по id
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new VideoNotFoundException(videoId)
		);
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

	@Transactional(readOnly = false)
	public void deleteCommentById(Long videoId, Long commentId, User authUser) {
		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new VideoNotFoundException(videoId)
		);

		// Находим у это видоса коммент по id, иначе ошибка
		Comment comment = video.getComments().stream().filter(it ->
				it.getId().equals(commentId)).findFirst().orElseThrow(() ->
				new CommentNotFoundException(commentId)
		);
		// Проверяем является ли авторизованный юзер автором этого коммента
		if (comment.getUser().equals(authUser)) {
			// Если да, то удаляем этот коммент
			commentRepo.deleteById(commentId);
			// Иначе он не явл. автором, посему ошибка
		} else
			throw new OurtubeException("Только автор коммента может удалять свои комменты");


	}

	@Transactional(readOnly = false)
	public Comment editComment(
			long videoId, long commentId, CommentReqDto dto, User authUser) {
		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new VideoNotFoundException(videoId)
		);
		// Находим коммент по id, иначе ошибка
		Comment comment = video.getComments().stream().filter(it ->
				it.getId().equals(commentId)).findFirst().orElseThrow(() ->
				new CommentNotFoundException(commentId)
		);
		// Проверяем является ли авторизованный юзер автором этого коммента
		if (comment.getUser().equals(authUser)) {
			// Изменяем текущий коммент на основе dto
			comment.setContent(dto.getContent());
			// Иначе он не явл. автором, посему ошибка
		} else
			throw new OurtubeException("Только автор коммента может редактировать свои комменты");

		// Сохраняем коммент
		return commentRepo.save(comment);
	}

	public List<Comment> findAllVideoCommentsByVideoId(Long videoId) {
		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new VideoNotFoundException(videoId)
		);
		// Возвращаем его комменты
		return video.getComments();
	}

	//====================================

	@Transactional(readOnly = false)
	public String likeVideo(Long videoId, User user) {

		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new VideoNotFoundException(videoId)
		);
		// Результат для вывода
		StringBuilder result = new StringBuilder();

		// Получаем кол-во лайков и дизлайков у этого видоса
		long likesCount = video.getLikesCount();
		long dislikesCount = video.getDislikesCount();

		// Если в это видео авторизованный юзер не ставил лайк
		if (!userService.isLikedVideo(videoId, user.getId())) {

			// И если авторизованный юзер уже ставил дизлайк
			if (userService.isDislikedVideo(videoId, user.getId())) {
				// То убираем дизлайк у видео
				userService.removeVideosDislike(videoId, user.getId());

				// И декрементируем кол-во дизлайков у видео
				video.setDislikesCount(--dislikesCount);

				result.append(String.format("Дизлайк к видео %s убран\n", videoId));
			}
			// И тогда ставим лайк
			userService.likeVideo(video, user.getId());

			// И инкрементируем кол-во лайков у видео
			video.setLikesCount(++likesCount);

			// Результат
			result.append(String.format("Лайк к видео %s поставлен", videoId));

		} else {
			// Иначе юзер ставил лайк, тогда убираем лайк у видео
			userService.removeVideosLike(videoId, user.getId());

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

	@Transactional(readOnly = false)
	public String dislikeVideo(Long videoId, User user) {

		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new VideoNotFoundException(videoId)
		);
		// Результат для вывода
		StringBuilder result = new StringBuilder();

		// Получаем кол-во дизлайков и лайков у этого видоса
		long dislikesCount = video.getDislikesCount();
		long likesCount = video.getLikesCount();

		// Если в это видео авторизованный юзер не ставил дизлайк
		if (!userService.isDislikedVideo(videoId, user.getId())) {

			// И если авторизованный юзер уже ставил лайк
			if (userService.isLikedVideo(videoId, user.getId())) {
				// То убираем лайк у видео
				userService.removeVideosLike(videoId, user.getId());

				// И декрементируем кол-во лайков у видео
				video.setLikesCount(--likesCount);

				result.append(String.format("Лайк к видео %s убран\n", videoId));
			}
			// И тогда ставим дизлайк
			userService.dislikeVideo(video, user.getId());

			// И инкрементируем кол-во дизлайков у видео
			video.setDislikesCount(++dislikesCount);

			// Результат
			result.append(String.format("Дизлайк к видео %s поставлен", videoId));

		} else {
			// Иначе юзер ставил дизлайк, тогда убираем дизлайк у видео
			userService.removeVideosDislike(videoId, user.getId());

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

	@Transactional(readOnly = false)
	public String likeComment(long videoId, long commentId, User user) {

		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new VideoNotFoundException(videoId)
		);
		// Находим коммент по id, иначе ошибка
		Comment comment = video.getComments().stream().filter(it ->
				it.getId().equals(commentId)).findFirst().orElseThrow(() ->
				new CommentNotFoundException(commentId)
		);
		// Строка для вывода результата
		StringBuilder result = new StringBuilder();

		// Получаем кол-во лайков и дизлайков у этого коммента
		long likesCount = comment.getLikesCount();
		long dislikesCount = comment.getDislikesCount();

		// Если в этот коммент авторизованный юзер не ставил лайк
		if (!userService.isLikedComment(commentId, user.getId())) {

			// И если авторизованный юзер уже ставил дизлайк
			if (userService.isDislikedComment(commentId, user.getId())) {
				// То убираем дизлайк у коммента
				userService.removeCommentsDislike(commentId, user.getId());
				// И декрементируем кол-во дизлайков у коммента
				comment.setDislikesCount(--dislikesCount);

				result.append(String.format("Дизлайк к комменту %s убран\n", videoId));
			}
			// И тогда ставим лайк
			userService.likeComment(comment, user.getId());
			// И инкрементируем кол-во лайков у коммента
			comment.setLikesCount(++likesCount);

			// Результат
			result.append(String.format("Лайк к комменту %s поставлен", videoId));

		} else {
			// Иначе юзер ставил лайк, тогда убираем лайк у коммента
			userService.removeCommentsLike(commentId, user.getId());

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

	@Transactional(readOnly = false)
	public String dislikeComment(long videoId, long commentId, User user) {

		// Находим видео по id, иначе ошибка
		Video video = videoRepo.findById(videoId).orElseThrow(() ->
				new VideoNotFoundException(videoId)
		);
		// Находим коммент по id, иначе ошибка
		Comment comment = video.getComments().stream().filter(it -> it.getId().equals(commentId))
				.findFirst().orElseThrow(() ->
						new CommentNotFoundException(commentId)
				);
		// Строка для вывода результата
		StringBuilder result = new StringBuilder();

		// Получаем кол-во лайков и дизлайков у этого коммента
		long dislikesCount = comment.getDislikesCount();
		long likesCount = comment.getLikesCount();

		// Если в этот коммент авторизованный юзер не ставил дизлайк
		if (!userService.isDislikedComment(commentId, user.getId())) {

			// И если авторизованный юзер уже ставил лайк
			if (userService.isLikedComment(commentId, user.getId())) {
				// То убираем лайк у коммента
				userService.removeCommentsLike(commentId, user.getId());

				// И декрементируем кол-во лайков у коммента
				comment.setLikesCount(--likesCount);

				// Результат
				result.append(String.format("Лайк к комменту %s убран\n", videoId));
			}
			// И тогда ставим дизлайк
			userService.dislikeComment(comment, user.getId());

			// И инкрементируем кол-во дизлайков у коммента
			comment.setDislikesCount(++dislikesCount);

			// Результат
			result.append(String.format("Дизлайк к комменту %s поставлен", videoId));

		} else {
			// Иначе юзер ставил дизлайк, тогда убираем дизлайк у коммента
			userService.removeCommentsDislike(commentId, user.getId());

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
