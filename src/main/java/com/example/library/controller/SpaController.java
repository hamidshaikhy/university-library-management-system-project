package com.example.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({
            "/",
            "/login",
            "/register",
            "/dashboard",
            "/profile",
            "/books",
            "/books/{id:\\d+}",
            "/my-reservations",
            "/my-borrows",
            "/admin",
            "/admin/dashboard",
            "/admin/users",
            "/admin/authors",
            "/admin/categories",
            "/admin/books",
            "/admin/borrows",
            "/admin/reservations"
    })
    public String reactApp() {
        return "forward:/index.html";
    }
}
