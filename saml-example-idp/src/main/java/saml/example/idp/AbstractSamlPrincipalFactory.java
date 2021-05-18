package saml.example.idp;

import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.AuthnRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

abstract class AbstractSamlPrincipalFactory {

    private final String nameIdType;

    public AbstractSamlPrincipalFactory(String nameIdType) {
        this.nameIdType = nameIdType;
    }

    public SamlPrincipal createSamlPrincipal(@SuppressWarnings("rawtypes") SAMLMessageContext messageContext,
                                             Authentication authentication) {
        AuthnRequest authnRequest = (AuthnRequest) messageContext.getInboundSAMLMessage();
        List<SamlAttribute> attributes = createAttributes(authentication);
        return SamlPrincipal.builder(authentication.getName(), nameIdType, attributes)
                            .serviceProviderEntityID(authnRequest.getIssuer().getValue())
                            .requestID(authnRequest.getID())
                            .assertionConsumerServiceUrl(authnRequest.getAssertionConsumerServiceURL())
                            .relayState(messageContext.getRelayState())
                            .build();
    }

    protected abstract List<SamlAttribute> createAttributes(Authentication authentication);

}