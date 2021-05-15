package saml.example.idp;

import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class SamlIdpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SamlIdpApplication.class, args);
    }

    @Component
    public static class SamlBootstrap implements BeanFactoryPostProcessor {

        private static final Logger LOGGER = LoggerFactory.getLogger(SamlBootstrap.class);

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            try {
                LOGGER.info("Initialize open saml...");
                DefaultBootstrap.bootstrap();
            } catch (ConfigurationException e) {
                throw new FatalBeanException("Error invoking OpenSAML bootstrap", e);
            }
        }
    }
}