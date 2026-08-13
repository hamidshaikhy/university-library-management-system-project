package com.example.library.service;

import com.example.library.entity.Category;
import com.example.library.repository.BookRepository;
import com.example.library.repository.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll(Sort.by("title"));
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("دسته‌بندی پیدا نشد."));
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        if (bookRepository.countByCategoryId(id) > 0) {
            throw new IllegalStateException("این دسته‌بندی به یک یا چند کتاب متصل است و قابل حذف نیست.");
        }
        categoryRepository.deleteById(id);
    }
}
