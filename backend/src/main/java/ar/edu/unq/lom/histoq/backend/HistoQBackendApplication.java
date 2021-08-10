package ar.edu.unq.lom.histoq.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.DispatcherServlet;


@SpringBootApplication
@EnableAsync
public class HistoQBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HistoQBackendApplication.class, args)
				.getBean(DispatcherServlet.class)
				.setThreadContextInheritable(true);
	}

}