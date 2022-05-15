package io.melakuera.ourtube.service;

import io.melakuera.ourtube.dto.AuthenticationReqDto;
import io.melakuera.ourtube.dto.UserRegisterReqDto;
import io.melakuera.ourtube.entity.Role;
import io.melakuera.ourtube.entity.User;
import io.melakuera.ourtube.exception.UserAlreadyExistsException;
import io.melakuera.ourtube.repo.UserRepo;
import io.melakuera.ourtube.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

	private final UserRepo userRepo;
	private final BCryptPasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

		return extractUserByLoginForm(login);
	}

	public ResponseEntity<User> registerNewUser(UserRegisterReqDto dto) {

		if (userRepo.findByEmail(dto.getEmail()).isPresent() ||
				userRepo.findByUsername(dto.getUsername()).isPresent()) {
			throw new UserAlreadyExistsException();
		}
		User registeredUser = new User();
		registeredUser.setUsername(dto.getUsername());
		if (dto.getUsername().equals("Eld"))
			registeredUser.setRole(Role.ADMIN);
		else
			registeredUser.setRole(Role.USER);
		registeredUser.setEmail(dto.getEmail());
		registeredUser.setPassword(passwordEncoder.encode(dto.getPassword()));

		userRepo.save(registeredUser);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", "http://localhost:8080/");

		return new ResponseEntity<>(registeredUser, headers, HttpStatus.CREATED);
	}

	public ResponseEntity<?> authUser(AuthenticationReqDto dto) {

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						dto.getLogin(),
						dto.getPassword()
				)
		);

		String jwt = jwtService.generateJwt(dto.getLogin());
		User user = extractUserByLoginForm(dto.getLogin());

		Map<String, Object> body = new HashMap<>();
		body.put("jwt", jwt);
		body.put("user", user);

		return ResponseEntity.ok(body);
	}

	public User extractUserByLoginForm(String login) {
		// Если строка из формы валидна формату email
		if (login.matches("\\w+@[a-z]+\\.[a-z]+")) {
			return userRepo.findByEmail(login)
					.orElseThrow(() -> new UsernameNotFoundException(
							String.format("Пользователь с данной " +
									"%s эл. почтой не найден", login)));
		} else {
			return userRepo.findByUsername(login)
					.orElseThrow(() -> new UsernameNotFoundException(
							String.format("Пользователь с данной " +
									"%s имя пользователя не найден", login)));
		}
	}

	public List<User> findAll() {
		return userRepo.findAll();
	}
}

