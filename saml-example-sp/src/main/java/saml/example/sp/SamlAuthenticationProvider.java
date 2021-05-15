package saml.example.sp;

import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.binding.decoding.HTTPPostDecoder;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public final class SamlAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSamlAssertionConsumer.class);

    private SimpleSamlAssertionConsumer assertionConsumer;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SamlPreAuthenticationToken preAuthenticationToken = (SamlPreAuthenticationToken) authentication;
        SamlContext samlContext = preAuthenticationToken.samlContext();
        @SuppressWarnings("rawtypes") SAMLMessageContext messageContext = null;
        try {
            messageContext = extractSAMLMessageContext(samlContext.request());
        } catch (MessageDecodingException | SecurityException e) {
            LOGGER.error("Failed to decode saml request", e);
            throw new InternalAuthenticationServiceException("Failed to decode saml request", e);
        }

        Response samlResponse = (Response) messageContext.getInboundSAMLMessage();
        String statusCode = samlResponse.getStatus().getStatusCode().getValue();
        if (!StatusCode.SUCCESS_URI.equals(statusCode)) {
            LOGGER.error("SAML login failed. status code[{}]", statusCode);
            throw new AuthenticationServiceException("SAML response status fail, code[" + statusCode + "]");
        }

        UserDetails userDetails = assertionConsumer.consume(samlResponse);
        LOGGER.info("Login user[{}]", userDetails);

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER"); // for test!!
        SamlAuthenticationToken resultToken = new SamlAuthenticationToken(userDetails.getUsername(), authorities);
        resultToken.setAuthenticated(true);
        resultToken.setDetails(userDetails);

        return resultToken;
    }

    @Override
    public boolean supports(Class<?> authenticationClass) {
        return authenticationClass.equals(SamlPreAuthenticationToken.class);
    }

    @SuppressWarnings("rawtypes")
    private SAMLMessageContext extractSAMLMessageContext(HttpServletRequest request)
            throws MessageDecodingException, SecurityException {
        BasicSAMLMessageContext messageContext = new BasicSAMLMessageContext();
        messageContext.setInboundMessageTransport(new HttpServletRequestAdapter(request));
        HTTPPostDecoder decoder = new HTTPPostDecoder();
        decoder.decode(messageContext);
        return messageContext;
    }

    public SamlAuthenticationProvider assertionConsumer(SimpleSamlAssertionConsumer assertionConsumer) {
        this.assertionConsumer = assertionConsumer;
        return this;
    }
}