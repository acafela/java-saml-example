package saml.example.core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class SubjectConfirmation {

    /**
     * https://docs.oasis-open.org/security/saml/v2.0/saml-profile-2.0-os.pdf
     * 3.3 Bearer
     */
    @XmlAttribute(name = "Method")
    private String method = "urn:oasis:names:tc:SAML:2.0:cm:bearer";

    @XmlElement(name = "SubjectConfirmationData", namespace = "urn:oasis:names:tc:SAML:2.0:assertion")
    private SubjectConfirmationData subjectConfirmationData;

    public String method() {
        return method;
    }

    public SubjectConfirmation method(String method) {
        this.method = method;
        return this;
    }

    public SubjectConfirmationData subjectConfirmationData() {
        return subjectConfirmationData;
    }

    public SubjectConfirmation subjectConfirmationData(SubjectConfirmationData subjectConfirmationData) {
        this.subjectConfirmationData = subjectConfirmationData;
        return this;
    }
}
