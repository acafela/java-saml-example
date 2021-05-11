package saml.example.core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * https://docs.oasis-open.org/security/saml/v2.0/saml-core-2.0-os.pdf
 * 3.3.3 Element <Response>
 * The <Response> message element is used when a response consists of a list of zero or more assertions
 * that satisfy the request. It has the complex type ResponseType, which extends StatusResponseType
 */
@XmlRootElement(name = "Response", namespace = "urn:oasis:names:tc:SAML:2.0:protocol")
public final class Response {

    @XmlAttribute(name = "ID")
    private String id;

    private Version version;
    private LocalDateTime issueInstant;

    private String destination;
    private String inResponseTo;

    @XmlElement(name = "Issuer", namespace = "urn:oasis:names:tc:SAML:2.0:assertion")
    private NameID issuer;

    @XmlElement(name = "Status", namespace = "urn:oasis:names:tc:SAML:2.0:protocol")
    private Status status;

    @XmlElement(name = "Assertion", namespace = "urn:oasis:names:tc:SAML:2.0:assertion")
    private Assertion assertion;

    public Response() {
        id = UUID.randomUUID().toString();
        issueInstant = LocalDateTime.now();
    }

    public String id() {
        return id;
    }

    public Response id(String id) {
        this.id = id;
        return this;
    }

    public Version version() {
        return version;
    }

    public Response version(Version version) {
        this.version = version;
        return this;
    }

    public LocalDateTime issueInstant() {
        return issueInstant;
    }

    public Response issueInstant(LocalDateTime issueInstant) {
        this.issueInstant = issueInstant;
        return this;
    }

    public String destination() {
        return destination;
    }

    public Response destination(String destination) {
        this.destination = destination;
        return this;
    }

    public String inResponseTo() {
        return inResponseTo;
    }

    public Response inResponseTo(String inResponseTo) {
        this.inResponseTo = inResponseTo;
        return this;
    }

    public NameID issuer() {
        return issuer;
    }

    public Response issuer(NameID issuer) {
        this.issuer = issuer;
        return this;
    }

    public Status status() {
        return status;
    }

    public Response status(Status status) {
        this.status = status;
        return this;
    }

    public Assertion assertion() {
        return assertion;
    }

    public Response assertion(Assertion assertion) {
        this.assertion = assertion;
        return this;
    }
}