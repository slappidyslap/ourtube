package io.melakuera.ourtube.config;

import io.melakuera.ourtube.entity.Role;
import io.melakuera.ourtube.security.AuthEntryPointJwt;
import io.melakuera.ourtube.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final JwtFilter jwtFilter;
	private final AuthEntryPointJwt authEntryPointJwt;

	public WebSecurityConfig(JwtFilter jwtFilter, AuthEntryPointJwt authEntryPointJwt) {
		this.jwtFilter = jwtFilter;
		this.authEntryPointJwt = authEntryPointJwt;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.cors()
				.and()
				.csrf().disable()
				.exceptionHandling().authenticationEntryPoint(authEntryPointJwt)
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.formLogin().loginPage("/login").permitAll()
				.and()
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/videos/**").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
				.antMatchers(HttpMethod.DELETE, "/api/videos/**").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
				.antMatchers(HttpMethod.PATCH, "/api/videos/**").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
				.antMatchers(HttpMethod.PATCH, "/api/videos/**").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
				.antMatchers(HttpMethod.GET, "/api/users").hasAuthority(Role.ADMIN.name())
				.antMatchers("/", "/register", "/api/users/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
				.anyRequest().authenticated();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}

