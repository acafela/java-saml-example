package saml.example.sp;

import org.opensaml.Configuration;
import org.opensaml.common.SAMLObject;
import org.opensaml.saml2.core.NameID;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import java.util.List;

public final class SAMLUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SAMLUtil.class);

    public static String getStringFromXMLObject(XMLObject xmlObj) {
        if (xmlObj instanceof XSString) {
            return ((XSString) xmlObj).getValue();
        } else if (xmlObj instanceof XSAny) {
            XSAny xsAny = (XSAny) xmlObj;
            String textContent = xsAny.getTextContent();
            if (StringUtils.hasText(textContent)) {
                return textContent;
            }
            List<XMLObject> unknownXMLObjects = xsAny.getUnknownXMLObjects();
            if (!CollectionUtils.isEmpty(unknownXMLObjects)) {
                XMLObject xmlObject = unknownXMLObjects.get(0);
                if (xmlObject instanceof NameID) {
                    NameID nameID = (NameID) xmlObject;
                    return nameID.getValue();
                }
            }
        }
        return "";
    }

    public static String samlObjectToString(SAMLObject object) {
        try {
            Element ele = samlObjectToElement(object);
            return elementToString(ele);
        } catch (MarshallingException | IllegalArgumentException e) {
            LOGGER.warn("Failed to SAMLObject to String.", e);
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