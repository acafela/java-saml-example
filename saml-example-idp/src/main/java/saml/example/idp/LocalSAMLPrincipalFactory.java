package saml.example.idp;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import saml.example.core.SAMLAttribute;

public class LocalSAMLPrincipalFactory extends AbstractSAMLPrincipalFactory {

	@Override
	protected List<SAMLAttribute> createAttributes(Authentication authentication) {
		Object userDetails = authentication.getDetails();
		if (!(userDetails instanceof LocalUserDetails)) {
			throw new IllegalStateException("Authentication details should LocalUserDetails");
		}
		LocalUserDetails localUserDetails = (LocalUserDetails) userDetails;
		return Arrays.asList(
				new SAMLAttribute("User.Username", localUserDetails.getUsername()),
				new SAMLAttribute("User.Email", localUserDetails.getMail()),
				new SAMLAttribute("User.FederationIdentifier", localUserDetails.getUsername()),
				new SAMLAttribute("User.Department", localUserDetails.getDepartment()),
				new SAMLAttribute("User.DisplayName", localUserDetails.getDisplayName())
		);
	}
}
