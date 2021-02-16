package ar.edu.unq.lom.histoq.backend.controller;

import ar.edu.unq.lom.histoq.backend.controller.security.AccessValidationInterceptor;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerWithoutTokenIntegrationTests {

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
    void findNonExistentImageBatchByIdReturnsHttpErrorNotFound() throws Exception {
        mvc.perform(get("/image-batches/78546546546546"))
                .andExpect(status().isNotFound());
    }

    @Test
    void requestNonExistentImageFileByIdReturnsHttpErrorNotFound() throws Exception {
        mvc.perform(get("/image-batches/image-file/78546546546546"))
                .andExpect(status().isNotFound());
    }

    @Test
    void requestNonExistentImageByIdReturnsHttpErrorNotFound() throws Exception {
        mvc.perform(get("/image-batches/image/78546546546546"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNonExistentImageBatchByIdReturnsHttpErrorNotFound() throws Exception {
        mvc.perform(delete("/image-batches/78546546546546"))
                .andExpect(status().isNotFound());
    }

}
