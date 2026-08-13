package com.example.library.controller.api;

import com.example.library.service.BookService;
import com.example.library.service.BorrowService;
import com.example.library.service.CategoryService;
import com.example.library.service.ReservationService;
import com.example.library.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.library.controller.api.ApiDtos.*;

@RestController
@RequestMapping("/api")
public class LibraryApiController {

    private final UserService userService;
    private final BookService bookService;
    private final BorrowService borrowService;
    private final ReservationService reservationService;
    private final CategoryService categoryService;
    private final ApiMapper mapper;

    public LibraryApiController(UserService userService,
                                BookService bookService,
                                BorrowService borrowService,
                                ReservationService reservationService,
                                CategoryService categoryService,
                                ApiMapper mapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.borrowService = borrowService;
        this.reservationService = reservationService;
        this.categoryService = categoryService;
        this.mapper = mapper;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(HttpSession session) {
        Long userId = currentUserId(session);
        return new DashboardResponse(
                borrowService.countActiveByUser(userId),
                reservationService.countByUser(userId),
                bookService.findLatestBooks().stream().map(mapper::toBook).toList()
        );
    }

    @GetMapping("/profile")
    public UserResponse profile(HttpSession session) {
        return mapper.toUser(userService.findById(currentUserId(session)));
    }

    @GetMapping("/books")
    public BooksResponse books(@RequestParam(required = false) String q,
                               @RequestParam(required = false) Long categoryId) {
        return new BooksResponse(
                bookService.findAll(q, categoryId).stream().map(mapper::toBook).toList(),
                categoryService.findAll().stream().map(mapper::toCategory).toList()
        );
    }

    @GetMapping("/books/{id}")
    public BookResponse book(@PathVariable Long id) {
        return mapper.toBook(bookService.findById(id));
    }

    @PostMapping("/books/{id}/reservations")
    public ReservationResponse reserve(@PathVariable Long id, HttpSession session) {
        return mapper.toReservation(reservationService.create(currentUserId(session), id));
    }

    @GetMapping("/me/reservations")
    public List<ReservationResponse> reservations(HttpSession session) {
        return reservationService.findByUserId(currentUserId(session))
                .stream().map(mapper::toReservation).toList();
    }

    @PostMapping("/me/reservations/{id}/cancel")
    public MessageResponse cancelReservation(@PathVariable Long id, HttpSession session) {
        reservationService.cancel(id, currentUserId(session));
        return new MessageResponse("رزرو لغو شد.");
    }

    @GetMapping("/me/borrows")
    public List<BorrowResponse> borrows(HttpSession session) {
        return borrowService.findByUserId(currentUserId(session))
                .stream().map(mapper::toBorrow).toList();
    }

    private Long currentUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }
}
