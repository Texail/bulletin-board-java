package com.omiew.texail.bulletin_board_java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BulletinBoardJavaApplication {

	private static final Logger logger = LoggerFactory.getLogger(BulletinBoardJavaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BulletinBoardJavaApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(UserRepository repository) {
		return (args -> {
			repository.save(new User("Помпа"));
			repository.save(new User("Гидра"));

			logger.info("Customers found with findAll():");
			logger.info("-------------------------------");
			repository.findAll().forEach(user -> {
				logger.info(user.toString());
			});
			logger.info("");


		});
	}

}
