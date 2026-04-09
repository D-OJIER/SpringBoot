package com.example.demo;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(BookRepository bookRepository) {
		return args -> {
			// Create the first dummy book
			Book book1 = new Book();
			book1.setTitle("The Spring Boot Guide");
			book1.setAuthor("Jane Doe");
			bookRepository.save(book1);
			// Create a second dummy book
			Book book2 = new Book();
			book2.setTitle("Mastering Java");
			book2.setAuthor("John Smith");
			bookRepository.save(book2);

			System.out.println("✅ Dummy data successfully loaded into the H2 database!");
		};
	}
}
