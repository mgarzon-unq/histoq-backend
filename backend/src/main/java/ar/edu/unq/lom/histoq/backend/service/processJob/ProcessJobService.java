package ar.edu.unq.lom.histoq.backend.service.processJob;

import ar.edu.unq.lom.histoq.backend.model.processJob.ProcessJob;
import ar.edu.unq.lom.histoq.backend.repository.processJob.ProcessJobRepository;
import ar.edu.unq.lom.histoq.backend.repository.processJob.exception.ProcessJobNotFoundException;
import ar.edu.unq.lom.histoq.backend.service.securiy.BaseServiceWithSecurity;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessJobService extends BaseServiceWithSecurity {
    private final ProcessJobRepository processJobRepository;

    ProcessJobService(ProcessJobRepository processJobRepository,
                 SecurityService securityService) {
        super(securityService);
        this.processJobRepository = processJobRepository;
    }

    public ProcessJob findProcessJobById(Long jobId) {
        userAccessControl(null);

        return this.processJobRepository
                .findById(jobId)
                .orElseThrow( () -> new ProcessJobNotFoundException("repository.process-job-not-found",
                        new String[]{jobId.toString()}) );
    }

    @Transactional
    public ProcessJob createProcessJob(String goal) {
        return this.processJobRepository.save(new ProcessJob(goal));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProcessJob processJobFinishedSuccessfully(ProcessJob processJob) {
        processJob.finishSuccessfully();
        return this.processJobRepository.save(processJob);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProcessJob processJobFinishedWithErrors(ProcessJob processJob, String errorsDescription) {
        processJob.finishWithErrors(errorsDescription);
        return this.processJobRepository.save(processJob);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProcessJob addSubProcessToJob(ProcessJob processJob, String goal) {
        ProcessJob subProcess = processJob.addSubProcess(goal);
        this.processJobRepository.save(subProcess);
        return subProcess;
    }
}
