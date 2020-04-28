package saml.example.idp;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
public class UserDetailsAuthenticationToken extends AbstractAuthenticationToken{
	
	private UserDetails userDetails;
	
	public UserDetailsAuthenticationToken(UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		super.setAuthenticated(true);
		this.userDetails = userDetails;
	}

	@Override
	public Object getCredentials() {
		return this.userDetails.getPassword();
	}

	@Override
	public Object getPrincipal() {
		return this.userDetails;
	}
	
	@Override
	public Object getDetails() {
		return this.userDetails;
	}

}
