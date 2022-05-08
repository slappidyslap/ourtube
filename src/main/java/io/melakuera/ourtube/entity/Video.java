package io.melakuera.ourtube.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
	@Column(nullable = false)
	private String title;
	private String description;
	@Column(nullable = false)
	private String videoName;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	private Long likesCount = 0L;
	private Long dislikesCount = 0L;
	private Long viewCount = 0L;
	@ElementCollection
	private Set<String> tags;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private VideoStatus videoStatus;
	@Column(nullable = false)
	private String thumbnailName;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "video")
	@ToString.Exclude
	private List<Comment> comments;

	@JsonManagedReference
	public List<Comment> getComments() {
		return comments;
	}

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