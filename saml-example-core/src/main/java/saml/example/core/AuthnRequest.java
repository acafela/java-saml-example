package saml.example.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * https://docs.oasis-open.org/security/saml/v2.0/saml-core-2.0-os.pdf
 * 3.2.1 Complex Type RequestAbstractType
 */
@XmlRootElement(name = "AuthnRequest", namespace = "urn:oasis:names:tc:SAML:2.0:protocol")
public final class AuthnRequest {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthnRequest.class);

    @XmlAttribute(name = "ID")
    private String id;

    @XmlAttribute(name = "Version")
    private Version version = Version.SAML20;

    @XmlAttribute(name = "IssueInstant")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime issueInstant;

    @XmlElement(name = "Issuer", namespace = "urn:oasis:names:tc:SAML:2.0:assertion")
    private NameID issuer;

    @XmlAttribute(name = "Destination")
    private String destination;

    @XmlAttribute(name = "AssertionConsumerServiceURL")
    private String assertionConsumerServiceURL;

    @XmlAttribute(name = "ProtocolBinding")
    private ProtocolBinding protocolBinding;

    public AuthnRequest() {
        id = UUID.randomUUID().toString();
        issueInstant = LocalDateTime.now();
    }

    public String toXml() {
        OutputStream os = new ByteArrayOutputStream();
        try {
            SAMLMarshaller.instance().marshal(this, os);
            return os.toString();
        } catch (JAXBException e) {
            LOGGER.error("Failed to marshal AuthnRequest[{}]", this.toString(), e);
            return "";
        }
    }

    public String id() {
        return id;
    }

    public AuthnRequest id(String id) {
        this.id = id;
        return this;
    }

    public Version version() {
        return version;
    }

    public AuthnRequest version(Version version) {
        this.version = version;
        return this;
    }

    public LocalDateTime issueInstant() {
        return issueInstant;
    }

    public AuthnRequest issueInstant(LocalDateTime issueInstant) {
        this.issueInstant = issueInstant;
        return this;
    }

    public NameID issuer() {
        return issuer;
    }

    public AuthnRequest issuer(NameID issuer) {
        this.issuer = issuer;
        return this;
    }

    public String destination() {
        return destination;
    }

    public AuthnRequest destination(String destination) {
        this.destination = destination;
        return this;
    }

    public String assertionConsumerServiceURL() {
        return assertionConsumerServiceURL;
    }

    public AuthnRequest assertionConsumerServiceURL(String assertionConsumerServiceURL) {
        this.assertionConsumerServiceURL = assertionConsumerServiceURL;
        return this;
    }

    public ProtocolBinding protocolBinding() {
        return protocolBinding;
    }

    public AuthnRequest protocolBinding(ProtocolBinding protocolBinding) {
        this.protocolBinding = protocolBinding;
        return this;
    }
}
