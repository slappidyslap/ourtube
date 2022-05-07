package io.melakuera.ourtube.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Video {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String description;
	private String fileName;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	private Long likesCount;
	private Long dislikesCount;
	private Long viewCount;
	@ElementCollection
	private Set<String> tags;
	@Enumerated(EnumType.STRING)
	private VideoStatus videoStatus;
	private String thumbnailUrl;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "video")
	@ToString.Exclude
	private List<Comment> comments;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Video video = (Video) o;
		return id != null && Objects.equals(id, video.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
