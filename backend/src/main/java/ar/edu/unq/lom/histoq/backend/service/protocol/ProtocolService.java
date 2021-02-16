package ar.edu.unq.lom.histoq.backend.service.protocol;

import ar.edu.unq.lom.histoq.backend.repository.protocol.IndividualRepository;
import ar.edu.unq.lom.histoq.backend.repository.protocol.ProtocolRepository;
import ar.edu.unq.lom.histoq.backend.repository.protocol.exception.IndividualNotFoundException;
import ar.edu.unq.lom.histoq.backend.model.protocol.Individual;
import ar.edu.unq.lom.histoq.backend.model.protocol.Protocol;
import ar.edu.unq.lom.histoq.backend.service.securiy.BaseServiceWithSecurity;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProtocolService extends BaseServiceWithSecurity {
    private final ProtocolRepository protocolRepository;
    private final IndividualRepository individualRepository;

    public  ProtocolService(ProtocolRepository protocolRepository,
                            IndividualRepository individualRepository,
                            SecurityService securityService) {
        super(securityService);
        this.protocolRepository = protocolRepository;
        this.individualRepository = individualRepository;
    }

    public Individual findIndividualById(Long individualId) {
        userAccessControl(null);
        return this.individualRepository
                .findById(individualId)
                .orElseThrow(() -> new IndividualNotFoundException("repository.individual-not-found",
                                                                         new String[]{individualId.toString()}));
    }

    public List<Protocol> findAllProtocolsContaining(String searchKey) {
        userAccessControl(null);
        return this.protocolRepository.findFirst20ByLabelContainingIgnoreCaseOrTitleContainingIgnoreCase(searchKey,searchKey);
    }
}
