package saml.example.core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * 3.2.2.2 Element <StatusCode>
 */
public class StatusCode {

    public StatusCode(Value value) {
        this.value = value;
    }

    @XmlAttribute(name = "Value")
    private Value value;

    enum Value {
        @XmlEnumValue("urn:oasis:names:tc:SAML:2.0:status:Success")
        SUCCESS,
        @XmlEnumValue("urn:oasis:names:tc:SAML:2.0:status:AuthnFailed")
        AuthnFailed,
        @XmlEnumValue("urn:oasis:names:tc:SAML:2.0:status:UnknownPrincipal")
        UnknownPrincipal,
        @XmlEnumValue("urn:oasis:names:tc:SAML:2.0:status:UnsupportedBinding")
        UnsupportedBinding
    }
}
