package saml.example.idp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.common.binding.SAMLMessageContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SsoController {

    private final SamlMessageHandler samlMessageHandler;

    public SsoController(SamlMessageHandler samlMessageHandler) {
        this.samlMessageHandler = samlMessageHandler;
    }

    @GetMapping("/sso")
    public void sso(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws Exception {
        @SuppressWarnings("rawtypes")
        SAMLMessageContext messageContext = samlMessageHandler.extractSAMLMessageContext(request, response);
        AbstractSamlPrincipalFactory principalFactory = new LocalSAMLPrincipalFactory();
        SamlPrincipal principal = principalFactory.createSAMLPrincipal(messageContext, authentication);
        samlMessageHandler.sendAuthnResponse(principal, response);
    }

    @GetMapping({"/login", "/"})
    public String login() {
        return "login";
    }
}