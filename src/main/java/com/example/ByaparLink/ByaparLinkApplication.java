package com.example.ByaparLink;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@OpenAPIDefinition(

		info = @Info(
				title = "ByaparLink API",
				version = "1.0",
				description = "API documentation for ByaparLink project",

				contact = @Contact(
						name = "API support team",
						email = "support@byaparlink.com",
						url = "https://byaparlink.com/support")
		),

				security = @SecurityRequirement(
						name = "bearerAuth"
				)


		)


@SpringBootApplication
public class ByaparLinkApplication {

	@Value("${spring.mail.username}")
	private static String mailUsername;

	public static void main(String[] args) {

		System.out.println("Spring_MailUsername retrieved : "+mailUsername);
		SpringApplication.run(ByaparLinkApplication.class, args);

	}

}
