package com.example.library.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // بررسی می‌کند کاربر وارد سامانه شده باشد.
        HttpSession session = request.getSession(false);
        Long userId = session == null ? null : (Long) session.getAttribute("userId");

        if (userId == null) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "برای ادامه ابتدا وارد حساب شوید.");
            return false;
        }

        // مسیرهای پنل مدیریت فقط برای نقش ADMIN مجاز هستند.
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (path.startsWith("/api/admin")) {
            String role = (String) session.getAttribute("userRole");
            if (!"ADMIN".equals(role)) {
                writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, "این بخش فقط برای مدیر سامانه است.");
                return false;
            }
        }

        return true;
    }

    private void writeJsonError(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
