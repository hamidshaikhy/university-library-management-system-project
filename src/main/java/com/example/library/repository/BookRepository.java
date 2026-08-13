package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCaseOrderByIdDesc(String title);

    List<Book> findByCategoryIdOrderByIdDesc(Long categoryId);

    List<Book> findByTitleContainingIgnoreCaseAndCategoryIdOrderByIdDesc(String title, Long categoryId);

    List<Book> findTop4ByOrderByIdDesc();

    Optional<Book> findTopByOrderByIdAsc();

    Optional<Book> findByIsbnIgnoreCase(String isbn);

    long countByAuthorId(Long authorId);

    long countByCategoryId(Long categoryId);
}
