package saml.example.sp;

import org.joda.time.DateTime;
import org.junit.Test;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.namespace.QName;
import java.util.UUID;

public class AuthnRequestTests {

    static String issuerUrl = "https://acafela.github.io";   // Issuer element value로 사용할 값 입력
    static String acsUrl = "https://acafela.github.io/acs";  // ACS URL 입력

    @Test
    public void createAuthnRequestTest(){
        try {
            // OpenSAML Library 초기화
            DefaultBootstrap.bootstrap();
            // Issuer 생성
            Issuer issuer = buildIssuer(issuerUrl);
            // AuthnRequest 생성
            AuthnRequest authnRequest = buildAuthnRequest(acsUrl, SAMLConstants.SAML2_POST_BINDING_URI, issuer);
            // 생성한 AuthnRequest 찍어보기 위해 String으로 변경
            String authnRequestTxt = samlObjectToString(authnRequest);
            // AutnRquest Console 출력
            System.out.println(authnRequestTxt);
        } catch (ConfigurationException e) {
            System.err.println("OpenSAML 초기 설정 실패");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    <T> T buildSAMLObject(final Class<T> objectClass, QName qName) {
        XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
        return (T) builderFactory.getBuilder(qName).buildObject(qName);
    }

    // AuthnRequest 관련 추가 설정은 이 메소드 수정
    AuthnRequest buildAuthnRequest(String acsUrl, String protocolBinding, Issuer issuer) {
        AuthnRequest authnRequest = buildSAMLObject(AuthnRequest.class, AuthnRequest.DEFAULT_ELEMENT_NAME);
        authnRequest.setIsPassive(true);
        authnRequest.setVersion(SAMLVersion.VERSION_20);
        authnRequest.setAssertionConsumerServiceURL(acsUrl);
        authnRequest.setProtocolBinding(protocolBinding);
        authnRequest.setIssuer(issuer);
        authnRequest.setIssueInstant(new DateTime());
        authnRequest.setID(UUID.randomUUID().toString());
        return authnRequest;
    }

    Issuer buildIssuer(String issuingEntityName) {
        Issuer issuer = buildSAMLObject(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue(issuingEntityName);
        issuer.setFormat(NameIDType.ENTITY);
        return issuer;
    }

    String samlObjectToString(SAMLObject object) {
        try {
            Element ele = samlObjectToElement(object);
            return elementToString(ele);
        } catch (MarshallingException | IllegalArgumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    org.w3c.dom.Element samlObjectToElement(SAMLObject object) throws MarshallingException {
        org.w3c.dom.Element element = null;
        try {
            MarshallerFactory unMarshallerFactory = Configuration.getMarshallerFactory();
            Marshaller marshaller = unMarshallerFactory.getMarshaller(object);
            element = marshaller.marshall(object);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("The class does not implement the interface XMLObject", e);
        }
        return element;
    }

    String elementToString(org.w3c.dom.Element ele) {
        Document document = ele.getOwnerDocument();
        DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        return serializer.writeToString(ele);
    }
}
