package io.melakuera.ourtube.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String jwt = request.getHeader("Authorization");

		try {
			if (jwt != null && jwtService.isJwtValid(jwt)) {

				Authentication authentication = jwtService.getAuthentication(jwt);

				if (authentication != null) {

					SecurityContextHolder.getContext().setAuthentication(authentication);
				} /*else {
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					Map<String, Object> body = new LinkedHashMap<>();

					body.put("timestamp", LocalDateTime.now().format(
							DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")));
					body.put("status", HttpStatus.BAD_REQUEST.value());
					body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
					body.put("message", "Данный JWT невалидный");
					body.put("path", request.getRequestURI());

					ObjectMapper mapper = new ObjectMapper();
					mapper.writeValue(response.getOutputStream(), body);
				}*/
			}
		} catch (AuthenticationException e) {
			SecurityContextHolder.clearContext();
		}
		filterChain.doFilter(request, response);
	}
}
