package ar.edu.unq.lom.histoq.backend.service;

import ar.edu.unq.lom.histoq.backend.service.async.AsyncRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.Locale;

public class BaseService {

    @Autowired
    private AsyncRunnerService asyncRunnerService;

    protected void asyncRun(final Runnable runnable) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        LocaleContextHolder.setLocale(currentLocale, true);
        this.asyncRunnerService.run(runnable);
    }
}
