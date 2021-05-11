package saml.example.core;

import javax.xml.bind.annotation.XmlElement;

public class Subject {

    @XmlElement(name = "NameID", namespace = "urn:oasis:names:tc:SAML:2.0:assertion")
    private NameID nameID;

    @XmlElement(name = "SubjectConfirmation", namespace = "urn:oasis:names:tc:SAML:2.0:assertion")
    private SubjectConfirmation subjectConfirmation;

    public NameID nameID() {
        return nameID;
    }

    public Subject nameID(NameID nameID) {
        this.nameID = nameID;
        return this;
    }

    public SubjectConfirmation subjectConfirmation() {
        return subjectConfirmation;
    }

    public Subject subjectConfirmation(SubjectConfirmation subjectConfirmation) {
        this.subjectConfirmation = subjectConfirmation;
        return this;
    }
}
