package ar.edu.unq.lom.histoq.backend.controller;

import ar.edu.unq.lom.histoq.backend.model.processJob.ProcessJob;
import ar.edu.unq.lom.histoq.backend.service.processJob.ProcessJobService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessJobController {
    private final ProcessJobService processJobService;

    ProcessJobController(ProcessJobService processJobService) {
        this.processJobService = processJobService;
    }

    @GetMapping("/jobs/{jobId}")
    public ProcessJob findProcessJob(@PathVariable Long jobId) {
        return this.processJobService.findProcessJobById(jobId);
    }
}
