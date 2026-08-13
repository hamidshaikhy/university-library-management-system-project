package com.example.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void meShowsAnonymousStateWithoutSession() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));
    }

    @Test
    void normalUserCanLoginAndReadBooks() throws Exception {
        MockHttpSession session = login("user@library.local", "User123");

        mockMvc.perform(get("/api/books").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books").isArray())
                .andExpect(jsonPath("$.categories").isArray());
    }

    @Test
    void adminRoutesCheckRole() throws Exception {
        MockHttpSession userSession = login("user@library.local", "User123");
        mockMvc.perform(get("/api/admin/dashboard").session(userSession))
                .andExpect(status().isForbidden());

        MockHttpSession adminSession = login("admin@library.local", "Admin123");
        mockMvc.perform(get("/api/admin/dashboard").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookCount").isNumber());
    }

    private MockHttpSession login(String email, String password) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }
}
