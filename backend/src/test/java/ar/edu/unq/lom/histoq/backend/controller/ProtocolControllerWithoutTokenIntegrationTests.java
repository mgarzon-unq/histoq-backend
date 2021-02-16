package ar.edu.unq.lom.histoq.backend.controller;

import ar.edu.unq.lom.histoq.backend.controller.security.AccessValidationInterceptor;
import ar.edu.unq.lom.histoq.backend.repository.user.exception.UnauthorizedAccessException;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProtocolControllerWithoutTokenIntegrationTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccessValidationInterceptor accessValidationInterceptor;

    @MockBean
    private SecurityService securityService;


    @BeforeEach
    public void setUp() throws Exception {
        when(this.accessValidationInterceptor.preHandle(any(),any(),any())).thenReturn(true);
    }

    @Test
    void requestWithoutActiveUserReturnsHttpErrorForbidden() throws Exception {
        Mockito.doThrow(UnauthorizedAccessException.class).when(this.securityService).userAccessControl(null);

        mvc.perform(get("/protocols/blabla"))
                .andExpect(status().isForbidden());
    }
}
