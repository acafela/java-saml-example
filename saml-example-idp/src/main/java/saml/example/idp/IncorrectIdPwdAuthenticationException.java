package saml.example.idp;

import org.springframework.security.core.AuthenticationException;

@SuppressWarnings("serial")
public class IncorrectIdPwdAuthenticationException extends AuthenticationException {

	public IncorrectIdPwdAuthenticationException(String msg) {
		super(msg);
	}
}
