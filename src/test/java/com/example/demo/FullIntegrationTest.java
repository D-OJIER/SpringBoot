package com.example.demo;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FullIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        bookRepository.deleteAll();
    }

    @Test
    public void whenCreateBook_thenItIsSavedInDatabase() throws Exception {
        Book newBook = new Book();
        newBook.setTitle("Mastering Spring Boot");
        newBook.setAuthor("Jane Doe");

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Mastering Spring Boot"));

        assertEquals(1, bookRepository.count());
    }

    @Test
    public void whenGetAllBooks_thenReturnJsonArray() throws Exception {
        Book book = new Book();
        book.setTitle("Integration Testing Guide");
        book.setAuthor("Test Author");
        bookRepository.save(book);

        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Integration Testing Guide"));
    }

    @Test
    public void whenGetInvalidBookId_thenReturn404() throws Exception {
        mockMvc.perform(get("/api/books/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }

    @Test
    public void whenDeleteBook_thenItIsRemovedFromDatabase() throws Exception {
        Book book = new Book();
        book.setTitle("To Be Deleted");
        book.setAuthor("Author");
        Book savedBook = bookRepository.save(book);

        mockMvc.perform(delete("/api/books/" + savedBook.getId()))
                .andExpect(status().isOk());

        assertEquals(0, bookRepository.count());
    }
}