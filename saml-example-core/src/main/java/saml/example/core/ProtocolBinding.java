package saml.example.core;

import javax.xml.bind.annotation.XmlEnumValue;

public enum ProtocolBinding {

    @XmlEnumValue("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST")
    POST("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"),

    @XmlEnumValue("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect")
    REDIRECT("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");

    private final String value;

    ProtocolBinding(String value) {
        this.value = value;
    }

    String value() {
        return value;
    }
}
