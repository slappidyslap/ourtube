package io.melakuera.ourtube.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "video_id")
	private Video video;
	private String content;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	private long likesCount;
	private long dislikesCount;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Comment comment = (Comment) o;
		return id != null && Objects.equals(id, comment.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}