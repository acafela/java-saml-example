package saml.example.core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

public class SubjectConfirmationData {

    @XmlAttribute(name = "InResponseTo")
    private String inResponseTo;

    @XmlAttribute(name = "NotOnOrAfter")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime notOnOrAfter;

    @XmlAttribute(name = "Recipient")
    private String recipient;

    public String inResponseTo() {
        return inResponseTo;
    }

    public SubjectConfirmationData inResponseTo(String inResponseTo) {
        this.inResponseTo = inResponseTo;
        return this;
    }

    public LocalDateTime notOnOrAfter() {
        return notOnOrAfter;
    }

    public SubjectConfirmationData notOnOrAfter(LocalDateTime notOnOrAfter) {
        this.notOnOrAfter = notOnOrAfter;
        return this;
    }

    public String recipient() {
        return recipient;
    }

    public SubjectConfirmationData recipient(String recipient) {
        this.recipient = recipient;
        return this;
    }
}
