package com.example.library.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.library.entity.Book;
import com.example.library.entity.Borrow;
import com.example.library.entity.Reservation;
import com.example.library.entity.ReservationStatus;
import com.example.library.entity.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRepository;
import com.example.library.repository.ReservationRepository;
import com.example.library.repository.UserRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;

    public ReservationService(ReservationRepository reservationRepository,
            UserRepository userRepository,
            BookRepository bookRepository,
            BorrowRepository borrowRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Reservation> findByUserId(Long userId) {
        return reservationRepository.findByUserIdOrderByReservationDateDesc(userId);
    }

    @Transactional
    public Reservation create(Long userId, Long bookId) {
        // از ثبت چند رزرو فعال برای یک کتاب جلوگیری می‌شود.
        if (reservationRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, ReservationStatus.PENDING)) {
            throw new IllegalStateException("برای این کتاب یک رزرو در انتظار دارید.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("کاربر پیدا نشد."));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("کتاب پیدا نشد."));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void cancel(Long reservationId, Long currentUserId) {
        Reservation reservation = findById(reservationId);

        if (!reservation.getUser().getId().equals(currentUserId)) {
            throw new IllegalStateException("این رزرو متعلق به شما نیست.");
        }
        changePendingStatus(reservation, ReservationStatus.CANCELED);
    }

    @Transactional
    public void approve(Long reservationId) {
        Reservation reservation = findById(reservationId);

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("فقط رزرو در انتظار قابل تأیید است.");
        }

        User user = reservation.getUser();
        Book book = reservation.getBook();

        if (book.getQuantity() == null || book.getQuantity() <= 0) {
            throw new IllegalStateException("این کتاب در حال حاضر موجود نیست.");
        }

        if (borrowRepository.existsByUserIdAndBookIdAndReturnedFalse(user.getId(), book.getId())) {
            throw new IllegalStateException("این کتاب هم‌اکنون در امانت همین کاربر است.");
        }

        // با تأیید رزرو، یک امانت ۱۴ روزه ساخته و موجودی کتاب کم می‌شود.
        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.now());
        borrow.setDueDate(LocalDate.now().plusDays(14));
        borrow.setReturned(false);

        book.setQuantity(book.getQuantity() - 1);
        reservation.setStatus(ReservationStatus.APPROVED);

        bookRepository.save(book);
        borrowRepository.save(borrow);
        reservationRepository.save(reservation);
    }

    @Transactional
    public void reject(Long reservationId) {
        changePendingStatus(findById(reservationId), ReservationStatus.REJECTED);
    }

    private Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("رزرو پیدا نشد."));
    }

    private void changePendingStatus(Reservation reservation, ReservationStatus newStatus) {
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("فقط رزرو در انتظار قابل تغییر است.");
        }
        reservation.setStatus(newStatus);
        reservationRepository.save(reservation);
    }

    public long countPending() {
        return reservationRepository.countByStatus(ReservationStatus.PENDING);
    }

    public long countByUser(Long userId) {
        return reservationRepository.countByUserId(userId);
    }
}
