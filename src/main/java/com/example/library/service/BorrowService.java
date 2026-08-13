package com.example.library.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.library.entity.Book;
import com.example.library.entity.Borrow;
import com.example.library.entity.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRepository;
import com.example.library.repository.UserRepository;

@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BorrowService(BorrowRepository borrowRepository,
            BookRepository bookRepository,
            UserRepository userRepository) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Borrow> findAll() {
        return borrowRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Borrow> findByUserId(Long userId) {
        return borrowRepository.findByUserIdOrderByBorrowDateDesc(userId);
    }

    public List<Borrow> findActive() {
        return borrowRepository.findByReturnedFalseOrderByBorrowDateDesc();
    }

    @Transactional
    public Borrow create(Long userId, Long bookId, LocalDate dueDate) {
        if (dueDate == null || dueDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException("تاریخ سررسید باید امروز یا بعد از امروز باشد.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("کاربر پیدا نشد."));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("کتاب پیدا نشد."));

        if (book.getQuantity() == null || book.getQuantity() <= 0) {
            throw new IllegalStateException("این کتاب در حال حاضر موجود نیست.");
        }
        if (borrowRepository.existsByUserIdAndBookIdAndReturnedFalse(userId, bookId)) {
            throw new IllegalStateException("این کتاب قبلاً به همین کاربر امانت داده شده است.");
        }

        // با ثبت امانت، موجودی کتاب یک واحد کم می‌شود.
        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);

        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.now());
        borrow.setDueDate(dueDate);
        borrow.setReturned(false);
        return borrowRepository.save(borrow);
    }

    @Transactional
    public void returnBook(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new IllegalArgumentException("امانت پیدا نشد."));

        if (borrow.isReturned()) {
            throw new IllegalStateException("این کتاب قبلاً بازگردانده شده است.");
        }

        borrow.setReturned(true);
        borrow.setReturnDate(LocalDate.now());

        // با بازگشت کتاب، موجودی دوباره افزایش پیدا می‌کند.
        Book book = borrow.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepository.save(book);
        borrowRepository.save(borrow);
    }

    public long countActive() {
        return borrowRepository.countByReturnedFalse();
    }

    public long countActiveByUser(Long userId) {
        return borrowRepository.countByUserIdAndReturnedFalse(userId);
    }
}
