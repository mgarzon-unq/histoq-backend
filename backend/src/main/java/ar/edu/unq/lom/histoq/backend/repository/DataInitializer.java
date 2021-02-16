package ar.edu.unq.lom.histoq.backend.repository;

import ar.edu.unq.lom.histoq.backend.repository.image.ImageBatchRepository;
import ar.edu.unq.lom.histoq.backend.repository.protocol.ProtocolRepository;
import ar.edu.unq.lom.histoq.backend.repository.user.UserRepository;
import ar.edu.unq.lom.histoq.backend.model.protocol.ExperimentalGroup;
import ar.edu.unq.lom.histoq.backend.model.protocol.Individual;
import ar.edu.unq.lom.histoq.backend.model.protocol.Protocol;
import ar.edu.unq.lom.histoq.backend.service.internationalization.InternationalizationMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Date;

@Configuration
@Slf4j
public class DataInitializer {

    private InternationalizationMessageService messageService;

    DataInitializer(InternationalizationMessageService messageService) {
        this.messageService = messageService;
    }

    @Bean
    CommandLineRunner initDatabase(ProtocolRepository protocolRepository,
                                   ImageBatchRepository imageBatchRepository,
                                   UserRepository userRepository) {
        return args -> {
            /*
            log.info(messageService.getMessage("log.db.init-deleting-records"));

            userRepository.deleteAll();
            imageBatchRepository.deleteAll();
            protocolRepository.deleteAll();

            log.info( userRepository.save( new User("Marcelo","Garzon","marcelo.garzon@gmail.com",true)).toString() );
            log.info( userRepository.save( new User("Robert","Pitwick","trasvalador@gmail.com",false)).toString() );

            log.info( getMessageForProtocolCreation( protocolRepository.save( createDummyProtocol("MG-20200905-01", "Drug repurposing of β-blocker propranolol in osteosarcoma."))));
            log.info( getMessageForProtocolCreation( protocolRepository.save( createDummyProtocol("MG-20200905-02", "Preclinical antitumor YYYYY efficacy in combination with chemotherapy."))));
            log.info( getMessageForProtocolCreation( protocolRepository.save( createDummyProtocol("MG-20200905-03", "In vitro and in vivo antitumoral activity of PPN."))));
            log.info( getMessageForProtocolCreation( protocolRepository.save( createDummyProtocol("MG-20200905-04", "Blocking protumoral effects after β-AR activation by catecholamines."))));
            log.info( getMessageForProtocolCreation( protocolRepository.save( createDummyProtocol("MG-20200905-05", "PPN as a co-adjuvant agent for the management of OSA."))));
            log.info( getMessageForProtocolCreation( protocolRepository.save( createDummyProtocol("MG-20200905-06", "Cellular proliferation in human glioblastoma."))));

             */
        };
    }


    private String getMessageForProtocolCreation(Protocol protocol) {
        return messageService.getMessage( "log.db.init-creating-protocol-records",
                new String[] { protocol.toString() } );
    }

    private Protocol createDummyProtocol(String label, String title) {
        Protocol protocol = new Protocol(label, title, new Date() );
        addDummyExperimentalGroup(protocol,"A");
        addDummyExperimentalGroup(protocol,"B");
        addDummyExperimentalGroup(protocol,"C");
        return protocol;
    }

    private ExperimentalGroup addDummyExperimentalGroup(Protocol protocol, String label) {
        ExperimentalGroup group = new ExperimentalGroup();
        group.setLabel(label);
        protocol.addExperimentalGroup(group);
        addDummyIndividual(group, "1");
        addDummyIndividual(group, "2");
        addDummyIndividual(group, "3");
        addDummyIndividual(group, "4");
        addDummyIndividual(group, "5");
        return group;
    }

    private Individual addDummyIndividual(ExperimentalGroup group, String label) {
        Individual individual = new Individual();
        individual.setLabel(label);
        group.addIndividual(individual);
        return individual;
    }

}
