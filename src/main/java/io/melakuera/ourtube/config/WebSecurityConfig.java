package io.melakuera.ourtube.config;

import io.melakuera.ourtube.security.AuthEntryPointJwt;
import io.melakuera.ourtube.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
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
				.exceptionHandling().authenticationEntryPoint(authEntryPointJwt)
				.and()
				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/", "/register").permitAll()
				.antMatchers("/api/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.formLogin().loginPage("/login").permitAll()
				.and().addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
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

