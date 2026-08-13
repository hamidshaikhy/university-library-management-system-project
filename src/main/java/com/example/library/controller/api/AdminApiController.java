package com.example.library.controller.api;

import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.service.AuthorService;
import com.example.library.service.BookService;
import com.example.library.service.BorrowService;
import com.example.library.service.CategoryService;
import com.example.library.service.ReservationService;
import com.example.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.library.controller.api.ApiDtos.*;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final BorrowService borrowService;
    private final ReservationService reservationService;
    private final ApiMapper mapper;

    public AdminApiController(BookService bookService,
                              AuthorService authorService,
                              CategoryService categoryService,
                              UserService userService,
                              BorrowService borrowService,
                              ReservationService reservationService,
                              ApiMapper mapper) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.borrowService = borrowService;
        this.reservationService = reservationService;
        this.mapper = mapper;
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse dashboard() {
        return new AdminDashboardResponse(
                bookService.count(),
                userService.count(),
                borrowService.countActive(),
                reservationService.countPending()
        );
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return userService.findAll().stream().map(mapper::toUser).toList();
    }

    @GetMapping("/authors")
    public List<AuthorResponse> authors() {
        return authorService.findAll().stream().map(mapper::toAuthor).toList();
    }

    @PostMapping("/authors")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse createAuthor(@Valid @RequestBody AuthorRequest request) {
        return mapper.toAuthor(authorService.save(toAuthor(request, new Author())));
    }

    @PutMapping("/authors/{id}")
    public AuthorResponse updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorRequest request) {
        return mapper.toAuthor(authorService.save(toAuthor(request, authorService.findById(id))));
    }

    @DeleteMapping("/authors/{id}")
    public MessageResponse deleteAuthor(@PathVariable Long id) {
        authorService.delete(id);
        return new MessageResponse("نویسنده حذف شد.");
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return categoryService.findAll().stream().map(mapper::toCategory).toList();
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return mapper.toCategory(categoryService.save(toCategory(request, new Category())));
    }

    @PutMapping("/categories/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return mapper.toCategory(categoryService.save(toCategory(request, categoryService.findById(id))));
    }

    @DeleteMapping("/categories/{id}")
    public MessageResponse deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return new MessageResponse("دسته‌بندی حذف شد.");
    }

    @GetMapping("/books")
    public AdminBooksResponse books() {
        return new AdminBooksResponse(
                bookService.findAll(null, null).stream().map(mapper::toBook).toList(),
                authorService.findAll().stream().map(mapper::toAuthor).toList(),
                categoryService.findAll().stream().map(mapper::toCategory).toList()
        );
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@Valid @RequestBody BookRequest request) {
        ensureUniqueIsbn(request.isbn(), null);
        return mapper.toBook(bookService.save(toBook(request, new Book()), request.authorId(), request.categoryId()));
    }

    @PutMapping("/books/{id}")
    public BookResponse updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        ensureUniqueIsbn(request.isbn(), id);
        Book book = toBook(request, bookService.findById(id));
        return mapper.toBook(bookService.save(book, request.authorId(), request.categoryId()));
    }

    @DeleteMapping("/books/{id}")
    public MessageResponse deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return new MessageResponse("کتاب حذف شد.");
    }

    @GetMapping("/borrows")
    public AdminBorrowsResponse borrows() {
        return new AdminBorrowsResponse(
                borrowService.findActive().stream().map(mapper::toBorrow).toList(),
                userService.findAll().stream().map(mapper::toUser).toList(),
                bookService.findAll(null, null).stream().map(mapper::toBook).toList()
        );
    }

    @PostMapping("/borrows")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowResponse createBorrow(@Valid @RequestBody BorrowRequest request) {
        return mapper.toBorrow(borrowService.create(request.userId(), request.bookId(), request.dueDate()));
    }

    @PostMapping("/borrows/{id}/return")
    public MessageResponse returnBook(@PathVariable Long id) {
        borrowService.returnBook(id);
        return new MessageResponse("بازگشت کتاب ثبت شد و موجودی افزایش یافت.");
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> reservations() {
        return reservationService.findAll().stream().map(mapper::toReservation).toList();
    }

    @PostMapping("/reservations/{id}/approve")
    public MessageResponse approveReservation(@PathVariable Long id) {
        reservationService.approve(id);
        return new MessageResponse("رزرو تأیید شد، امانت ثبت شد و موجودی کاهش یافت.");
    }

    @PostMapping("/reservations/{id}/reject")
    public MessageResponse rejectReservation(@PathVariable Long id) {
        reservationService.reject(id);
        return new MessageResponse("رزرو رد شد.");
    }

    private Author toAuthor(AuthorRequest request, Author author) {
        author.setFullName(request.fullName().trim());
        author.setBiography(request.biography());
        return author;
    }

    private Category toCategory(CategoryRequest request, Category category) {
        category.setTitle(request.title().trim());
        category.setDescription(request.description());
        return category;
    }

    private Book toBook(BookRequest request, Book book) {
        book.setTitle(request.title().trim());
        book.setIsbn(request.isbn().trim());
        book.setPublishYear(request.publishYear());
        book.setQuantity(request.quantity());
        book.setDescription(request.description());
        book.setCoverUrl(request.coverUrl());
        return book;
    }

    private void ensureUniqueIsbn(String isbn, Long currentId) {
        if (bookService.isIsbnDuplicate(isbn.trim(), currentId)) {
            throw new IllegalStateException("این ISBN قبلاً ثبت شده است.");
        }
    }
}
