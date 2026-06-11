package com.huseynovvusal.springblogapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableJpaAuditing
@EnableAspectJAutoProxy
@SpringBootApplication
@EnableCaching
public class SpringBlogApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBlogApiApplication.class, args);
  }
}
