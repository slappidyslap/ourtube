package io.melakuera.ourtube.controller;

import io.melakuera.ourtube.dto.AuthenticationReqDto;
import io.melakuera.ourtube.dto.UserRegisterReqDto;
import io.melakuera.ourtube.entity.User;
import io.melakuera.ourtube.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	ResponseEntity<User> registerUser(@RequestBody UserRegisterReqDto dto) {
		return userService.registerNewUser(dto);
	}

	@PostMapping("/auth")
	ResponseEntity<?> authUser(@RequestBody AuthenticationReqDto dto) {
		return userService.authUser(dto);
	}

	@GetMapping
	List<User> getAllUsers() {
		return userService.findAll();
	}

	@PostMapping("/{userId}/subscribe")
	String subscribeToUser(@PathVariable long userId, @AuthenticationPrincipal User authUser) {
		return userService.subscribe(userId, authUser);
	}

	@GetMapping("/{userId}")
	User getOne(@PathVariable long userId) {
		return userService.findById(userId);
	}
}
