package com.blog.services;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.blog.services.**.mappers")
public class BlogUserServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogUserServicesApplication.class, args);
	}

}
