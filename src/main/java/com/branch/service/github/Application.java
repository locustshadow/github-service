package com.branch.service.github;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
// NOTE: if we wanted caching, we can enable it here (probably would want to configure a different cache provider though, like Caffeine or Ehcache)
// @EnableCaching
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
