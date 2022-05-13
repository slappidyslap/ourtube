package io.melakuera.ourtube.controller;

import io.melakuera.ourtube.dto.AuthenticationReqDto;
import io.melakuera.ourtube.dto.UserRegisterReqDto;
import io.melakuera.ourtube.entity.User;
import io.melakuera.ourtube.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

	@PostMapping("/login")
	ResponseEntity<?> authUser(@RequestBody AuthenticationReqDto dto) {

		return userService.authUser(dto);
	}
}
