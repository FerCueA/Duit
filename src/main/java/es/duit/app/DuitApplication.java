package es.duit.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "es.duit")
public class DuitApplication {

	public static void main(String[] args) {
		SpringApplication.run(DuitApplication.class, args);
	}

}
