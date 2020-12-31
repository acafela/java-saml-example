package saml.example.idp;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class LocalAuthenticationProvider implements AuthenticationProvider {
	
	private List<LocalUserDetails> localUsers;
	
	public LocalAuthenticationProvider(List<LocalUserDetails> localUsers) {
		this.localUsers = localUsers;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String principal = authentication.getPrincipal().toString();
		String credential = authentication.getCredentials().toString();
		LocalUserDetails userDetails = localUsers.stream()
												.filter(u -> u.getUserPrincipalName().equals(principal)
															&& u.getPassword().equals(credential))
												.findFirst()
												.orElseThrow(() -> new BadCredentialsException("Incorrect username or password"));
		return new UserDetailsAuthenticationToken(userDetails, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}