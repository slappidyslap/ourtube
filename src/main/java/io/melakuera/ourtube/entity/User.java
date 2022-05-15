package io.melakuera.ourtube.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "usr")
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String email;
	@JsonIgnore
	private String password;
	@Enumerated(EnumType.STRING)
	private Role role;
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "subscriber_subscription",
			joinColumns = @JoinColumn(name = "subscriber_id"),
			inverseJoinColumns = @JoinColumn(name = "subscription_id"))
	@ToString.Exclude
	private List<User> subscriptions = new ArrayList<>();
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "subscriber_subscription",
			joinColumns = @JoinColumn(name = "subscription_id"),
			inverseJoinColumns = @JoinColumn(name = "subscriber_id"))
	@ToString.Exclude
	private List<User> subscribers = new ArrayList<>();
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@ToString.Exclude
	private List<Video> likedVideos = new ArrayList<>();
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@ToString.Exclude
	private List<Video> dislikedVideos = new ArrayList<>();
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@ToString.Exclude
	private List<Video> viewedVideos = new ArrayList<>();
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@ToString.Exclude
	private List<Comment> likedComments = new ArrayList<>();
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@ToString.Exclude
	private List<Comment> dislikedComments = new ArrayList<>();

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(
				new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
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