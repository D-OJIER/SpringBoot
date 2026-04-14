package com.example.demo;

import com.example.demo.controller.BookController;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class WebLayerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookRepository bookRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void whenGetAllBooks_thenReturnJsonArray() throws Exception {
        Book book1 = new Book();
        book1.setTitle("JUnit Mastery");
        book1.setAuthor("Jane Doe");

        Mockito.when(bookRepository.findAll()).thenReturn(Arrays.asList(book1));

        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("JUnit Mastery"))
                .andExpect(jsonPath("$[0].author").value("Jane Doe"));
    }

    @Test
    public void whenGetBookById_thenReturnBook() throws Exception {
        Book book = new Book();
        book.setTitle("Spring Security");
        book.setAuthor("John Smith");

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Security"));
    }

    @Test
    public void whenGetBookByInvalidId_thenReturn404() throws Exception {
        Mockito.when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }

    @Test
    public void whenCreateBook_thenReturnCreatedBook() throws Exception {
        Book newBook = new Book();
        newBook.setTitle("Docker for Java");
        newBook.setAuthor("Alice");

        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(newBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Docker for Java"));
    }
}