package saml.example.idp;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.Authentication;

public class LocalSAMLPrincipalFactory extends AbstractSAMLPrincipalFactory {

	@Override
	protected List<SAMLAttribute> createAttributes(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		if (!(principal instanceof LocalUserPrincipal)) {
			throw new IllegalStateException("Authentication details should LocalUserPrincipal");
		}
		LocalUserPrincipal localUserPrincipal = (LocalUserPrincipal) principal;
		return Arrays.asList(
				new SAMLAttribute("User.Username", localUserPrincipal.getName()),
				new SAMLAttribute("User.Email", localUserPrincipal.getMail()),
				new SAMLAttribute("User.FederationIdentifier", localUserPrincipal.getName()),
				new SAMLAttribute("User.Department", localUserPrincipal.getDepartment()),
				new SAMLAttribute("User.DisplayName", localUserPrincipal.getDisplayName())
		);
	}
}