package io.melakuera.ourtube.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "usr")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;
	private String username;
	private String email;
	@ManyToMany
	@JoinTable(name = "subscriber_subscription",
			joinColumns = @JoinColumn(name = "subscriber_id"),
			inverseJoinColumns = @JoinColumn(name = "subscription_id"))
	@ToString.Exclude
	private List<User> subscriptions;
	@ManyToMany
	@JoinTable(name = "subscriber_subscription",
			joinColumns = @JoinColumn(name = "subscription_id"),
			inverseJoinColumns = @JoinColumn(name = "subscriber_id"))
	@ToString.Exclude
	private List<User> subscribers;
	@ManyToMany
	@ToString.Exclude
	private List<Video> likedVideos = new ArrayList<>();
	@ManyToMany
	@ToString.Exclude
	private List<Video> dislikedVideos = new ArrayList<>();
	@ManyToMany
	@ToString.Exclude
	private List<Video> viewedVideos;
	@ManyToMany
	@ToString.Exclude
	private List<Comment> likedComment = new ArrayList<>();
	@OneToMany(cascade = {
			CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.PERSIST,CascadeType.REFRESH})
	@ToString.Exclude
	private List<Comment> dislikedComment = new ArrayList<>();

	public boolean isLikedVideo(long videoId) {
		return likedVideos.stream().anyMatch(it ->
				it.getId().equals(videoId));
	}

	public boolean isDislikedVideo(long videoId) {
		return dislikedVideos.stream().anyMatch(it ->
				it.getId().equals(videoId));
	}

	public void removeVideosDislike(Long videoId) {
		dislikedVideos.removeIf(it -> it.getId().equals(videoId));
	}

	public void removeVideosLike(Long videoId) {
		likedVideos.removeIf(it -> it.getId().equals(videoId));
	}

	public void likeToVideo(Video video) {
		likedVideos.add(video);
	}

	public void disLikeToVideo(Video video) {
		dislikedVideos.add(video);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		User user = (User) o;
		return id != null && Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}