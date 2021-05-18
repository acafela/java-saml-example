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
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SamlIdpApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SamlIdpApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SamlIdpApplication.class, args);
    }

    @Component
    public static class SamlBootstrap implements BeanFactoryPostProcessor {

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

    @Configuration
    public static class MvcConfig implements WebMvcConfigurer {
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("login");
            registry.addViewController("/login").setViewName("login");
        }
    }
}