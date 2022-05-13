package io.melakuera.ourtube.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Value("${spring.servlet.multipart.location}")
	private String pathToVideo;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/video/**").addResourceLocations("file:///"+pathToVideo);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/register").setViewName("register");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/videos").setViewName("videos");
	}
}
