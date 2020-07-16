package eu.xenit.de.testing.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationTest {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationTest.class);

    public ConfigurationTest() {
        log.info("Instantiating ConfigurationTestClass");
    }

}
