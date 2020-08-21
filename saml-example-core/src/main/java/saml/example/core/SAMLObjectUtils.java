package saml.example.core;

import org.opensaml.Configuration;
import org.opensaml.common.SAMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class SAMLObjectUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SAMLObjectUtils.class);

    public static String samlObjectToString(SAMLObject object) {
        try {
            Element ele = samlObjectToElement(object);
            return elementToString(ele);
        } catch (MarshallingException | IllegalArgumentException e) {
            LOG.warn("Failed to SAMLObject to String.", e);
            return "";
        }
    }

    private static Element samlObjectToElement(SAMLObject object) throws MarshallingException {
        Element element = null;
        try {
            MarshallerFactory unMarshallerFactory = Configuration.getMarshallerFactory();
            Marshaller marshaller = unMarshallerFactory.getMarshaller(object);
            element = marshaller.marshall(object);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("The class does not implement the interface XMLObject", e);
        }
        return element;
    }

    private static String elementToString(Element ele) {
        Document document = ele.getOwnerDocument();
        DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        return serializer.writeToString(ele);
    }
}
