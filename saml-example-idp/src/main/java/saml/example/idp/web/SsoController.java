package saml.example.idp.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.common.binding.SAMLMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import saml.example.core.SAMLPrincipal;
import saml.example.idp.AbstractSAMLPrincipalFactory;
import saml.example.idp.LocalSAMLPrincipalFactory;
import saml.example.idp.SAMLMessageHandler;

@Controller
public class SsoController {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private SAMLMessageHandler samlMessageHandler;

	@GetMapping("/sso")
	public void sso(HttpServletRequest request, HttpServletResponse response, Authentication authentication, String SigAlg, String SAMLRequest, String Signature, String RelayState) 
			throws Exception {

		LOG.debug("SAMLRequest=[{}], Signature=[{}], SigAlg=[{}], RelayState=[{}]", SAMLRequest, Signature, SigAlg, RelayState);

		@SuppressWarnings("rawtypes")
		SAMLMessageContext messageContext = samlMessageHandler.extractSAMLMessageContext(request, response, false);
		
		AbstractSAMLPrincipalFactory principalFactory = new LocalSAMLPrincipalFactory();

		SAMLPrincipal principal = principalFactory.createSAMLPrincipal(messageContext, authentication);
		
		samlMessageHandler.sendAuthnResponse(principal, response);
	}

}
