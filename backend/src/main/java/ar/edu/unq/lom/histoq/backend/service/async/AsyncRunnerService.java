package ar.edu.unq.lom.histoq.backend.service.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsyncRunnerService {

    @Async
    @Transactional
    public void run(final Runnable runnable) {
        runnable.run();
    }

}
