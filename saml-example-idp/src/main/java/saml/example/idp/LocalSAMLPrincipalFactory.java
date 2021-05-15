package saml.example.idp;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.Authentication;

public class LocalSAMLPrincipalFactory extends AbstractSamlPrincipalFactory {

    @Override
    protected List<SamlAttribute> createAttributes(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof LocalUserPrincipal)) {
            throw new IllegalStateException("Authentication details should LocalUserPrincipal");
        }
        LocalUserPrincipal localUserPrincipal = (LocalUserPrincipal) principal;
        return Arrays.asList(
                new SamlAttribute("User.Username", localUserPrincipal.getName()),
                new SamlAttribute("User.Email", localUserPrincipal.getMail()),
                new SamlAttribute("User.FederationIdentifier", localUserPrincipal.getName()),
                new SamlAttribute("User.Department", localUserPrincipal.getDepartment()),
                new SamlAttribute("User.DisplayName", localUserPrincipal.getDisplayName())
        );
    }
}