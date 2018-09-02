package com.queue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Jsr330ScopeMetadataResolver;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

import com.queue.config.AppConfig;
import com.queue.config.EncodingFilter;

@SpringBootApplication
@EnableScheduling
@RestController
@EnableAsync
@ComponentScan(
	scopeResolver = Jsr330ScopeMetadataResolver.class
)
@Import(AppConfig.class)
@EnableCaching
@EnableAutoConfiguration
public class TaskManagementApplication extends SpringBootServletInitializer {
	
	@Bean
	EncodingFilter EncodingFilter() {
		return new EncodingFilter();
	}
	
	/**
	 * configファイルを設定する
	 * @param args
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TaskManagementApplication.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(TaskManagementApplication.class, args);
		System.out.println("deploydone.");
	}
}