package io.melakuera.ourtube;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.melakuera.ourtube.entity.User;
import io.melakuera.ourtube.entity.Video;
import io.melakuera.ourtube.entity.VideoStatus;
import io.melakuera.ourtube.repo.VideoRepo;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@PropertySource("classpath:application-test.yml")
public class VideoControllerTest {
	
	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;
	@MockBean
	private VideoRepo mockVideoRepo;
	
	@Test
	void shouldFindVideoById() throws Exception {
		
		Video actualVideo = new Video();
		actualVideo.setId(41L);
		actualVideo.setTitle("123");

		when(mockVideoRepo.findById(41L)).thenReturn(Optional.of(actualVideo));
		
		mvc.perform(get("/api/videos/41")
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title", is("123")));
	}
	
	@Test
	void shouldGetUnauth() throws Exception {
		
		mvc.perform(post("/api/videos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(new Video())))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	@Sql(value = "classpath:existedUser.sql")
	void shouldAddNewVideo() throws Exception {
			
		String jwt = getJwtUserWithRoleUser();
		
		var video = new Video();
		video.setVideoStatus(VideoStatus.PUBLIC);
		
		mvc.perform(post("/api/videos")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", jwt)
				.content(new ObjectMapper().writeValueAsString(video)))
		.andDo(print())
		.andExpect(status().isCreated());
	}

	String getJwtUserWithRoleUser() throws Exception {
		
		MvcResult res = mvc.perform(post("/api/users/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"login\": \"user\", \"password\": \"test\"}"))
		.andExpect(status().isOk()).andReturn();
		
		String resJson = res.getResponse().getContentAsString();
		
		// https://www.baeldung.com/jackson-map
		TypeReference<HashMap<String, Object>> typeRef = new 
				TypeReference<HashMap<String, Object>>() {};
		Map<String, Object> map = om.readValue(resJson, typeRef);
		String jwt = (String) map.get("jwt");
		
		return jwt;
	}
	
	String getJwtUserWithRoleAdmin() throws Exception {
		
		MvcResult res = mvc.perform(post("/api/users/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"login\": \"admin\", \"password\": \"test\"}"))
		.andExpect(status().isOk()).andReturn();
		
		String resJson = res.getResponse().getContentAsString();
		
		// https://www.baeldung.com/jackson-map
		TypeReference<HashMap<String, Object>> typeRef = new 
				TypeReference<HashMap<String, Object>>() {};
		Map<String, Object> map = om.readValue(resJson, typeRef);
		String jwt = (String) map.get("jwt");
		
		return jwt;
	}
	
}
