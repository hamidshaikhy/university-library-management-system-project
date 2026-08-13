package com.example.library.config;

import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.entity.Reservation;
import com.example.library.entity.ReservationStatus;
import com.example.library.entity.Role;
import com.example.library.entity.User;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.CategoryRepository;
import com.example.library.repository.ReservationRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final AuthService authService;

    public DataInitializer(UserRepository userRepository,
                           AuthorRepository authorRepository,
                           CategoryRepository categoryRepository,
                           BookRepository bookRepository,
                           ReservationRepository reservationRepository,
                           AuthService authService) {
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
        this.authService = authService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // کاربران اولیه فقط در صورت نبودن حساب‌ها ساخته می‌شوند.
        User admin = createDefaultUser(
                "مدیر کتابخانه",
                "admin@library.local",
                "Admin123",
                Role.ADMIN
        );
        User normalUser = createDefaultUser(
                "دانشجوی نمونه",
                "user@library.local",
                "User123",
                Role.USER
        );

        // داده‌های نمونه فقط در اولین اجرای برنامه وارد می‌شوند.
        if (authorRepository.count() == 0 && categoryRepository.count() == 0 && bookRepository.count() == 0) {
            Author author1 = authorRepository.save(new Author("رابرت سی. مارتین", "نویسنده کتاب‌های مطرح مهندسی نرم‌افزار."));
            Author author2 = authorRepository.save(new Author("اندرو تننبام", "نویسنده و استاد حوزه سیستم‌عامل و شبکه."));
            Author author3 = authorRepository.save(new Author("استوارت راسل", "پژوهشگر و نویسنده حوزه هوش مصنوعی."));

            Category category1 = categoryRepository.save(new Category("برنامه‌نویسی", "کتاب‌های برنامه‌نویسی و مهندسی نرم‌افزار"));
            Category category2 = categoryRepository.save(new Category("شبکه", "مبانی و معماری شبکه‌های کامپیوتری"));
            Category category3 = categoryRepository.save(new Category("هوش مصنوعی", "یادگیری ماشین و هوش مصنوعی"));
            Category category4 = categoryRepository.save(new Category("پایگاه داده", "طراحی و مدیریت پایگاه داده"));

            List<Book> books = List.of(
                    new Book("کدنویسی تمیز", "9780132350884", 2008, 4,
                            "راهنمای اصول نوشتن کد خوانا و قابل نگهداری.", "", author1, category1),
                    new Book("معماری تمیز", "9780134494166", 2017, 3,
                            "اصول طراحی معماری نرم‌افزار.", "", author1, category1),
                    new Book("شبکه‌های کامپیوتری", "9780132126953", 2010, 2,
                            "مرجع آموزشی مفاهیم شبکه‌های کامپیوتری.", "", author2, category2),
                    new Book("سیستم‌عامل‌های نوین", "9780133591620", 2014, 2,
                            "مبانی طراحی و عملکرد سیستم‌عامل.", "", author2, category1),
                    new Book("هوش مصنوعی: رویکردی نوین", "9780134610993", 2020, 3,
                            "مقدمه‌ای جامع بر هوش مصنوعی.", "", author3, category3),
                    new Book("مبانی پایگاه داده", "9780073523323", 2010, 5,
                            "آشنایی با مدل رابطه‌ای و SQL.", "", author1, category4)
            );
            bookRepository.saveAll(books);
        }

        if (reservationRepository.count() == 0 && normalUser != null && bookRepository.count() > 0) {
            Reservation reservation = new Reservation();
            reservation.setUser(normalUser);
            reservation.setBook(bookRepository.findTopByOrderByIdAsc()
                    .orElseThrow(() -> new IllegalStateException("کتاب نمونه پیدا نشد.")));
            reservation.setReservationDate(LocalDateTime.now().minusDays(1));
            reservation.setStatus(ReservationStatus.PENDING);
            reservationRepository.save(reservation);
        }
    }

    private User createDefaultUser(String fullName, String email, String password, Role role) {
        return userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            User user = new User(fullName, email, authService.encodePassword(password), role);
            return userRepository.save(user);
        });
    }
}
