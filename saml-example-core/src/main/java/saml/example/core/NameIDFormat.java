package saml.example.core;

public enum NameIDFormat {
    ENTITY("urn:oasis:names:tc:SAML:2.0:nameid-format:entity"),
    UNSPECIFIED("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");

    private final String value;

    NameIDFormat(String value) {
        this.value = value;
    }

    String value() {
        return value;
    }
}
