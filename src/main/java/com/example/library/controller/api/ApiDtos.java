package com.example.library.controller.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class ApiDtos {

    private ApiDtos() {
    }

    public record UserResponse(Long id, String fullName, String email, String role) {
    }

    public record AuthorResponse(Long id, String fullName, String biography) {
    }

    public record CategoryResponse(Long id, String title, String description) {
    }

    public record BookResponse(
            Long id,
            String title,
            String isbn,
            Integer publishYear,
            Integer quantity,
            String description,
            String coverUrl,
            AuthorResponse author,
            CategoryResponse category
    ) {
    }

    public record BorrowResponse(
            Long id,
            LocalDate borrowDate,
            LocalDate dueDate,
            LocalDate returnDate,
            boolean returned,
            UserResponse user,
            BookResponse book
    ) {
    }

    public record ReservationResponse(
            Long id,
            LocalDateTime reservationDate,
            String status,
            UserResponse user,
            BookResponse book
    ) {
    }

    public record AuthResponse(boolean authenticated, UserResponse user) {
    }

    public record BooksResponse(List<BookResponse> books, List<CategoryResponse> categories) {
    }

    public record DashboardResponse(
            long activeBorrowCount,
            long reservationCount,
            List<BookResponse> latestBooks
    ) {
    }

    public record AdminDashboardResponse(
            long bookCount,
            long userCount,
            long activeBorrowCount,
            long pendingReservationCount
    ) {
    }

    public record AdminBooksResponse(
            List<BookResponse> books,
            List<AuthorResponse> authors,
            List<CategoryResponse> categories
    ) {
    }

    public record AdminBorrowsResponse(
            List<BorrowResponse> borrows,
            List<UserResponse> users,
            List<BookResponse> books
    ) {
    }

    public record MessageResponse(String message) {
    }

    public record ErrorResponse(String message, Map<String, String> fieldErrors) {
    }

    public record LoginRequest(
            @NotBlank(message = "ایمیل الزامی است.")
            @Email(message = "فرمت ایمیل صحیح نیست.")
            String email,
            @NotBlank(message = "رمز عبور الزامی است.")
            String password
    ) {
    }

    public record RegisterRequest(
            @NotBlank(message = "نام و نام خانوادگی الزامی است.")
            String fullName,
            @NotBlank(message = "ایمیل الزامی است.")
            @Email(message = "فرمت ایمیل صحیح نیست.")
            String email,
            @NotBlank(message = "رمز عبور الزامی است.")
            @Size(min = 6, message = "رمز عبور باید حداقل ۶ کاراکتر باشد.")
            String password
    ) {
    }

    public record AuthorRequest(
            @NotBlank(message = "نام نویسنده الزامی است.")
            String fullName,
            String biography
    ) {
    }

    public record CategoryRequest(
            @NotBlank(message = "عنوان دسته‌بندی الزامی است.")
            String title,
            String description
    ) {
    }

    public record BookRequest(
            @NotBlank(message = "عنوان کتاب الزامی است.")
            String title,
            @NotBlank(message = "ISBN الزامی است.")
            String isbn,
            Integer publishYear,
            @NotNull(message = "تعداد کتاب الزامی است.")
            @Min(value = 0, message = "تعداد کتاب نمی‌تواند منفی باشد.")
            Integer quantity,
            String description,
            String coverUrl,
            @NotNull(message = "نویسنده را انتخاب کنید.")
            Long authorId,
            @NotNull(message = "دسته‌بندی را انتخاب کنید.")
            Long categoryId
    ) {
    }

    public record BorrowRequest(
            @NotNull(message = "کاربر را انتخاب کنید.")
            Long userId,
            @NotNull(message = "کتاب را انتخاب کنید.")
            Long bookId,
            @NotNull(message = "تاریخ سررسید الزامی است.")
            LocalDate dueDate
    ) {
    }
}
