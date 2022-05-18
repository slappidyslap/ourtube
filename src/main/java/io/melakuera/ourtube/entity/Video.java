package io.melakuera.ourtube.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Video {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private Long id;
	@Column(nullable = false)
	private String title;
	private String description;

	@Column(nullable = false)
	@Lob
	private byte[] video;
	@Column(nullable = false)
	private String videoName;
	@Column(nullable = false)
	private long videoSize;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User author;
	private long likesCount = 0;
	private long dislikesCount = 0;
	private long viewCount = 0;
	@ElementCollection
	private Set<String> tags;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private VideoStatus videoStatus;

	@Column(nullable = false)
	@Lob
	private byte[] thumbnail;
	@Column(nullable = false)
	private String thumbnailName;
	@Column(nullable = false)
	private String thumbnailSize;
	
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
