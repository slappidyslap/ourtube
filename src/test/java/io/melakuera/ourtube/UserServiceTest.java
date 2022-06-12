package io.melakuera.ourtube;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.melakuera.ourtube.dto.UserRegisterReqDto;
import io.melakuera.ourtube.entity.User;
import io.melakuera.ourtube.repo.UserRepo;
import io.melakuera.ourtube.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@PropertySource("classpath:application-test.yml")
public class UserServiceTest {
	
	@Autowired
	private MockMvc mvc;
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepo userRepo;
	
	@BeforeEach
	void resetDb() {
		userRepo.deleteAll();
	}
	
	@Test
	void shouldRegisterNewUser() throws Exception {
		
		var dto = new UserRegisterReqDto();
		dto.setEmail("test@test.com");
		dto.setUsername("test");
		dto.setPassword("test");
		
		userService.registerNewUser(dto);
		
		var exceptedUser = userRepo.findByUsername("test");
		assertThat(exceptedUser.get().getEmail()).isEqualTo("test@test.com");
		assertThat(exceptedUser.get().getUsername()).isEqualTo("test");
		assertThat(exceptedUser.get().getPassword(), startsWith("$2a"));
	}
	
	@Test
	void shouldNotRegisterIfUsernameOrEmailIsExists() throws Exception {
		
		var mockUserRepo = Mockito.mock(UserRepo.class);
		
		var dto = new UserRegisterReqDto();
		dto.setEmail("test@test.com");
		dto.setUsername("test");
		dto.setPassword("test");
		String reqJson = new ObjectMapper().writeValueAsString(dto);
		
		when(mockUserRepo.findByEmail("test@test.com")).thenReturn(Optional.of(new User()));
		when(mockUserRepo.findByUsername("test")).thenReturn(Optional.of(new User()));
		
		mvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(reqJson))
		.andDo(print())
		.andExpect(status().is4xxClientError());
	}
	
}
