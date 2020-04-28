package saml.example.idp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class LocalAuthenticationProvider implements AuthenticationProvider {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private Map<String, LocalUserDetails> localUsers;
	
	public LocalAuthenticationProvider() {
		initLocalUsers();
	}
	
	private void initLocalUsers() {
		localUsers = new HashMap<>();
		LocalUserDetails admin = LocalUserDetails.builder().department("Development Team")
													.displayName("시스템관리자")
													.mail("administrator@xxx.com")
													.userPrincipalName("admin")
													.authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))).build();
		LocalUserDetails user = LocalUserDetails.builder().department("HR Team")
													.displayName("일반사용자")
													.mail("user123@xxx.com")
													.userPrincipalName("user123")
													.authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))).build();
		//key - {ID}|{Password} 로컬 테스트용 ..
		localUsers.put("admin|admin123", admin);
		localUsers.put("user|user123", user);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String principal = authentication.getPrincipal().toString();
		String credential = authentication.getCredentials().toString();
		String key = principal + "|" + credential; 
		LOG.debug("LocalAuthenticationKey [{}]", key);
		
		Optional<LocalUserDetails> nullableUserDetails = Optional.ofNullable(localUsers.get(key));
		LocalUserDetails userDetails = nullableUserDetails.orElseThrow(() -> new IncorrectIdPwdAuthenticationException("Failed to authentication"));
		
		return new UserDetailsAuthenticationToken(userDetails, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
