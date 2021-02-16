package ar.edu.unq.lom.histoq.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProtocolControllerWithTokenIntegrationTests {
    @Autowired
    private MockMvc mvc;

    @Test
    void requestWithoutTokenReturnsHttpErrorForbidden() throws Exception {
        mvc.perform(get("/protocols/blabla"))
                .andExpect(status().isForbidden());
    }
}
