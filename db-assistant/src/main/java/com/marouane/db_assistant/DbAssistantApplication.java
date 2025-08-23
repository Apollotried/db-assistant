package com.marouane.db_assistant;

import com.marouane.db_assistant.role.Role;
import com.marouane.db_assistant.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = org.springframework.ai.model.ollama.autoconfigure.OllamaChatAutoConfiguration.class)
public class DbAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbAssistantApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByName("USER").isEmpty()) {
				roleRepository.save(Role.builder().name("USER").build());
			}
		};
	}

}
