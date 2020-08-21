package saml.example.sp;

import org.apache.velocity.app.VelocityEngine;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.binding.decoding.HTTPPostDecoder;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import saml.example.core.SAMLBuilder;
import saml.example.core.SAMLObjectUtils;
import saml.example.core.SAMLPrincipal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.CertificateException;

@Controller
public class SamlBindingContoller {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	SpAuthenticationProvider spAuthenticationProvider;

	@Autowired
	SpAuthenticationSuccessHandler spAuthenticationSuccessHandler;

	@PostMapping("/acs")
	public void acs(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "SAMLResponse", required = true) String _samlResponse)
			throws MessageDecodingException, SecurityException, CertificateException, ValidationException, IOException, ServletException {

		LOG.debug("SAMLResponse[{}]", _samlResponse);

		SAMLMessageContext messageContext = extractSAMLMessageContext(request);
		Response samlResponse = (Response) messageContext.getInboundSAMLMessage();

		String statusCode = samlResponse.getStatus().getStatusCode().getValue();
		if (!StatusCode.SUCCESS_URI.equals(statusCode)) {
			LOG.error("SAML login failed. status code[{}]", statusCode);
			throw new RuntimeException("SAML response status fail, code[" + statusCode + "]");

		} else {
			AssertionConsumer consumer = new AssertionConsumer();
			SpUser spUser = consumer.consume(samlResponse);
			LOG.info("Login user[{}]", spUser);

			Authentication authentication = spAuthenticationProvider.provideAuthentication(spUser);
			spAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
		}
	}

	private SAMLMessageContext extractSAMLMessageContext(HttpServletRequest request) throws MessageDecodingException, SecurityException {
	    BasicSAMLMessageContext messageContext = new BasicSAMLMessageContext();
	    messageContext.setInboundMessageTransport(new HttpServletRequestAdapter(request));
	    HTTPPostDecoder decoder = new HTTPPostDecoder();
	    decoder.decode(messageContext);
	    return messageContext;
	}

}
