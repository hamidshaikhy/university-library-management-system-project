package com.example.library.repository;

import com.example.library.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    List<Borrow> findByUserIdOrderByBorrowDateDesc(Long userId);

    List<Borrow> findByReturnedFalseOrderByBorrowDateDesc();

    long countByReturnedFalse();

    long countByUserIdAndReturnedFalse(Long userId);

    long countByBookId(Long bookId);

    boolean existsByUserIdAndBookIdAndReturnedFalse(Long userId, Long bookId);

    List<Borrow> findByReturnedFalseAndDueDateBefore(LocalDate date);
}
