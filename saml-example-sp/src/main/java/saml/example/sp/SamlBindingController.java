package saml.example.sp;

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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.CertificateException;

@Controller
public class SamlBindingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SamlBindingController.class);

	private final SpAuthenticationProvider spAuthenticationProvider;
	private final SpAuthenticationSuccessHandler spAuthenticationSuccessHandler;

	public SamlBindingController(SpAuthenticationProvider spAuthenticationProvider
			, SpAuthenticationSuccessHandler spAuthenticationSuccessHandler) {
		this.spAuthenticationProvider = spAuthenticationProvider;
		this.spAuthenticationSuccessHandler = spAuthenticationSuccessHandler;
	}

	@PostMapping("/acs")
	public void acs(HttpServletRequest request, HttpServletResponse response)
			throws MessageDecodingException, SecurityException, CertificateException, ValidationException, IOException {

		SAMLMessageContext messageContext = extractSAMLMessageContext(request);
		Response samlResponse = (Response) messageContext.getInboundSAMLMessage();

		String statusCode = samlResponse.getStatus().getStatusCode().getValue();
		if (!StatusCode.SUCCESS_URI.equals(statusCode)) {
			LOGGER.error("SAML login failed. status code[{}]", statusCode);
			throw new RuntimeException("SAML response status fail, code[" + statusCode + "]");
		}

		AssertionConsumer consumer = new AssertionConsumer();
		SpUser spUser = consumer.consume(samlResponse);
		LOGGER.info("Login user[{}]", spUser);

		Authentication authentication = spAuthenticationProvider.provideAuthentication(spUser);
		spAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
	}

	private SAMLMessageContext extractSAMLMessageContext(HttpServletRequest request)
			throws MessageDecodingException, SecurityException {
		BasicSAMLMessageContext messageContext = new BasicSAMLMessageContext();
		messageContext.setInboundMessageTransport(new HttpServletRequestAdapter(request));
		HTTPPostDecoder decoder = new HTTPPostDecoder();
		decoder.decode(messageContext);
		return messageContext;
	}
}