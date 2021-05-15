package saml.example.sp;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class SamlPreAuthenticationToken extends AbstractAuthenticationToken {

    private SamlContext samlContext;

    public SamlPreAuthenticationToken(SamlContext samlContext) {
        super(null);
        this.samlContext = samlContext;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    public SamlContext samlContext() {
        return samlContext;
    }

    public SamlPreAuthenticationToken samlContext(SamlContext samlContext) {
        this.samlContext = samlContext;
        return this;
    }
}
