package io.melakuera.ourtube.service;

import io.melakuera.ourtube.dto.UserRegisterReqDto;
import io.melakuera.ourtube.entity.Role;
import io.melakuera.ourtube.entity.User;
import io.melakuera.ourtube.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

	private final UserRepo userRepo;
	private final BCryptPasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String loginFromForm) throws UsernameNotFoundException {
		// Если строка из формы валидна формату email
		if (loginFromForm.matches("\\w+@[a-z]+\\.[a-z]+")) {
			return userRepo.findByEmail(loginFromForm)
					.orElseThrow(() -> new UsernameNotFoundException(
							String.format("Пользователь с данной " +
									"%s эл. почтой не найден", loginFromForm)));
		} else {
			return userRepo.findByUsername(loginFromForm)
					.orElseThrow(() -> new UsernameNotFoundException(
							String.format("Пользователь с данной " +
									"%s имя пользователя не найден", loginFromForm)));
		}
	}

	public ResponseEntity<User> registerNewUser(UserRegisterReqDto dto) {

		if (userRepo.findByEmail(dto.getEmail()).isPresent() ||
				userRepo.findByUsername(dto.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Такой юзер уже существует");
		}
		User registeredUser = new User();
		registeredUser.setUsername(dto.getUsername());
		registeredUser.setEmail(dto.getEmail());
		registeredUser.setRole(Role.USER);
		registeredUser.setPassword(passwordEncoder.encode(dto.getPassword()));

		userRepo.save(registeredUser);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", "http://localhost:8080/");

		return new ResponseEntity<>(registeredUser, headers, HttpStatus.CREATED);
	}
}

