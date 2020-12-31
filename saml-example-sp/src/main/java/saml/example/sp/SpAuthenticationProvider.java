package saml.example.sp;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
public class SpAuthenticationProvider {

    public Authentication provideAuthentication(Principal principal){
        List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("ROLE_USER");
        Authentication authentication = new RememberMeAuthenticationToken(principal.getName(), principal, roles);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}