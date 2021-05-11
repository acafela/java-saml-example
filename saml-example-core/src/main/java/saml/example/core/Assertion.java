package saml.example.core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * https://docs.oasis-open.org/security/saml/v2.0/saml-core-2.0-os.pdf
 * 2.3.3 Element <Assertion>
 * The <Assertion> element is of the AssertionType complex type. This type specifies the basic
 * information that is common to all assertions,
 */
public class Assertion {

    @XmlAttribute(name = "ID")
    private String id;

    @XmlAttribute(name = "Version")
    private Version version = Version.SAML20;

    @XmlAttribute(name = "IssueInstant")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime issueInstant;

    @XmlElement(name = "Issuer", namespace = "urn:oasis:names:tc:SAML:2.0:assertion")
    private NameID issuer;

    @XmlElement(name = "Subject", namespace = "urn:oasis:names:tc:SAML:2.0:assertion")
    private Subject subject;

    public Assertion() {
        issueInstant = LocalDateTime.now();
        id = UUID.randomUUID().toString();
    }

    public String id() {
        return id;
    }

    public Assertion id(String id) {
        this.id = id;
        return this;
    }

    public Version version() {
        return version;
    }

    public Assertion version(Version version) {
        this.version = version;
        return this;
    }

    public LocalDateTime issueInstant() {
        return issueInstant;
    }

    public Assertion issueInstant(LocalDateTime issueInstant) {
        this.issueInstant = issueInstant;
        return this;
    }

    public NameID issuer() {
        return issuer;
    }

    public Assertion issuer(NameID issuer) {
        this.issuer = issuer;
        return this;
    }

    public Subject subject() {
        return subject;
    }

    public Assertion subject(Subject subject) {
        this.subject = subject;
        return this;
    }
}
