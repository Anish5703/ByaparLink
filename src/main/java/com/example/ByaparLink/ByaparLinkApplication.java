package com.example.ByaparLink;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
public class ByaparLinkApplication {

	@Value("${spring.mail.username")
	private static String mailUsername;

	public static void main(String[] args) {

		System.out.println("Spring_MailUsername retrieved : "+mailUsername);
		SpringApplication.run(ByaparLinkApplication.class, args);

	}

}
