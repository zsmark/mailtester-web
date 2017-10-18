package hu.giro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Properties;


//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
//@EnableScheduling
@SpringBootApplication
public class SpringdemoApplication {

	public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringdemoApplication.class);

        /*try {
            Properties extra = new Properties();
            extra.put("server.port",Integer.parseInt(System.getProperty("port")));
            application.setDefaultProperties(extra);
            System.out.println();
        } catch (Throwable e) {

        }
*/
        application.run(args);
	}
}
