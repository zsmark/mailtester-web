package hu.giro.smtpserver;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class Configuration implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Properties props = new Properties();
        try {
            props.put("server.port", Integer.parseInt(System.getProperty("web.port")));
        } catch (Throwable e) {}
        try {
            props.put("smtpserver.port", Integer.parseInt(System.getProperty("smtp.port")));
        } catch (Throwable e) {}

        event.getEnvironment().
                getPropertySources().
                addFirst(new PropertiesPropertySource("overriden", props));
    }

}
