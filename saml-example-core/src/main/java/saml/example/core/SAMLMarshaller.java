package saml.example.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

final class SAMLMarshaller {

    private final static Logger LOGGER = LoggerFactory.getLogger(SAMLMarshaller.class);

    private static Marshaller jaxbMarshaller;

    static {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AuthnRequest.class, Response.class);
            jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new SAMLNamespaceMapper());
        } catch (JAXBException e) {
            LOGGER.error("Failed initialize marshaller.", e);
        }
    }

    static Marshaller instance() {
        return jaxbMarshaller;
    }

}