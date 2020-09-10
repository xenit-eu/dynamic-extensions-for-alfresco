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

    @Bean
    public GreetingServiceWrapper greetingServiceWrapper() {
        return new GreetingServiceWrapper(greetingService());
    }

    @Bean
    public GreetingServiceWrapper anotherGreetingServiceWrapper() {
        return new GreetingServiceWrapper(greetingService());
    }

}
