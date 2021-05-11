package saml.example.idp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SamlIdpApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(SamlIdpApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SamlIdpApplication.class, args);
    }

}