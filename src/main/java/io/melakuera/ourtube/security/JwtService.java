package io.melakuera.ourtube.security;

import io.jsonwebtoken.*;
import io.melakuera.ourtube.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

@Service
@Slf4j
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;
	@Value("${jwt.durationSec}")
	private long jwtDurationSec;

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
		Date issueAt = new Date(currentTimeMillis());
		Date expiration = new Date(issueAt.getTime() + jwtDurationSec * 1000);

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(issueAt)
				.setExpiration(expiration)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}

	public boolean isJwtValid(String jwt) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
			return true;
		} catch (SignatureException | MalformedJwtException | ExpiredJwtException |
				UnsupportedJwtException | IllegalArgumentException e) {
			log.warn("Resolved ["+e.getClass().toString().substring(6)+": "+e.getMessage()+"]");
			return false;
		}
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
}
