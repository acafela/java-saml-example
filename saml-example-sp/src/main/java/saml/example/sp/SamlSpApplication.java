package saml.example.sp;

import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SamlSpApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(SamlSpApplication.class);

    public static void main(String[] args) {
        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException e) {
            LOGGER.error("Failed to initialize OpenSAML", e);
            return;
        }
        SpringApplication.run(SamlSpApplication.class, args);
    }

}