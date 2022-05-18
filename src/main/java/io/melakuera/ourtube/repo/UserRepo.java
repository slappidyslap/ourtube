package io.melakuera.ourtube.repo;

import io.melakuera.ourtube.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	@Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.likedComments WHERE u.id = :id")
	User findUserByIdFetchLikedComments(@Param("id") long id);

	@Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.dislikedComments WHERE u.id = :id")
	User findUserByIdFetchDislikedComments(@Param("id") long id);

	@Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.likedVideos WHERE u.id = :id")
	User findUserByIdFetchLikedVideos(@Param("id") long id);

	@Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.dislikedVideos WHERE u.id = :id")
	User findUserByIdFetchDislikedVideos(@Param("id") long id);

	@Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.subscriptions WHERE u.id = :id")
	User findUserByIdFetchSubscriptions(@Param("id") long id);
}
