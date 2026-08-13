package com.example.library.repository;

import com.example.library.entity.Reservation;
import com.example.library.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserIdOrderByReservationDateDesc(Long userId);

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReservationStatus status);

    long countByStatus(ReservationStatus status);

    long countByUserId(Long userId);

    long countByBookId(Long bookId);
}
