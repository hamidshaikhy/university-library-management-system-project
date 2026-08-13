package com.example.library.controller.api;

import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Borrow;
import com.example.library.entity.Category;
import com.example.library.entity.Reservation;
import com.example.library.entity.User;
import org.springframework.stereotype.Component;

import static com.example.library.controller.api.ApiDtos.*;

@Component
public class ApiMapper {

    public UserResponse toUser(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }

    public AuthorResponse toAuthor(Author author) {
        return new AuthorResponse(author.getId(), author.getFullName(), author.getBiography());
    }

    public CategoryResponse toCategory(Category category) {
        return new CategoryResponse(category.getId(), category.getTitle(), category.getDescription());
    }

    public BookResponse toBook(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishYear(),
                book.getQuantity(),
                book.getDescription(),
                book.getCoverUrl(),
                toAuthor(book.getAuthor()),
                toCategory(book.getCategory())
        );
    }

    public BorrowResponse toBorrow(Borrow borrow) {
        return new BorrowResponse(
                borrow.getId(),
                borrow.getBorrowDate(),
                borrow.getDueDate(),
                borrow.getReturnDate(),
                borrow.isReturned(),
                toUser(borrow.getUser()),
                toBook(borrow.getBook())
        );
    }

    public ReservationResponse toReservation(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getReservationDate(),
                reservation.getStatus().name(),
                toUser(reservation.getUser()),
                toBook(reservation.getBook())
        );
    }
}
