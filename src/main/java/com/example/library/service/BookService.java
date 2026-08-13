package com.example.library.service;

import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRepository;
import com.example.library.repository.ReservationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;
    private final ReservationRepository reservationRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    public BookService(BookRepository bookRepository,
                       BorrowRepository borrowRepository,
                       ReservationRepository reservationRepository,
                       AuthorService authorService,
                       CategoryService categoryService) {
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
        this.reservationRepository = reservationRepository;
        this.authorService = authorService;
        this.categoryService = categoryService;
    }

    public List<Book> findAll(String query, Long categoryId) {
        boolean hasQuery = query != null && !query.isBlank();
        boolean hasCategory = categoryId != null;

        if (hasQuery && hasCategory) {
            return bookRepository.findByTitleContainingIgnoreCaseAndCategoryIdOrderByIdDesc(query.trim(), categoryId);
        }
        if (hasQuery) {
            return bookRepository.findByTitleContainingIgnoreCaseOrderByIdDesc(query.trim());
        }
        if (hasCategory) {
            return bookRepository.findByCategoryIdOrderByIdDesc(categoryId);
        }
        return bookRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Book> findLatestBooks() {
        return bookRepository.findTop4ByOrderByIdDesc();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("کتاب پیدا نشد."));
    }

    public boolean isIsbnDuplicate(String isbn, Long currentBookId) {
        return bookRepository.findByIsbnIgnoreCase(isbn)
                .map(book -> currentBookId == null || !book.getId().equals(currentBookId))
                .orElse(false);
    }

    @Transactional
    public Book save(Book book, Long authorId, Long categoryId) {
        Author author = authorService.findById(authorId);
        Category category = categoryService.findById(categoryId);
        book.setAuthor(author);
        book.setCategory(category);
        book.setIsbn(book.getIsbn().trim());
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        if (borrowRepository.countByBookId(id) > 0 || reservationRepository.countByBookId(id) > 0) {
            throw new IllegalStateException("این کتاب سابقه امانت یا رزرو دارد و قابل حذف نیست.");
        }
        bookRepository.deleteById(id);
    }

    public long count() {
        return bookRepository.count();
    }
}
