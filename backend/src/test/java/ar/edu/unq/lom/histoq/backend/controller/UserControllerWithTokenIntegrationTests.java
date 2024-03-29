package ar.edu.unq.lom.histoq.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerWithTokenIntegrationTests {

    @Autowired
    private MockMvc mvc;


    @Test
    void requestWithoutTokenReturnsHttpErrorForbidden() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void publicRequestWithoutCAPTCHAReturnsHttpErrorForbidden() throws Exception {
        mvc.perform(post("/users/registration-requests"))
                .andExpect(status().isForbidden());
    }
}
