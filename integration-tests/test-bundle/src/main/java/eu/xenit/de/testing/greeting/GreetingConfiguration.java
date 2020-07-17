package eu.xenit.de.testing.greeting;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("unused")
public class GreetingConfiguration {

    @Bean
    public GreetingService greetingService() {
        return new GreetingService();
    }

}
