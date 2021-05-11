package saml.example.core;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;

final class SAMLNamespaceMapper extends NamespacePrefixMapper {

    private static final String ASSERTION_PREFIX = "samlp";
    private static final String ASSERTION_URI = "urn:oasis:names:tc:SAML:2.0:protocol";

    private static final String PROTOCOL_PREFIX = "saml";
    private static final String PROTOCOL_URI = "urn:oasis:names:tc:SAML:2.0:assertion";

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        if (ASSERTION_URI.equals(namespaceUri)) {
            return ASSERTION_PREFIX;
        } else if (PROTOCOL_URI.equals(namespaceUri)) {
            return PROTOCOL_PREFIX;
        }
        return suggestion;
    }

    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] { ASSERTION_URI, PROTOCOL_URI };
    }

}
