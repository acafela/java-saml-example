package saml.example.sp;

import org.opensaml.common.SAMLException;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.filter.GenericFilterBean;
import saml.example.core.SAMLBuilder;
import saml.example.core.SAMLObjectUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SAML SSO Step 최초 진입점
 */
public class SamlSsoEntryPoint extends GenericFilterBean implements AuthenticationEntryPoint {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${sp.entity_id}")
    private String entityId;

    @Value("${sp.acs}")
    private String acs;

    @Value("${sp.single_sign_on_service_location}")
    private String ssoLocation;

    @Value("${sp.login_url}")
    private String loginUrl;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);

        if (!isLoginUrl(fi.getRequest())) {
            chain.doFilter(request, response);
            return;
        }

        commence(fi.getRequest(), fi.getResponse(), null);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        try {

            Issuer issuer = SAMLBuilder.buildIssuer(entityId);
            AuthnRequest authnRequest = SAMLBuilder.buildAuthnRequest(acs, SAMLConstants.SAML2_POST_BINDING_URI, issuer);

            LOG.debug("Created AuthnRequest[{}]", SAMLObjectUtils.samlObjectToString(authnRequest));

            BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = new BasicSAMLMessageContext<>();
            HttpServletResponseAdapter transport = new HttpServletResponseAdapter(response, false);
            context.setOutboundMessageTransport(transport);
            context.setPeerEntityEndpoint(getIDPEndpoint());
            context.setOutboundSAMLMessage(authnRequest);

            HTTPRedirectDeflateEncoder encoder = new HTTPRedirectDeflateEncoder();
            encoder.encode(context);

        } catch (MessageEncodingException e) {
            LOG.error("Error initializing SAML Request", e);
            throw new ServletException(e);
        }


    }

    private boolean isLoginUrl(HttpServletRequest request) {
        return request.getRequestURI().contains(loginUrl);
    }

    private Endpoint getIDPEndpoint() {
        Endpoint samlEndpoint = SAMLBuilder.buildSAMLObject(Endpoint.class, SingleSignOnService.DEFAULT_ELEMENT_NAME);
        samlEndpoint.setLocation(ssoLocation);
        return samlEndpoint;
    }

}
