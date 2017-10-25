package hu.giro;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class MailtesterApplication {

	public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MailtesterApplication.class);
        application.run(args);
	}

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
