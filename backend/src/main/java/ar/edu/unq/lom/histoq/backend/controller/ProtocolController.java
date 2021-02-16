package ar.edu.unq.lom.histoq.backend.controller;

import ar.edu.unq.lom.histoq.backend.model.protocol.Protocol;
import ar.edu.unq.lom.histoq.backend.service.protocol.ProtocolService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class ProtocolController {

    private final ProtocolService protocolService;

    ProtocolController(ProtocolService protocolService){
        this.protocolService = protocolService;
    }

    @GetMapping("/protocols/{searchKey}")
    List<Protocol> findAllProtocolsContaining(@PathVariable String searchKey) {
        return protocolService.findAllProtocolsContaining(searchKey);
    }

}
