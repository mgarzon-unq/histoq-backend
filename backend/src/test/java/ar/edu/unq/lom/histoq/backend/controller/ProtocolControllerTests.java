package ar.edu.unq.lom.histoq.backend.controller;

import ar.edu.unq.lom.histoq.backend.service.protocol.ProtocolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ProtocolControllerTests {
    private ProtocolController protocolController;

    @Mock
    private ProtocolService protocolService;

    @BeforeEach
    public void setUp() {
        this.protocolController = new ProtocolController(this.protocolService);
    }

    @Test
    void findAllProtocolsContainingTest() {
        String searchKey = "";

        this.protocolController.findAllProtocolsContaining(searchKey);

        verify(this.protocolService,times(1)).findAllProtocolsContaining(searchKey);
    }
}
