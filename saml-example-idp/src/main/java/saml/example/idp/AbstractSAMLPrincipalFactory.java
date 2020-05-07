package saml.example.idp;

import java.util.List;

import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.NameIDType;
import org.springframework.security.core.Authentication;

import saml.example.core.SAMLAttribute;
import saml.example.core.SAMLPrincipal;

public abstract class AbstractSAMLPrincipalFactory {
	
	public SAMLPrincipal createSAMLPrincipal(@SuppressWarnings("rawtypes") SAMLMessageContext messageContext, Authentication authentication) {
		
		AuthnRequest authnRequest = (AuthnRequest) messageContext.getInboundSAMLMessage();

//		String assertionConsumerServiceURL = authnRequest.getAssertionConsumerServiceURL();
		List<SAMLAttribute> attributes = createAttributes(authentication);

		String nameIdType = attributes.stream()
									.filter(attr -> "urn:oasis:names:tc:SAML:1.1:nameid-format".equals(attr.getName()))
									.findFirst()
									.map(attr -> attr.getValue())
									.orElse(NameIDType.UNSPECIFIED); 
		
		SAMLPrincipal principal = SAMLPrincipal.builder(authentication.getName(), nameIdType, attributes)
										.serviceProviderEntityID(authnRequest.getIssuer().getValue())
										.requestID(authnRequest.getID())
										.assertionConsumerServiceUrl(authnRequest.getAssertionConsumerServiceURL())
										.relayState(messageContext.getRelayState())
										.build();
		
		return principal;
	}

	protected abstract List<SAMLAttribute> createAttributes(Authentication authentication);

}
