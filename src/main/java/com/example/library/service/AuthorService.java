package com.example.library.service;

import com.example.library.entity.Author;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public List<Author> findAll() {
        return authorRepository.findAll(Sort.by("fullName"));
    }

    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("نویسنده پیدا نشد."));
    }

    public Author save(Author author) {
        return authorRepository.save(author);
    }

    public void delete(Long id) {
        if (bookRepository.countByAuthorId(id) > 0) {
            throw new IllegalStateException("این نویسنده به یک یا چند کتاب متصل است و قابل حذف نیست.");
        }
        authorRepository.deleteById(id);
    }
}
