package hu.giro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MailtesterApplication {

	public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MailtesterApplication.class);
        application.run(args);
	}
}
