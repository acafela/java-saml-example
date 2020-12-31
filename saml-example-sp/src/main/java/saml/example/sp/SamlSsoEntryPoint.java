package saml.example.sp;

import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.filter.GenericFilterBean;
import saml.example.core.SAMLUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.UUID;

/**
 * A SAML SSO entry point.
 * This class creates SAML requests and redirects requests to IdP.
 */
public class SamlSsoEntryPoint extends GenericFilterBean implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SamlSsoEntryPoint.class);

    @Value("${sp.entity_id}")
    private String entityId;

    @Value("${sp.acs}")
    private String acs;

    @Value("${sp.single_sign_on_service_location}")
    private String ssoLocation;

    @Value("${sp.login_url}")
    private String loginUrl;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        if (!isLoginUrl(fi.getRequest())) {
            chain.doFilter(request, response);
            return;
        }
        commence(fi.getRequest(), fi.getResponse(), null);
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws ServletException {
        try {
            BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = new BasicSAMLMessageContext<>();
            HttpServletResponseAdapter transport = new HttpServletResponseAdapter(response, false);
            context.setOutboundMessageTransport(transport);
            context.setPeerEntityEndpoint(getIDPEndpoint());
            Issuer issuer = buildIssuer(entityId);
            AuthnRequest authnRequest = buildAuthnRequest(acs, SAMLConstants.SAML2_POST_BINDING_URI, issuer);
            LOGGER.debug("Created AuthnRequest[{}]", SAMLUtil.samlObjectToString(authnRequest));
            context.setOutboundSAMLMessage(authnRequest);

            HTTPRedirectDeflateEncoder encoder = new HTTPRedirectDeflateEncoder();
            encoder.encode(context);
        } catch (MessageEncodingException e) {
            LOGGER.error("Error initializing SAML Request", e);
            throw new ServletException(e);
        }
    }

    private boolean isLoginUrl(HttpServletRequest request) {
        return request.getRequestURI().contains(loginUrl);
    }

    @SuppressWarnings("unchecked")
    private <T> T buildSAMLObject(final Class<T> objectClass, QName qName) {
        XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
        return (T) builderFactory.getBuilder(qName).buildObject(qName);
    }

    private AuthnRequest buildAuthnRequest(String acsUrl, String protocolBinding, Issuer issuer) {
        AuthnRequest authnRequest = buildSAMLObject(AuthnRequest.class, AuthnRequest.DEFAULT_ELEMENT_NAME);
        authnRequest.setIsPassive(true);
        authnRequest.setVersion(SAMLVersion.VERSION_20);
        authnRequest.setAssertionConsumerServiceURL(acsUrl);
        authnRequest.setProtocolBinding(protocolBinding);
        authnRequest.setIssuer(issuer);
        authnRequest.setIssueInstant(new DateTime());
        authnRequest.setID(UUID.randomUUID().toString());
        return authnRequest;
    }

    private Issuer buildIssuer(String issuingEntityName) {
        Issuer issuer = buildSAMLObject(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue(issuingEntityName);
        issuer.setFormat(NameIDType.ENTITY);
        return issuer;
    }

    private Endpoint getIDPEndpoint() {
        Endpoint samlEndpoint = buildSAMLObject(Endpoint.class, SingleSignOnService.DEFAULT_ELEMENT_NAME);
        samlEndpoint.setLocation(ssoLocation);
        return samlEndpoint;
    }
}