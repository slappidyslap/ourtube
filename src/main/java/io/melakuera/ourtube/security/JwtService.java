package io.melakuera.ourtube.security;

import io.jsonwebtoken.*;
import io.melakuera.ourtube.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.RequestEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;

	private final UserService userService;

	public JwtService(@Lazy UserService userService) {
		this.userService = userService;
	}

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(
				secretKey.getBytes(StandardCharsets.UTF_8));
	}

	public String generateJwt(String username) {
		Claims claims = Jwts.claims().setSubject(username);
		LocalDateTime issueAt = LocalDateTime.now();
		LocalDateTime expiration = issueAt.plusSeconds(15);

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(Date.from(issueAt.toInstant(ZoneOffset.UTC)))
				.setExpiration(Date.from(expiration.toInstant(ZoneOffset.UTC)))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}

	public boolean isJwtExpired(Jws<Claims> claims) {
		return claims.getBody().getExpiration().before(
				Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
	}

	public boolean validateJwt(String jwt) {
		Jws<Claims> claimsJws;
		try {
			claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtException("Jwt исчерпан");
		}
		return isJwtExpired(claimsJws);
	}

	public Authentication getAuthentication(String jwt) {
		UserDetails userDetails = userService.loadUserByUsername(getUsername(jwt));
		return new UsernamePasswordAuthenticationToken(
				userDetails, null, userDetails.getAuthorities());
	}

	public String getUsername(String jwt) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt)
				.getBody().getSubject();
	}

	public String resolveJwt(HttpServletRequest req) {
		return req.getHeader("Authorization");
	}
}
