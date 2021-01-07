package saml.example.idp;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class LocalAuthenticationProvider implements AuthenticationProvider {
	
	private List<LocalUserPrincipal> localUsers;
	
	public LocalAuthenticationProvider(List<LocalUserPrincipal> localUsers) {
		this.localUsers = localUsers;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String principal = authentication.getPrincipal().toString();
		String credential = authentication.getCredentials().toString();
		LocalUserPrincipal userDetails = localUsers.stream()
												.filter(u -> u.getUserPrincipalName().equals(principal)
															&& u.getPassword().equals(credential))
												.findFirst()
												.orElseThrow(() -> new BadCredentialsException("Incorrect username or password"));
		return new RememberMeAuthenticationToken(
				userDetails.getName(),
				userDetails,
				Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
		);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}